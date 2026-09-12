package com.example.viewmodel

import com.example.data.PaymentRepository
import com.example.data.AddressEntity

/** Keeps the existing checkout UI API while routing the actual order through the trusted backend. */
fun FoodDeliveryViewModel.checkout(address: AddressEntity, paymentMethod: String) {
    val activity = PaymentRepository.currentActivity()
    if (activity == null) return
    checkout(activity, address, paymentMethod)
}
