# BarmerEats Firebase Backend

The backend lives in `functions/` and runs as Firebase Cloud Functions. The Android app is only the client.

## Firestore collections

- `users/{uid}` — role, name, phone, approved
- `users/{uid}/devices/{deviceId}` — FCM device tokens
- `restaurants/{restaurantId}` — approved restaurant catalog metadata
- `restaurants/{restaurantId}/menuItems/{menuItemId}` — authoritative prices/availability
- `orders/{orderId}` — server-created orders and payment state
- `deliveryTracking/{orderId}` — rider GPS coordinates
- `notifications/{notificationId}` — in-app notification history
- `kycDocuments/{documentId}` — KYC metadata; files belong in Storage

## Callable backend API

- `registerFcmToken`
- `createOrder`
- `transitionOrder`
- `createRazorpayOrder`
- `verifyRazorpayPayment`

## Security model

Orders are created by `createOrder` using the Admin SDK. Firestore client-side order creation is disabled. Prices, delivery fee and tax are calculated from trusted Firestore catalog data, not from the Android cart.

Razorpay secret credentials must be Firebase Functions secrets. Never put `RAZORPAY_KEY_SECRET` in the Android app or repository.

## Required Firebase configuration

1. Enable Firebase Authentication.
2. Enable Firestore and Storage.
3. Enable Cloud Functions.
4. Add the Android app with application ID `com.malaramofficial.barmereats` and place its generated `google-services.json` in `app/`.
5. Configure Functions secrets:
   - `RAZORPAY_KEY_ID`
   - `RAZORPAY_KEY_SECRET`
6. Deploy rules/indexes/functions from the repository with the Firebase CLI.

No real secrets belong in Git. The repository intentionally contains only code and configuration templates.
