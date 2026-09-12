import {createHmac} from "node:crypto";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import {defineSecret} from "firebase-functions/params";
import {initializeApp} from "firebase-admin/app";
import {FieldValue, getFirestore} from "firebase-admin/firestore";
import Razorpay from "razorpay";

initializeApp();
const db = getFirestore();
const razorpayKeyId = defineSecret("RAZORPAY_KEY_ID");
const razorpayKeySecret = defineSecret("RAZORPAY_KEY_SECRET");
const allowedPaymentMethods = new Set(["UPI", "CARD", "COD"]);
const transitions: Record<string, Set<string>> = {
  PLACED: new Set(["ACCEPTED", "REJECTED", "CANCELLED"]),
  ACCEPTED: new Set(["PREPARING"]), PREPARING: new Set(["READY"]), READY: new Set(["RIDER_ASSIGNED"]),
  RIDER_ASSIGNED: new Set(["PICKED_UP"]), PICKED_UP: new Set(["OUT_FOR_DELIVERY"]), OUT_FOR_DELIVERY: new Set(["DELIVERED"]),
};

async function getRole(uid: string): Promise<string> {
  const snap = await db.collection("users").doc(uid).get();
  if (!snap.exists) throw new HttpsError("permission-denied", "User profile is not provisioned.");
  return String(snap.data()?.role ?? "");
}

export const createOrder = onCall(async (request) => {
  if (!request.auth) throw new HttpsError("unauthenticated", "Sign in required.");
  const data = request.data as {restaurantId?: string; items?: Array<{menuItemId?: string; quantity?: number}>; deliveryAddress?: {addressLine?: string; lat?: number; lng?: number}; paymentMethod?: string};
  const restaurantId = data.restaurantId?.trim();
  const items = data.items ?? [];
  const paymentMethod = data.paymentMethod?.toUpperCase();
  if (!restaurantId || items.length === 0 || !allowedPaymentMethods.has(paymentMethod ?? "")) throw new HttpsError("invalid-argument", "Invalid restaurant, cart or payment method.");
  if (await getRole(request.auth.uid) !== "CUSTOMER") throw new HttpsError("permission-denied", "Only customers can create orders.");

  const restaurantSnap = await db.collection("restaurants").doc(restaurantId).get();
  if (!restaurantSnap.exists) throw new HttpsError("not-found", "Restaurant not found.");
  const restaurant = restaurantSnap.data()!;
  if (restaurant.isOpen !== true || restaurant.approved !== true) throw new HttpsError("failed-precondition", "Restaurant is not currently accepting orders.");

  const uniqueIds = new Set<string>();
  for (const item of items) {
    if (!item.menuItemId || !Number.isInteger(item.quantity) || item.quantity! < 1 || item.quantity! > 20) throw new HttpsError("invalid-argument", "Invalid cart item quantity.");
    uniqueIds.add(item.menuItemId);
  }
  const menuRefs = [...uniqueIds].map((id) => restaurantSnap.ref.collection("menuItems").doc(id));
  const menuDocs = await db.getAll(...menuRefs);
  const menu = new Map(menuDocs.filter((d) => d.exists).map((d) => [d.id, d.data()!]));
  if (menu.size !== uniqueIds.size) throw new HttpsError("failed-precondition", "One or more menu items are unavailable.");

  let subtotal = 0;
  const orderItems: Array<Record<string, unknown>> = [];
  for (const item of items) {
    const menuItem = menu.get(item.menuItemId!);
    if (!menuItem || menuItem.isAvailable !== true) throw new HttpsError("failed-precondition", "A selected item is unavailable.");
    const price = Number(menuItem.price);
    const quantity = item.quantity!;
    if (!Number.isFinite(price) || price < 0) throw new HttpsError("failed-precondition", "Invalid menu price.");
    subtotal += price * quantity;
    orderItems.push({menuItemId: item.menuItemId, name: String(menuItem.name ?? "Item"), price, quantity});
  }
  const deliveryFee = Math.max(0, Number(restaurant.deliveryFee ?? 40));
  const taxAmount = Math.round(subtotal * 0.05 * 100) / 100;
  const totalAmount = subtotal + deliveryFee + taxAmount;
  const address = data.deliveryAddress ?? {};
  if (!address.addressLine || typeof address.lat !== "number" || typeof address.lng !== "number") throw new HttpsError("invalid-argument", "A valid delivery address is required.");
  const restaurantOwnerId = String(restaurant.ownerId ?? "");
  if (!restaurantOwnerId) throw new HttpsError("failed-precondition", "Restaurant owner is not configured.");

  const orderRef = db.collection("orders").doc();
  await orderRef.set({customerId: request.auth.uid, customerName: String(request.auth.token.name ?? "Customer"), restaurantId, restaurantName: String(restaurant.name ?? "Restaurant"), restaurantOwnerId, status: "PLACED", paymentMethod, paymentStatus: paymentMethod === "COD" ? "COD_PENDING" : "PENDING", subtotal, deliveryFee, taxAmount, totalAmount, deliveryAddress: address.addressLine, deliveryLat: address.lat, deliveryLng: address.lng, items: orderItems, createdAt: FieldValue.serverTimestamp(), updatedAt: FieldValue.serverTimestamp()});
  return {orderId: orderRef.id, totalAmount, currency: "INR"};
});

