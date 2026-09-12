package com.example.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Compatibility surface for the existing tracking UI.
 * These values are deliberately non-authoritative; real rider GPS will replace them
 * once the Android foreground location service is wired to deliveryTracking.
 */
private val riderLatitude = MutableStateFlow(25.7510)
private val riderLongitude = MutableStateFlow(71.3965)

val FoodDeliveryViewModel.riderSimulatedLat: StateFlow<Double>
    get() = riderLatitude

val FoodDeliveryViewModel.riderSimulatedLng: StateFlow<Double>
    get() = riderLongitude
