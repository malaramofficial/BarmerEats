package com.example.ui.customer

import com.example.data.AddressEntity
import com.example.data.PaymentRepository
import com.example.viewmodel.FoodDeliveryViewModel

/** Compatibility overload for existing customer checkout calls. */
fun FoodDeliveryViewModel.checkout(address: AddressEntity, paymentMethod: String) {
    val activity = PaymentRepository.currentActivity() ?: return
    checkout(activity, address, paymentMethod)
}
