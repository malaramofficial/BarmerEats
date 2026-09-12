package com.example.data

import android.app.Activity
import android.content.Context
import com.google.firebase.functions.FirebaseFunctions
import com.razorpay.Checkout
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.lang.ref.WeakReference

enum class PaymentStatus { PENDING, AUTHORIZED, PAID, FAILED, REFUNDED, COD_PENDING, COD_COLLECTED }
sealed class PaymentResult { data class Success(val transactionId: String, val signature: String) : PaymentResult(); data class Error(val code: Int, val message: String) : PaymentResult() }
data class RazorpayOrder(val razorpayOrderId: String, val amountPaise: Long, val currency: String, val keyId: String)

class PaymentRepository(private val context: Context) {
    private val functions = FirebaseFunctions.getInstance()
    companion object { private var hostActivity: WeakReference<Activity>? = null; fun attachActivity(activity: Activity) { hostActivity = WeakReference(activity) } }

    suspend fun createRazorpayOrder(orderId: String): Result<RazorpayOrder> = runCatching {
        require(orderId.isNotBlank()) { "Order ID is required." }
        val result = functions.getHttpsCallable("createRazorpayOrder").call(mapOf("orderId" to orderId)).await()
        val data = result.data as? Map<*, *> ?: error("Invalid payment server response.")
        RazorpayOrder(data["razorpayOrderId"]?.toString() ?: error("Missing Razorpay order ID."), (data["amount"] as? Number)?.toLong() ?: error("Missing payment amount."), data["currency"]?.toString() ?: "INR", data["keyId"]?.toString() ?: error("Missing Razorpay public key."))
    }

    fun launchCheckout(paymentOrder: RazorpayOrder, customerName: String, email: String, phone: String): Result<Unit> = runCatching {
        val activity = hostActivity?.get() ?: error("Payment screen is not available.")
        launchCheckout(activity, paymentOrder, customerName, email, phone)
    }

    fun launchCheckout(activity: Activity, paymentOrder: RazorpayOrder, customerName: String, email: String, phone: String) {
        val checkout = Checkout()
        checkout.setKeyID(paymentOrder.keyId)
        val options = JSONObject().apply {
            put("key", paymentOrder.keyId); put("order_id", paymentOrder.razorpayOrderId); put("amount", paymentOrder.amountPaise); put("currency", paymentOrder.currency)
            put("name", "BarmerEats"); put("description", "BarmerEats food order")
            put("prefill", JSONObject().apply { put("name", customerName); put("email", email); put("contact", phone) })
            put("theme", JSONObject().apply { put("color", "#7A1F1F") })
        }
        checkout.open(activity, options)
    }

    suspend fun verifyRazorpayPayment(orderId: String, razorpayOrderId: String, razorpayPaymentId: String, signature: String): Result<Boolean> = runCatching {
        val result = functions.getHttpsCallable("verifyRazorpayPayment").call(mapOf("orderId" to orderId, "razorpayOrderId" to razorpayOrderId, "razorpayPaymentId" to razorpayPaymentId, "signature" to signature)).await()
        val data = result.data as? Map<*, *> ?: error("Invalid verification response.")
        data["verified"] == true
    }

    suspend fun initiateRazorpayPayment(amountInRupees: Double, orderId: String, customerEmail: String, customerPhone: String): PaymentResult =
        if (amountInRupees <= 0.0 || orderId.isBlank()) PaymentResult.Error(400, "Invalid payment amount or order ID.") else PaymentResult.Error(400, "Use the trusted Razorpay checkout flow.")

    fun verifySignatureOnServer(paymentId: String, rzpOrderId: String, signature: String): Boolean = false
}
