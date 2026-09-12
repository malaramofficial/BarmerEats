package com.example.ui.customer

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.viewmodel.FoodDeliveryViewModel

@Composable
fun CustomerApp(viewModel: FoodDeliveryViewModel) {
    val currentScreen by viewModel.customerScreen.collectAsState()
    val cart by viewModel.cartItems.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SandyGold, contentColor = DeepCrimson) {
                NavigationBarItem(selected = currentScreen == "HOME", onClick = { viewModel.navigateCustomer("HOME") }, icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = DeepCrimson, indicatorColor = DeepCrimson), modifier = Modifier.testTag("nav_home"))
                NavigationBarItem(selected = currentScreen == "SEARCH", onClick = { viewModel.navigateCustomer("SEARCH") }, icon = { Icon(Icons.Default.Search, "Search") }, label = { Text("Search", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = DeepCrimson, indicatorColor = DeepCrimson), modifier = Modifier.testTag("nav_search"))
                NavigationBarItem(selected = currentScreen == "CART" || currentScreen == "CHECKOUT", onClick = { viewModel.navigateCustomer("CART") }, icon = { BadgedBox(badge = { if (cart.isNotEmpty()) Badge(containerColor = DesertOrange) { Text(cart.values.sum().toString(), color = Color.White) } }) { Icon(Icons.Default.ShoppingCart, "Cart") } }, label = { Text("Cart", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = DeepCrimson, indicatorColor = DeepCrimson), modifier = Modifier.testTag("nav_cart"))
                NavigationBarItem(selected = currentScreen == "ORDERS" || currentScreen == "LIVE_TRACKING", onClick = { viewModel.navigateCustomer("ORDERS") }, icon = { Icon(Icons.Default.ReceiptLong, "Orders") }, label = { Text("Orders", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = DeepCrimson, indicatorColor = DeepCrimson), modifier = Modifier.testTag("nav_orders"))
                NavigationBarItem(selected = currentScreen == "PROFILE", onClick = { viewModel.navigateCustomer("PROFILE") }, icon = { Icon(Icons.Default.Person, "Profile") }, label = { Text("Profile", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = DeepCrimson, indicatorColor = DeepCrimson), modifier = Modifier.testTag("nav_profile"))
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().background(Color(0xFFFCFBF7)).padding(innerPadding)) {
            when (currentScreen) {
                "HOME" -> CustomerHomeScreen(viewModel)
                "SEARCH" -> CustomerSearchScreen(viewModel)
                "RESTAURANT" -> RestaurantDetailScreen(viewModel)
                "CART" -> CustomerCartScreen(viewModel)
                "CHECKOUT" -> CustomerCheckoutScreen(viewModel)
                "ORDERS" -> CustomerOrdersScreen(viewModel)
                "LIVE_TRACKING" -> CustomerTrackingScreen(viewModel)
                "PROFILE" -> CustomerProfileScreen(viewModel)
            }
        }
    }
}

@Composable
fun CustomerHomeScreen(viewModel: FoodDeliveryViewModel) {
    val restaurants by viewModel.restaurants.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Traditional", "Main Course", "Snacks", "Desserts", "Beverages")
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Box(Modifier.fillMaxWidth().height(200.dp)) {
                BarmerFortBackdrop(Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                    Text("Barmer Food Delivery", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SandyGold)
                    Text("Savor the rich heritage of western Rajasthan", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }
        item {
            Column(Modifier.padding(vertical = 16.dp)) {
                Text("Cuisine Categories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepCrimson, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(categories) { cat -> FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DeepCrimson, selectedLabelColor = Color.White, containerColor = SandyGold, labelColor = CharcoalGray)) }
                }
            }
        }
        item { Text("Popular Kitchens in Barmer", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CharcoalGray, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
        val filteredRestaurants = if (selectedCategory == "All") restaurants else restaurants.filter { it.cuisineTags.contains(selectedCategory, ignoreCase = true) }
        if (filteredRestaurants.isEmpty()) item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Text("No kitchens available in this category.", color = Color.Gray) } }
        else items(filteredRestaurants) { rest ->
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { viewModel.selectRestaurant(rest) }.testTag("restaurant_card_${rest.id}"), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column {
                    Box(Modifier.fillMaxWidth().height(120.dp).background(SandyGold.copy(alpha = 0.6f))) {
                        FoodIllustration(tag = rest.bannerImageTag, modifier = Modifier.size(100.dp).align(Alignment.Center))
                        if (!rest.isOpen) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) { Text("CLOSED NOW", color = Color.White, fontWeight = FontWeight.Bold) }
                    }
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(rest.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) { Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB703), modifier = Modifier.size(18.dp)); Text(rest.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        }
                        Spacer(Modifier.height(4.dp)); Text(rest.description, fontSize = 13.sp, color = Color.Gray, maxLines = 1); Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { rest.cuisineTags.split(",").forEach { tag -> Box(Modifier.background(SandyGold, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) { Text(tag.trim(), fontSize = 11.sp, color = DeepCrimson) } } }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerSearchScreen(viewModel: FoodDeliveryViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val restaurants by viewModel.restaurants.collectAsState()
    val allMenuProducts = remember { mutableStateListOf<Pair<MenuItemEntity, String>>() }
    LaunchedEffect(restaurants) {
        allMenuProducts.clear()
        restaurants.forEach { rest -> viewModel.getMenuItemsForRestaurant(rest.id).collect { items -> items.forEach { item -> if (allMenuProducts.none { it.first.id == item.id }) allMenuProducts.add(item to rest.name) } } }
    }
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("search_input"), placeholder = { Text("Search dishes, traditional thalis, sweets...") }, leadingIcon = { Icon(Icons.Default.Search, "Search") }, trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, "Clear") } }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DeepCrimson, cursorColor = DeepCrimson))
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            if (searchQuery.isEmpty()) item { Column(Modifier.fillMaxWidth().padding(top = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Search, "Search", Modifier.size(64.dp), tint = Color.LightGray); Spacer(Modifier.height(16.dp)); Text("What are you craving today?", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray) } }
            else {
                val matches = allMenuProducts.filter { it.first.name.contains(searchQuery, true) || it.first.description.contains(searchQuery, true) || it.second.contains(searchQuery, true) }
                if (matches.isEmpty()) item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Text("No match found for '$searchQuery'. Try 'Thali' or 'Kachori'", color = Color.Gray) } }
                else items(matches) { (item, restName) ->
                    ListItem(headlineContent = { Text(item.name, fontWeight = FontWeight.Bold) }, supportingContent = { Column { Text(item.description, fontSize = 12.sp, maxLines = 1); Text("From: $restName", fontSize = 11.sp, color = DeepCrimson, fontWeight = FontWeight.Medium) } }, trailingContent = { Button(onClick = { viewModel.addToCart(item) }, colors = ButtonDefaults.buttonColors(containerColor = DesertOrange), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.testTag("add_item_${item.id}")) { Text("Add ₹${item.price.toInt()}", fontSize = 11.sp, color = Color.White) } }, leadingContent = { Box(Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(SandyGold)) { FoodIllustration(tag = item.name, modifier = Modifier.fillMaxSize().padding(4.dp)) } }, modifier = Modifier.clickable { restaurants.find { it.name == restName }?.let { viewModel.selectRestaurant(it) } })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun RestaurantDetailScreen(viewModel: FoodDeliveryViewModel) {
    val restaurant by viewModel.selectedRestaurant.collectAsState(); val r = restaurant ?: return
    val items by viewModel.getMenuItemsForRestaurant(r.id).collectAsState(initial = emptyList()); val cart by viewModel.cartItems.collectAsState()
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().height(140.dp).background(SandyGold)) {
            FoodIllustration(tag = r.bannerImageTag, modifier = Modifier.size(110.dp).align(Alignment.Center))
            IconButton(onClick = { viewModel.navigateCustomer("HOME") }, modifier = Modifier.padding(16.dp).align(Alignment.TopStart).background(Color.White.copy(alpha = 0.8f), CircleShape)) { Icon(Icons.Default.ArrowBack, "Back") }
        }
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(r.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CharcoalGray); Text("★ ${r.rating}", fontWeight = FontWeight.Bold, color = DeepCrimson) }
            Spacer(Modifier.height(4.dp)); Text(r.description, color = Color.Gray); Spacer(Modifier.height(12.dp))
            if (!r.isOpen) Text("Currently closed", color = DesertOrange, fontWeight = FontWeight.Bold)
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(items) { item -> Card(Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) { ListItem(headlineContent = { Text(item.name, fontWeight = FontWeight.Bold) }, supportingContent = { Text(item.description, maxLines = 2) }, trailingContent = { Column(horizontalAlignment = Alignment.End) { Text("₹${item.price.toInt()}", fontWeight = FontWeight.Bold); Button(onClick = { viewModel.addToCart(item) }, enabled = r.isOpen && item.isAvailable) { Text("Add") } } }) } }
        }
        if (cart.isNotEmpty()) Button(onClick = { viewModel.navigateCustomer("CART") }, modifier = Modifier.fillMaxWidth().padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson)) { Text("View Cart (${cart.values.sum()})", color = Color.White) }
    }
}