export const transitionOrder = onCall(async (request) => {
  if (!request.auth) throw new HttpsError("unauthenticated", "Sign in required.");
  const orderId = String(request.data?.orderId ?? "").trim();
  const nextStatus = String(request.data?.nextStatus ?? "").trim();
  if (!orderId || !nextStatus) throw new HttpsError("invalid-argument", "Order ID and next status are required.");
  const orderRef = db.collection("orders").doc(orderId);
  const orderSnap = await orderRef.get();
  if (!orderSnap.exists) throw new HttpsError("not-found", "Order not found.");
  const order = orderSnap.data()!;
  const currentStatus = String(order.status ?? "");
  if (!transitions[currentStatus]?.has(nextStatus)) throw new HttpsError("failed-precondition", `Invalid transition ${currentStatus} -> ${nextStatus}.`);
  const role = await getRole(request.auth.uid);
  const uid = request.auth.uid;
  const customerAction = nextStatus === "CANCELLED" && role === "CUSTOMER" && order.customerId === uid;
  const restaurantAction = ["ACCEPTED", "REJECTED", "PREPARING", "READY"].includes(nextStatus) && role === "RESTAURANT" && order.restaurantOwnerId === uid;
  const riderAction = ["RIDER_ASSIGNED", "PICKED_UP", "OUT_FOR_DELIVERY", "DELIVERED"].includes(nextStatus) && role === "RIDER" && (order.riderId === uid || nextStatus === "RIDER_ASSIGNED");
  if (!customerAction && !restaurantAction && !riderAction && role !== "ADMIN") throw new HttpsError("permission-denied", "You are not allowed to perform this order transition.");
  const update: Record<string, unknown> = {status: nextStatus, updatedAt: FieldValue.serverTimestamp()};
  if (nextStatus === "RIDER_ASSIGNED") {
    const riderId = String(request.data?.riderId ?? "").trim();
    if (!riderId) throw new HttpsError("invalid-argument", "Rider ID is required for assignment.");
    update.riderId = riderId;
  }
  await orderRef.update(update);
  return {updated: true, orderId, status: nextStatus};
});

export const createRazorpayOrder = onCall({secrets: [razorpayKeyId, razorpayKeySecret]}, async (request) => {
  if (!request.auth) throw new HttpsError("unauthenticated", "Sign in required.");
  const orderId = String(request.data?.orderId ?? "").trim();
  if (!orderId) throw new HttpsError("invalid-argument", "Order ID is required.");
  const orderSnap = await db.collection("orders").doc(orderId).get();
  if (!orderSnap.exists) throw new HttpsError("not-found", "Order not found.");
  const order = orderSnap.data()!;
  if (order.customerId !== request.auth.uid) throw new HttpsError("permission-denied", "Order access denied.");
  if (order.paymentMethod === "COD") throw new HttpsError("failed-precondition", "COD orders do not need Razorpay.");
  if (order.paymentStatus !== "PENDING") throw new HttpsError("failed-precondition", "Payment is not pending.");
  const amount = Number(order.totalAmount);
  if (!Number.isFinite(amount) || amount <= 0) throw new HttpsError("failed-precondition", "Invalid authoritative order amount.");
  const razorpay = new Razorpay({key_id: razorpayKeyId.value(), key_secret: razorpayKeySecret.value()});
  const paymentOrder = await razorpay.orders.create({amount: Math.round(amount * 100), currency: "INR", receipt: orderId, notes: {barmerEatsOrderId: orderId, customerId: request.auth.uid}});
  await orderSnap.ref.update({razorpayOrderId: paymentOrder.id, updatedAt: FieldValue.serverTimestamp()});
  return {razorpayOrderId: paymentOrder.id, amount: paymentOrder.amount, currency: paymentOrder.currency, keyId: razorpayKeyId.value()};
});

export const verifyRazorpayPayment = onCall({secrets: [razorpayKeySecret]}, async (request) => {
  if (!request.auth) throw new HttpsError("unauthenticated", "Sign in required.");
  const orderId = String(request.data?.orderId ?? "").trim();
  const razorpayOrderId = String(request.data?.razorpayOrderId ?? "").trim();
  const razorpayPaymentId = String(request.data?.razorpayPaymentId ?? "").trim();
  const signature = String(request.data?.signature ?? "").trim();
  if (!orderId || !razorpayOrderId || !razorpayPaymentId || !signature) throw new HttpsError("invalid-argument", "Incomplete Razorpay payment response.");
  const orderRef = db.collection("orders").doc(orderId);
  const orderSnap = await orderRef.get();
  if (!orderSnap.exists) throw new HttpsError("not-found", "Order not found.");
  const order = orderSnap.data()!;
  if (order.customerId !== request.auth.uid) throw new HttpsError("permission-denied", "Order access denied.");
  if (order.paymentStatus === "PAID") return {verified: true, idempotent: true};
  if (order.razorpayOrderId !== razorpayOrderId) throw new HttpsError("failed-precondition", "Razorpay order does not match BarmerEats order.");
  const expected = createHmac("sha256", razorpayKeySecret.value()).update(`${razorpayOrderId}|${razorpayPaymentId}`).digest("hex");
  if (expected !== signature) throw new HttpsError("permission-denied", "Payment signature verification failed.");
  await db.runTransaction(async (tx) => {
    const latest = await tx.get(orderRef);
    if (latest.data()?.paymentStatus !== "PAID") tx.update(orderRef, {paymentStatus: "PAID", razorpayPaymentId, paymentVerifiedAt: FieldValue.serverTimestamp(), updatedAt: FieldValue.serverTimestamp()});
  });
  return {verified: true, idempotent: false};
});
