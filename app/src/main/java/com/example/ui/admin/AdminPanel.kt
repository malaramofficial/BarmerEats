package com.example.ui.admin

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
fun AdminPanel(viewModel: FoodDeliveryViewModel) {
    val currentScreen by viewModel.adminScreen.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SandyGold, contentColor = DeepCrimson) {
                NavigationBarItem(
                    selected = currentScreen == "DASHBOARD",
                    onClick = { viewModel.navigateAdmin("DASHBOARD") },
                    icon = { Icon(Icons.Default.AdminPanelSettings, "Dashboard") },
                    label = { Text("Dashboard", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("admin_nav_dash")
                )
                NavigationBarItem(
                    selected = currentScreen == "RESTAURANTS" || currentScreen == "RIDERS",
                    onClick = { viewModel.navigateAdmin("RESTAURANTS") },
                    icon = { Icon(Icons.Default.SupervisedUserCircle, "Members") },
                    label = { Text("Directory", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("admin_nav_directory")
                )
                NavigationBarItem(
                    selected = currentScreen == "ORDERS",
                    onClick = { viewModel.navigateAdmin("ORDERS") },
                    icon = { Icon(Icons.Default.Assignment, "Orders") },
                    label = { Text("Orders", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("admin_nav_orders")
                )
                NavigationBarItem(
                    selected = currentScreen == "KYC" || currentScreen == "PAYMENTS",
                    onClick = { viewModel.navigateAdmin("KYC") },
                    icon = { Icon(Icons.Default.VerifiedUser, "KYC") },
                    label = { Text("KYC/Pay", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("admin_nav_kyc")
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
                "DASHBOARD" -> AdminDashboardScreen(viewModel)
                "RESTAURANTS" -> AdminRestaurantsScreen(viewModel)
                "ORDERS" -> AdminOrdersScreen(viewModel)
                "KYC" -> AdminKycScreen(viewModel)
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(viewModel: FoodDeliveryViewModel) {
    val restaurants by viewModel.restaurants.collectAsState()
    val allRiders by viewModel.allRiders.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val kycDocs by viewModel.allKycDocuments.collectAsState()

    val pendingKyc = kycDocs.filter { it.status == "PENDING" }.size
    val totalRevenue = allOrders.filter { it.status == "DELIVERED" }.sumOf { it.totalAmount }
    val commissionEarnings = totalRevenue * 0.15 // 15% platform commission

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Platform Central Operations",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCrimson
            )
            Text("Barmer HQ Core Administrative Hub", fontSize = 13.sp, color = Color.Gray)
        }

        // Stats blocks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SandyGold)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Gross GMV", fontSize = 12.sp, color = DeepCrimson, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹${totalRevenue.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE2F0D9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("15% Commission", fontSize = 12.sp, color = SageGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹${commissionEarnings.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active Kitchens", fontSize = 12.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(restaurants.size.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SandyGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pending KYC", fontSize = 12.sp, color = DesertOrange, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(pendingKyc.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Service Area Overview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SandyGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Defined Service Boundaries", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalGray)

                    listOf(
                        "Station Road (Zone A)" to "ACTIVE",
                        "Shastri Nagar (Zone B)" to "ACTIVE",
                        "Roy Colony (Zone C)" to "ACTIVE",
                        "Barmer Fort Hill (Zone D)" to "RESTRICTED (Steep Terrains)"
                    ).forEach { (zone, status) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.PinDrop, "Zone", tint = DesertOrange, modifier = Modifier.size(16.dp))
                                Text(zone, fontSize = 13.sp)
                            }
                            Text(
                                status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (status == "ACTIVE") SageGreen else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Analytics comparing categories
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SandyGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cuisine Sales Distribution", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepCrimson)
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Dal Baati (Traditional)" to 0.75f,
                            "Mawa Kachori (Sweets)" to 0.45f,
                            "Gatte ki Sabji (Veg)" to 0.55f,
                            "Masala Chai (Beverages)" to 0.25f
                        ).forEach { (cat, pct) ->
                            Column {
                                Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(SandyGold, RoundedCornerShape(4.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pct)
                                            .fillMaxHeight()
                                            .background(DesertOrange, RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminRestaurantsScreen(viewModel: FoodDeliveryViewModel) {
    val restaurants by viewModel.restaurants.collectAsState()
    val riders by viewModel.allRiders.collectAsState()

    var selectedTab by remember { mutableStateOf("KITCHENS") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Directory Directory",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DeepCrimson,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { selectedTab = "KITCHENS" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "KITCHENS") DeepCrimson else SandyGold
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Kitchens (${restaurants.size})", color = if (selectedTab == "KITCHENS") Color.White else CharcoalGray)
            }
            Button(
                onClick = { selectedTab = "RIDERS" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "RIDERS") DeepCrimson else SandyGold
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Riders (${riders.size})", color = if (selectedTab == "RIDERS") Color.White else CharcoalGray)
            }
        }

        if (selectedTab == "KITCHENS") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(restaurants) { r ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, SandyGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(r.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Address: ${r.address}", fontSize = 12.sp, color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB703), modifier = Modifier.size(16.dp))
                                    Text(r.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Switch(
                                checked = r.isOpen,
                                onCheckedChange = { viewModel.toggleRestaurantStatus(r) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SageGreen,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.LightGray
                                )
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(riders) { rd ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, SandyGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(rd.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Vehicle: ${rd.vehicleNumber}", fontSize = 12.sp, color = Color.Gray)
                                Text("Phone: ${rd.phone}", fontSize = 12.sp, color = CharcoalGray)
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (rd.status == "AVAILABLE") SageGreen.copy(alpha = 0.2f) else DesertOrange.copy(alpha = 0.2f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    rd.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rd.status == "AVAILABLE") SageGreen else DesertOrange
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrdersScreen(viewModel: FoodDeliveryViewModel) {
    val orders by viewModel.allOrders.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Global Order Monitoring",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DeepCrimson,
            modifier = Modifier.padding(16.dp)
        )

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders placed on platform yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { ord ->
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
                                Column {
                                    Text("Order #${ord.id}", fontWeight = FontWeight.Bold, color = DeepCrimson)
                                    Text("From: ${ord.restaurantName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("To: ${ord.customerName} (${ord.deliveryAddress})", fontSize = 12.sp, color = Color.Gray)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(SandyGold, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(ord.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepCrimson)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Grand Total: ₹${ord.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Payment: ${ord.paymentMethod}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminKycScreen(viewModel: FoodDeliveryViewModel) {
    val kycDocs by viewModel.allKycDocuments.collectAsState()
    val pendingDocs = kycDocs.filter { it.status == "PENDING" }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "KYC Approval Queue",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DeepCrimson,
            modifier = Modifier.padding(16.dp)
        )

        if (pendingDocs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.VerifiedUser, "No KYC", modifier = Modifier.size(64.dp), tint = SageGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("All documents verified! Zero backlog.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pendingDocs) { doc ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, SandyGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Owner Type: ${doc.ownerType}", fontWeight = FontWeight.Bold, color = DeepCrimson)
                                Text("Doc: ${doc.documentType}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text("ID Number: ${doc.documentNumber}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.approveKyc(doc) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Approve", color = Color.White)
                                }
                                Button(
                                    onClick = { viewModel.rejectKyc(doc, "Invalid License Image") },
                                    colors = ButtonDefaults.buttonColors(containerColor = DesertOrange),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reject", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