@Composable
fun CustomerCartScreen(viewModel: FoodDeliveryViewModel) {
    val cart by viewModel.cartItems.collectAsState(); val restaurants by viewModel.restaurants.collectAsState()
    val entries = cart.entries.toList()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Cart", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCrimson); Spacer(Modifier.height(12.dp))
        if (entries.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.ShoppingCart, "Empty cart", Modifier.size(64.dp), tint = Color.LightGray); Text("Your cart is empty", color = Color.Gray); Button(onClick = { viewModel.navigateCustomer("HOME") }) { Text("Browse restaurants") } } }
        else {
            val selected = viewModel.selectedRestaurant.value ?: restaurants.find { it.id == entries.first().key.restaurantId }
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(entries) { (item, quantity) -> ListItem(headlineContent = { Text(item.name, fontWeight = FontWeight.Bold) }, supportingContent = { Text("₹${item.price.toInt()} × $quantity") }, trailingContent = { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { viewModel.updateCartQuantity(item, quantity - 1) }) { Icon(Icons.Default.Remove, "Decrease") }; Text(quantity.toString(), fontWeight = FontWeight.Bold); IconButton(onClick = { viewModel.updateCartQuantity(item, quantity + 1) }) { Icon(Icons.Default.Add, "Increase") } } }); HorizontalDivider() }
            }
            Button(onClick = { viewModel.navigateCustomer("CHECKOUT") }, modifier = Modifier.fillMaxWidth(), enabled = selected?.isOpen == true, colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson)) { Text(if (selected?.isOpen == true) "Proceed to Checkout" else "Restaurant closed", color = Color.White) }
        }
    }
}

