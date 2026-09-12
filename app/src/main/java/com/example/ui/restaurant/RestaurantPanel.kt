package com.example.ui.restaurant

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.viewmodel.FoodDeliveryViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun RestaurantPanel(viewModel: FoodDeliveryViewModel) {
    val currentScreen by viewModel.restaurantScreen.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SandyGold, contentColor = DeepCrimson) {
                NavigationBarItem(
                    selected = currentScreen == "DASHBOARD",
                    onClick = { viewModel.navigateRestaurant("DASHBOARD") },
                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                    label = { Text("Dashboard", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("rest_nav_dash")
                )
                NavigationBarItem(
                    selected = currentScreen == "ORDERS",
                    onClick = { viewModel.navigateRestaurant("ORDERS") },
                    icon = { Icon(Icons.Default.ReceiptLong, "Orders") },
                    label = { Text("Orders", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("rest_nav_orders")
                )
                NavigationBarItem(
                    selected = currentScreen == "MENU",
                    onClick = { viewModel.navigateRestaurant("MENU") },
                    icon = { Icon(Icons.Default.RestaurantMenu, "Menu") },
                    label = { Text("Menu", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("rest_nav_menu")
                )
                NavigationBarItem(
                    selected = currentScreen == "EARNINGS",
                    onClick = { viewModel.navigateRestaurant("EARNINGS") },
                    icon = { Icon(Icons.Default.MonetizationOn, "Earnings") },
                    label = { Text("Earnings", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("rest_nav_earnings")
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
                "DASHBOARD" -> RestaurantDashboardScreen(viewModel)
                "ORDERS" -> RestaurantOrdersScreen(viewModel)
                "MENU" -> RestaurantMenuScreen(viewModel)
                "EARNINGS" -> RestaurantEarningsScreen(viewModel)
            }
        }
    }
}

@Composable
fun RestaurantDashboardScreen(viewModel: FoodDeliveryViewModel) {
    val allOrders by viewModel.allOrders.collectAsState()
    val menus by viewModel.getMenuItemsForRestaurant(1).collectAsState(initial = emptyList())

    // Filter only orders assigned to Restaurant #1
    val myOrders = allOrders.filter { it.restaurantId == 1 }
    val pendingOrders = myOrders.filter { it.status == "PLACED" || it.status == "ACCEPTED" || it.status == "PREPARING" }
    val totalEarnings = myOrders.filter { it.status == "DELIVERED" }.sumOf { it.totalAmount - 40.0 } // 40 is delivery fee

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Desert Haveli Dashboard",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCrimson
            )
            Text("Station Road, Barmer", fontSize = 13.sp, color = Color.Gray)
        }

        // Stats Row
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
                        Text("Active Orders", fontSize = 12.sp, color = DeepCrimson, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            pendingOrders.size.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Sales", fontSize = 12.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "₹${totalEarnings.toInt()}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray
                        )
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE2F0D9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Menu Count", fontSize = 12.sp, color = SageGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            menus.size.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SandyGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Store Rating", fontSize = 12.sp, color = DesertOrange, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("4.8 ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
                            Icon(Icons.Default.Star, "Star", tint = Color(0xFFFFB703), modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }

        // Verification Status Alert
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SageGreen.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, SageGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Verified, "Verified", tint = SageGreen)
                    Column {
                        Text("FSSAI Registered Kitchen", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Your store is certified to prepare and dispatch hot traditional food across Barmer district.", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }

        item {
            Text("Latest Order Requests", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalGray)
        }

        if (pendingOrders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No active order requests at this moment.", color = Color.Gray)
                }
            }
        } else {
            items(pendingOrders) { ord ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SandyGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Order #${ord.id}", fontWeight = FontWeight.Bold, color = DeepCrimson)
                            Text("Amount: ₹${ord.totalAmount.toInt()}", fontSize = 12.sp, color = Color.Gray)
                            Text("Status: ${ord.status}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesertOrange)
                        }
                        IconButton(
                            onClick = { viewModel.navigateRestaurant("ORDERS") },
                            modifier = Modifier.background(DeepCrimson, CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowForward, "Detail", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantOrdersScreen(viewModel: FoodDeliveryViewModel) {
    val allOrders by viewModel.allOrders.collectAsState()
    val myOrders = allOrders.filter { it.restaurantId == 1 }

    var selectedTab by remember { mutableStateOf("ACTIVE") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Manage Kitchen Orders",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DeepCrimson,
            modifier = Modifier.padding(16.dp)
        )

        // Custom filter tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ACTIVE", "COMPLETED").forEach { tab ->
                Button(
                    onClick = { selectedTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == tab) DeepCrimson else SandyGold
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(tab, color = if (selectedTab == tab) Color.White else CharcoalGray)
                }
            }
        }

        val filteredList = if (selectedTab == "ACTIVE") {
            myOrders.filter { it.status != "DELIVERED" && it.status != "CANCELLED" }
        } else {
            myOrders.filter { it.status == "DELIVERED" || it.status == "CANCELLED" }
        }

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders in this section.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { ord ->
                    var showItems by remember { mutableStateOf(false) }
                    val orderItems by viewModel.getOrderItems(ord.id).collectAsState(initial = emptyList<OrderItemEntity>())

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
                                    Text("Order #${ord.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepCrimson)
                                    Text("To: ${ord.customerName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Address: ${ord.deliveryAddress}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(SandyGold, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(ord.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepCrimson)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(
                                onClick = { showItems = !showItems },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (showItems) "Hide Items" else "Show Items (${orderItems.size})", fontSize = 13.sp, color = DesertOrange)
                                    Icon(
                                        if (showItems) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        "Toggle",
                                        tint = DesertOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (showItems) {
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    orderItems.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("• ${item.itemName} x ${item.quantity}", fontSize = 13.sp)
                                            Text("₹${(item.price * item.quantity).toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive workflow controls
                            if (ord.status == "PLACED") {
                                Button(
                                    onClick = { viewModel.acceptOrder(ord) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Accept & Confirm Order", color = Color.White)
                                }
                            } else if (ord.status == "ACCEPTED") {
                                Button(
                                    onClick = { viewModel.prepareOrder(ord) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DesertOrange),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Start Preparation", color = Color.White)
                                }
                            } else if (ord.status == "PREPARING") {
                                Button(
                                    onClick = { viewModel.readyOrder(ord) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Mark Ready (Dispatch Rider)", color = Color.White)
                                }
                            } else {
                                Text(
                                    text = if (ord.status == "READY") "Awaiting Rider Pick Up..." else "Order Delivered Safely",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth()
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
fun RestaurantMenuScreen(viewModel: FoodDeliveryViewModel) {
    val menus by viewModel.getMenuItemsForRestaurant(1).collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Traditional") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Edit Kitchen Menu", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepCrimson)
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DeepCrimson,
                contentColor = Color.White,
                modifier = Modifier.size(44.dp).testTag("add_menu_item_fab")
            ) {
                Icon(Icons.Default.Add, "Add Item")
            }
        }

        if (menus.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your menu list is empty.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(menus) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, SandyGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SandyGold)
                            ) {
                                FoodIllustration(tag = item.name, modifier = Modifier.fillMaxSize().padding(4.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("₹${item.price.toInt()} • ${item.category}", fontSize = 12.sp, color = DesertOrange, fontWeight = FontWeight.Bold)
                                Text(item.description, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Dish", fontWeight = FontWeight.Bold, color = DeepCrimson) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Dish Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Categories Selector
                    Text("Category", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Traditional", "Main Course", "Snacks", "Desserts", "Beverages").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pr = price.toDoubleOrNull() ?: 0.0
                        if (name.isNotEmpty() && pr > 0.0) {
                            viewModel.addToCart(MenuItemEntity(id = 0, restaurantId = 1, name = name, description = desc, price = pr, category = category)) // VM insertion shortcut
                            showAddDialog = false
                            name = ""
                            price = ""
                            desc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun RestaurantEarningsScreen(viewModel: FoodDeliveryViewModel) {
    val allOrders by viewModel.allOrders.collectAsState()
    val myDelivered = allOrders.filter { it.restaurantId == 1 && it.status == "DELIVERED" }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Restaurant Earnings",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DeepCrimson
        )
        Text("Detailed sales analytics & historical payout ledger", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Simple Graphic Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = SandyGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Weekly Revenue Trend", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepCrimson)
                Spacer(modifier = Modifier.height(16.dp))

                // Procedural Bars
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val levels = listOf(0.4f, 0.2f, 0.6f, 0.8f, 0.3f, 0.9f, 0.7f)

                    days.forEachIndexed { idx, day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .fillMaxHeight(0.7f * levels[idx])
                                    .background(DeepCrimson, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(day, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Payout History", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalGray)

        if (myDelivered.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("No earnings logged yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(myDelivered) { ord ->
                    ListItem(
                        headlineContent = { Text("Order #${ord.id}", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Settled via UPI • ${ord.customerName}", fontSize = 12.sp) },
                        trailingContent = {
                            Text(
                                "₹${(ord.totalAmount - 40.0).toInt()}",
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
