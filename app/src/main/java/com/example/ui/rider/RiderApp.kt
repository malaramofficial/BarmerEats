package com.example.ui.rider

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.viewmodel.FoodDeliveryViewModel

@Composable
fun RiderApp(viewModel: FoodDeliveryViewModel) {
    val currentScreen by viewModel.riderScreen.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()

    // Assuming Rider ID = 1 (Ramesh Kumar)
    val activeDelivery = allOrders.find { it.riderId == 1 && it.status == "PICKED_UP" }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SandyGold, contentColor = DeepCrimson) {
                NavigationBarItem(
                    selected = currentScreen == "AVAILABLE_ORDERS",
                    onClick = { viewModel.navigateRider("AVAILABLE_ORDERS") },
                    icon = { Icon(Icons.Default.ListAlt, "Available") },
                    label = { Text("Available", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("rider_nav_avail")
                )
                NavigationBarItem(
                    selected = currentScreen == "ACTIVE_DELIVERY" || currentScreen == "NAVIGATION",
                    onClick = {
                        if (activeDelivery != null) {
                            viewModel.navigateRider("ACTIVE_DELIVERY")
                        } else {
                            viewModel.navigateRider("AVAILABLE_ORDERS")
                        }
                    },
                    enabled = activeDelivery != null,
                    icon = {
                        BadgedBox(badge = {
                            if (activeDelivery != null) {
                                Badge(containerColor = DesertOrange) {
                                    Text("1", color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.DirectionsBike, "Active")
                        }
                    },
                    label = { Text("Active", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("rider_nav_active")
                )
                NavigationBarItem(
                    selected = currentScreen == "EARNINGS",
                    onClick = { viewModel.navigateRider("EARNINGS") },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, "Earnings") },
                    label = { Text("Earnings", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("rider_nav_earn")
                )
                NavigationBarItem(
                    selected = currentScreen == "PROFILE",
                    onClick = { viewModel.navigateRider("PROFILE") },
                    icon = { Icon(Icons.Default.AssignmentInd, "KYC") },
                    label = { Text("Profile/KYC", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("rider_nav_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFCFBF7))
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "AVAILABLE_ORDERS" -> RiderAvailableOrdersScreen(viewModel)
                "ACTIVE_DELIVERY" -> RiderActiveDeliveryScreen(viewModel, activeDelivery)
                "EARNINGS" -> RiderEarningsScreen(viewModel)
                "PROFILE" -> RiderProfileKycScreen(viewModel)
            }
        }
    }
}

@Composable
fun RiderAvailableOrdersScreen(viewModel: FoodDeliveryViewModel) {
    val allOrders by viewModel.allOrders.collectAsState()
    val currentRider by viewModel.currentRider.collectAsState()

    // Available order pools: Ready for pickup and not yet assigned
    val readyPool = allOrders.filter { (it.status == "READY" || it.status == "PLACED") && it.riderId == null }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Available Deliveries",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCrimson
            )
            Text("Instant dispatch requests in Barmer city limits", fontSize = 13.sp, color = Color.Gray)
        }

        if (currentRider?.status == "BUSY") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DesertOrange.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, DesertOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, "Busy", tint = DesertOrange)
                        Column {
                            Text("You are currently BUSY!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Complete your active delivery first to unlock new order pools.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        if (readyPool.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Moped, "No Orders", modifier = Modifier.size(64.dp), tint = SandyGold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Searching for new order dispatches...", color = Color.Gray)
                }
            }
        } else {
            items(readyPool) { ord ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SandyGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ord.restaurantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "Base Pay: ₹60",
                                fontWeight = FontWeight.Bold,
                                color = SageGreen,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Pick Up From: Station Road, Barmer", fontSize = 12.sp, color = Color.Gray)
                        Text("Deliver To: ${ord.deliveryAddress}", fontSize = 12.sp, color = CharcoalGray)
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                currentRider?.let { rider ->
                                    viewModel.pickUpOrder(ord, rider)
                                    viewModel.navigateRider("ACTIVE_DELIVERY")
                                }
                            },
                            enabled = currentRider?.status != "BUSY",
                            colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Accept Order & Start Pickup", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiderActiveDeliveryScreen(viewModel: FoodDeliveryViewModel, order: OrderEntity?) {
    val o = order ?: return
    val riderLat by viewModel.riderSimulatedLat.collectAsState()
    val riderLng by viewModel.riderSimulatedLng.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Active Delivery Mission",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCrimson
            )
            Text("Deliver hot & fresh to ${o.customerName}", fontSize = 13.sp, color = Color.Gray)
        }

        // Live map guidance
        item {
            Text("Live Route GPS Guidance", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            BarmerMapSimulation(
                riderLat = riderLat,
                riderLng = riderLng,
                customerLat = o.deliveryLat,
                customerLng = o.deliveryLng
            )
        }

        // Action HUD Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SandyGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Turn-by-Turn GPS HUD", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepCrimson)

                    // Procedural turn-by-turn guidance based on latitude progress
                    val progressFraction = (riderLat - 25.7535) / (o.deliveryLat - 25.7535)
                    val guidanceText = when {
                        progressFraction < 0.2 -> "Start: Pickup completed from Gandhi Chowk. Head south on Station Road."
                        progressFraction < 0.5 -> "Turn Left on Roy Colony Link Road. Keep steady speed (35 km/h)."
                        progressFraction < 0.8 -> "Approaching Shastri Nagar intersection. Head straight."
                        else -> "Arriving shortly at sun heights colony. Prepare to call customer."
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Navigation, "Nav Direction", tint = DesertOrange)
                        Text(guidanceText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CharcoalGray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Customer Payment", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = if (o.paymentMethod == "CASH") "COLLECT CASH: ₹${o.totalAmount.toInt()}" else "ONLINE PREPAID: ₹0",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (o.paymentMethod == "CASH") DeepCrimson else SageGreen
                            )
                        }

                        IconButton(
                            onClick = {}, // Mock call
                            modifier = Modifier.background(SageGreen, CircleShape)
                        ) {
                            Icon(Icons.Default.Phone, "Call", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiderEarningsScreen(viewModel: FoodDeliveryViewModel) {
    val allOrders by viewModel.allOrders.collectAsState()
    val riderDelivered = allOrders.filter { it.riderId == 1 && it.status == "DELIVERED" }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Rider Wallet & Earnings",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DeepCrimson
        )
        Text("Payout ledger of super-fast dispatches in Barmer", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Total balance card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SandyGold)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Total Wallet Balance", fontSize = 14.sp, color = DeepCrimson, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "₹${(riderDelivered.size * 60 + 180).toInt()}", // Base payout model + starting seed
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Delivered Missions: ${riderDelivered.size}", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Mission Delivery History", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalGray)

        if (riderDelivered.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("No deliveries logged yet in this cycle.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(riderDelivered) { ord ->
                    ListItem(
                        headlineContent = { Text("To: ${ord.customerName}", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Trip completed safely • ${ord.deliveryAddress}", fontSize = 11.sp) },
                        trailingContent = {
                            Text(
                                "₹60.00",
                                fontWeight = FontWeight.Bold,
                                color = SageGreen,
                                fontSize = 16.sp
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun RiderProfileKycScreen(viewModel: FoodDeliveryViewModel) {
    val currentRider by viewModel.currentRider.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text("Rider KYC & Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepCrimson)
            Text("Verification credentials for Barmer region", fontSize = 13.sp, color = Color.Gray)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SandyGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Personal Credentials", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepCrimson)

                    Text("Rider Name: ${currentRider?.name ?: "Ramesh Kumar"}", fontWeight = FontWeight.SemiBold)
                    Text("Vehicle Registration: ${currentRider?.vehicleNumber ?: "RJ-16-SA-1234"}", fontWeight = FontWeight.SemiBold)
                    Text("Driving License: ${currentRider?.licenseNumber ?: "RJ16/DL/2023-010"}", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (currentRider?.kycStatus == "APPROVED") SageGreen.copy(alpha = 0.15f) else DesertOrange.copy(alpha = 0.15f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (currentRider?.kycStatus == "APPROVED") SageGreen else DesertOrange
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (currentRider?.kycStatus == "APPROVED") Icons.Default.VerifiedUser else Icons.Default.HourglassEmpty,
                        "KYC Status",
                        tint = if (currentRider?.kycStatus == "APPROVED") SageGreen else DesertOrange
                    )
                    Column {
                        Text(
                            text = if (currentRider?.kycStatus == "APPROVED") "Aadhaar verified" else "Pending Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (currentRider?.kycStatus == "APPROVED") "Aadhaar Card documents successfully matched and verified." else "Admin is reviewing submitted driving license.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