@Composable
fun CustomerCheckoutScreen(viewModel: FoodDeliveryViewModel) {
    val cart by viewModel.cartItems.collectAsState(); var address by remember { mutableStateOf("") }; var payment by remember { mutableStateOf("CASH") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Checkout", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCrimson); Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, modifier = Modifier.fillMaxWidth().testTag("delivery_address"), label = { Text("Delivery address") }, minLines = 3)
        Spacer(Modifier.height(12.dp)); Text("Payment", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("CASH", "ONLINE").forEach { method -> FilterChip(selected = payment == method, onClick = { payment = method }, label = { Text(if (method == "CASH") "Cash on Delivery" else "UPI / Card") }) } }
        Spacer(Modifier.height(12.dp)); Text("Items: ${cart.values.sum()}", color = Color.Gray)
        Spacer(Modifier.weight(1f)); Button(onClick = { viewModel.checkout(address, payment) }, enabled = address.isNotBlank() && cart.isNotEmpty(), modifier = Modifier.fillMaxWidth().testTag("place_order_button"), colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson)) { Text("Place Order", color = Color.White) }
    }
}

@Composable
fun CustomerOrdersScreen(viewModel: FoodDeliveryViewModel) {
    val orders by viewModel.customerOrders.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Orders", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCrimson); Spacer(Modifier.height(12.dp))
        if (orders.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No orders yet.", color = Color.Gray) }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(orders) { order -> Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(16.dp)) { Text(order.restaurantName, fontWeight = FontWeight.Bold); Text("Order #${order.id}"); Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold); Text(order.status, color = DeepCrimson); if (order.status == "OUT_FOR_DELIVERY" || order.status == "RIDER_ASSIGNED" || order.status == "PICKED_UP") Button(onClick = { viewModel.navigateCustomer("LIVE_TRACKING") }) { Text("Track delivery") } } } } }
    }
}

@Composable
fun CustomerTrackingScreen(viewModel: FoodDeliveryViewModel) {
    val lat by viewModel.riderSimulatedLat.collectAsState(); val lng by viewModel.riderSimulatedLng.collectAsState()
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Live Delivery", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCrimson); Spacer(Modifier.height(16.dp));
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(20.dp)) { Icon(Icons.Default.Moped, "Rider", Modifier.size(64.dp), tint = DeepCrimson); Text("Rider location", fontWeight = FontWeight.Bold); Text("Latitude: %.5f".format(lat)); Text("Longitude: %.5f".format(lng)); Text("Live tracking will update from the assigned rider.", color = Color.Gray) } }
    }
}

@Composable
fun CustomerProfileScreen(viewModel: FoodDeliveryViewModel) {
    val user by viewModel.currentUser.collectAsState()
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCrimson); Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(20.dp)) { Text(user?.name ?: "Customer", fontSize = 20.sp, fontWeight = FontWeight.Bold); Text(user?.email ?: ""); Text("Role: ${user?.role ?: "CUSTOMER"}", color = DeepCrimson) } }
    }
}
