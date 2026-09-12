package com.example.data

import android.content.Context

enum class PaymentStatus {
    PENDING,
    AUTHORIZED,
    PAID,
    FAILED,
    REFUNDED,
    COD_PENDING,
    COD_COLLECTED
}

sealed class PaymentResult {
    data class Success(val transactionId: String, val signature: String) : PaymentResult()
    data class Error(val code: Int, val message: String) : PaymentResult()
}

class PaymentRepository(private val context: Context) {

    /**
     * Razorpay integration boundary.
     *
     * This method intentionally does NOT simulate a successful payment.
     * A production payment must be created/verified using a trusted backend
     * and the official Razorpay Android checkout flow.
     */
    suspend fun initiateRazorpayPayment(
        amountInRupees: Double,
        orderId: String,
        customerEmail: String,
        customerPhone: String
    ): PaymentResult {
        if (amountInRupees <= 0.0 || orderId.isBlank()) {
            return PaymentResult.Error(400, "Invalid payment amount or order ID.")
        }
        return PaymentResult.Error(
            503,
            "Online payment is not configured yet. Payment cannot be marked successful without trusted Razorpay verification."
        )
    }

    /**
     * Client code must never claim that a Razorpay signature is verified.
     * Verification belongs on a trusted server/Cloud Function using the
     * Razorpay secret key, which must never be bundled into the APK.
     */
    fun verifySignatureOnServer(paymentId: String, rzpOrderId: String, signature: String): Boolean {
        return false
    }
}
