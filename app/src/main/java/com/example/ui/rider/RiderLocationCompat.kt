package com.example.ui.rider

import com.example.viewmodel.FoodDeliveryViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Compatibility state for the existing rider map UI until real foreground GPS is wired. */
private val riderLatitude = MutableStateFlow(25.7510)
private val riderLongitude = MutableStateFlow(71.3965)

val FoodDeliveryViewModel.riderSimulatedLat: StateFlow<Double>
    get() = riderLatitude

val FoodDeliveryViewModel.riderSimulatedLng: StateFlow<Double>
    get() = riderLongitude
