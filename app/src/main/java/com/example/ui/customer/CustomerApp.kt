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
            NavigationBar(
                containerColor = SandyGold,
                contentColor = DeepCrimson
            ) {
                NavigationBarItem(
                    selected = currentScreen == "HOME",
                    onClick = { viewModel.navigateCustomer("HOME") },
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = currentScreen == "SEARCH",
                    onClick = { viewModel.navigateCustomer("SEARCH") },
                    icon = { Icon(Icons.Default.Search, "Search") },
                    label = { Text("Search", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("nav_search")
                )
                NavigationBarItem(
                    selected = currentScreen == "CART" || currentScreen == "CHECKOUT",
                    onClick = { viewModel.navigateCustomer("CART") },
                    icon = {
                        BadgedBox(badge = {
                            if (cart.isNotEmpty()) {
                                Badge(containerColor = DesertOrange) {
                                    Text(cart.values.sum().toString(), color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, "Cart")
                        }
                    },
                    label = { Text("Cart", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("nav_cart")
                )
                NavigationBarItem(
                    selected = currentScreen == "ORDERS" || currentScreen == "LIVE_TRACKING",
                    onClick = { viewModel.navigateCustomer("ORDERS") },
                    icon = { Icon(Icons.Default.ReceiptLong, "Orders") },
                    label = { Text("Orders", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("nav_orders")
                )
                NavigationBarItem(
                    selected = currentScreen == "PROFILE",
                    onClick = { viewModel.navigateCustomer("PROFILE") },
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DeepCrimson,
                        indicatorColor = DeepCrimson
                    ),
                    modifier = Modifier.testTag("nav_profile")
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Sunset Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                BarmerFortBackdrop(modifier = Modifier.fillMaxSize())
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        "Barmer Food Delivery",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = SandyGold
                    )
                    Text(
                        "Savor the rich heritage of western Rajasthan",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Horizontal Category Row
        item {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    "Cuisine Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepCrimson,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepCrimson,
                                selectedLabelColor = Color.White,
                                containerColor = SandyGold,
                                labelColor = CharcoalGray
                            )
                        )
                    }
                }
            }
        }

        // Restaurant Feed Title
        item {
            Text(
                "Popular Kitchens in Barmer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalGray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Restaurant List
        val filteredRestaurants = if (selectedCategory == "All") {
            restaurants
        } else {
            // Very simple filtering
            restaurants.filter { it.cuisineTags.contains(selectedCategory, ignoreCase = true) }
        }

        if (filteredRestaurants.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No kitchens available in this category.", color = Color.Gray)
                }
            }
        } else {
            items(filteredRestaurants) { rest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { viewModel.selectRestaurant(rest) }
                        .testTag("restaurant_card_${rest.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Drawing Illustration
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(SandyGold.copy(alpha = 0.6f))
                        ) {
                            FoodIllustration(
                                tag = rest.bannerImageTag,
                                modifier = Modifier
                                    .size(100.dp)
                                    .align(Alignment.Center)
                            )
                            if (!rest.isOpen) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("CLOSED NOW", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Info Section
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    rest.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalGray
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB703), modifier = Modifier.size(18.dp))
                                    Text(rest.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                rest.description,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rest.cuisineTags.split(",").forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .background(SandyGold, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(tag.trim(), fontSize = 11.sp, color = DeepCrimson)
                                    }
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
fun CustomerSearchScreen(viewModel: FoodDeliveryViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val restaurants by viewModel.restaurants.collectAsState()
    val allMenuProducts = remember { mutableStateListOf<Pair<MenuItemEntity, String>>() } // menu item and restaurant name

    // Gather all menu items once
    LaunchedEffect(restaurants) {
        allMenuProducts.clear()
        restaurants.forEach { rest ->
            viewModel.getMenuItemsForRestaurant(rest.id).collect { items ->
                items.forEach { item ->
                    if (allMenuProducts.none { it.first.id == item.id }) {
                        allMenuProducts.add(item to rest.name)
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("search_input"),
            placeholder = { Text("Search dishes, traditional thalis, sweets...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, "Clear")
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DeepCrimson,
                cursorColor = DeepCrimson
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (searchQuery.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Search,
                            "Search",
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "What are you craving today?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                val matches = allMenuProducts.filter {
                    it.first.name.contains(searchQuery, ignoreCase = true) ||
                    it.first.description.contains(searchQuery, ignoreCase = true) ||
                    it.second.contains(searchQuery, ignoreCase = true)
                }

                if (matches.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No match found for '$searchQuery'. Try 'Thali' or 'Kachori'", color = Color.Gray)
                        }
                    }
                } else {
                    items(matches) { (item, restName) ->
                        ListItem(
                            headlineContent = { Text(item.name, fontWeight = FontWeight.Bold) },
                            supportingContent = {
                                Column {
                                    Text(item.description, fontSize = 12.sp, maxLines = 1)
                                    Text("From: $restName", fontSize = 11.sp, color = DeepCrimson, fontWeight = FontWeight.Medium)
                                }
                            },
                            trailingContent = {
                                Button(
                                    onClick = { viewModel.addToCart(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DesertOrange),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("add_item_${item.id}")
                                ) {
                                    Text("Add ₹${item.price.toInt()}", fontSize = 11.sp, color = Color.White)
                                }
                            },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SandyGold)
                                ) {
                                    FoodIllustration(tag = item.name, modifier = Modifier.fillMaxSize().padding(4.dp))
                                }
                            },
                            modifier = Modifier.clickable {
                                // Find restaurant entity
                                val rest = restaurants.find { it.name == restName }
                                if (rest != null) {
                                    viewModel.selectRestaurant(rest)
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantDetailScreen(viewModel: FoodDeliveryViewModel) {
    val restaurant by viewModel.selectedRestaurant.collectAsState()
    val r = restaurant ?: return
    val items by viewModel.getMenuItemsForRestaurant(r.id).collectAsState(initial = emptyList())
    val cart by viewModel.cartItems.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Cover
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(SandyGold)
        ) {
            FoodIllustration(
                tag = r.bannerImageTag,
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.Center)
            )
            IconButton(
                onClick = { viewModel.navigateCustomer("HOME") },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        }

        // Shop Info
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(r.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB703), modifier = Modifier.size(20.dp))
                    Text(r.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(r.description, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.LocationOn, "Location", tint = DeepCrimson, modifier = Modifier.size(16.dp))
                Text(r.address, fontSize = 12.sp, color = CharcoalGray)
            }
        }

        HorizontalDivider(thickness = 8.dp, color = SandyGold.copy(alpha = 0.5f))

        // Menu list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Text(
                    "Our Signature Dishes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepCrimson,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (items.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No items on menu yet.", color = Color.Gray)
                    }
                }
            } else {
                items(items) { menuItem ->
                    val qtyInCart = cart[menuItem] ?: 0
                    ListItem(
                        headlineContent = { Text(menuItem.name, fontWeight = FontWeight.Bold) },
                        supportingContent = {
                            Column {
                                Text("₹${menuItem.price.toInt()}", fontWeight = FontWeight.Bold, color = DesertOrange)
                                Text(menuItem.description, fontSize = 12.sp, color = Color.Gray)
                            }
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SandyGold)
                            ) {
                                FoodIllustration(tag = menuItem.name, modifier = Modifier.fillMaxSize().padding(4.dp))
                            }
                        },
                        trailingContent = {
                            if (qtyInCart > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.removeFromCart(menuItem) },
                                        modifier = Modifier.size(32.dp).background(SandyGold, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Remove, "Remove", modifier = Modifier.size(16.dp))
                                    }
                                    Text(qtyInCart.toString(), fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { viewModel.addToCart(menuItem) },
                                        modifier = Modifier.size(32.dp).background(DeepCrimson, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Add, "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.addToCart(menuItem) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
                                    modifier = Modifier.testTag("add_item_${menuItem.id}")
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }

        // Bottom view Cart float
        if (cart.isNotEmpty()) {
            Surface(
                color = DeepCrimson,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateCustomer("CART") }
                    .testTag("cart_float_panel")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${cart.values.sum()} Item(s)", color = Color.White, fontSize = 12.sp)
                        Text("Total: ₹${cart.entries.sumOf { it.key.price * it.value }.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("View Cart", color = Color.White, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowForward, "Cart", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerCartScreen(viewModel: FoodDeliveryViewModel) {
    val cart by viewModel.cartItems.collectAsState()

    if (cart.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.ShoppingCart, "Empty Cart", modifier = Modifier.size(80.dp), tint = SandyGold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your cart is empty", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
            Text("Add delicious traditional dishes from Barmer kitchens", color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.navigateCustomer("HOME") },
                colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson)
            ) {
                Text("Browse Restaurants")
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "My Cart",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCrimson,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(cart.entries.toList()) { (item, qty) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
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
                            Text("₹${item.price.toInt()} each", fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.removeFromCart(item) },
                                modifier = Modifier.size(28.dp).background(SandyGold, CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, "Remove", modifier = Modifier.size(14.dp))
                            }
                            Text(qty.toString(), fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { viewModel.addToCart(item) },
                                modifier = Modifier.size(28.dp).background(DeepCrimson, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("₹${(item.price * qty).toInt()}", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider()
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                // Bill Details
                item {
                    val subtotal = cart.entries.sumOf { it.key.price * it.value }
                    val deliveryFee = 40.0
                    val tax = subtotal * 0.05
                    val grandTotal = subtotal + deliveryFee + tax

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SandyGold.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Bill Details", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepCrimson)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Item Subtotal", fontSize = 14.sp)
                                Text("₹${subtotal.toInt()}")
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Delivery Partner Fee", fontSize = 14.sp)
                                Text("₹${deliveryFee.toInt()}")
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Taxes & Charges (5%)", fontSize = 14.sp)
                                Text("₹${tax.toInt()}")
                            }
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("₹${grandTotal.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepCrimson)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.navigateCustomer("CHECKOUT") },
                colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
                    .testTag("checkout_button")
            ) {
                Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CustomerCheckoutScreen(viewModel: FoodDeliveryViewModel) {
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    var selectedAddress by remember { mutableStateOf<AddressEntity?>(null) }
    var selectedPayment by remember { mutableStateOf("UPI") }

    LaunchedEffect(savedAddresses) {
        if (savedAddresses.isNotEmpty() && selectedAddress == null) {
            selectedAddress = savedAddresses.first()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateCustomer("CART") }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text("Checkout Info", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepCrimson)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Address Selector
            item {
                Column {
                    Text("Select Delivery Address", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (savedAddresses.isEmpty()) {
                        Text("No saved addresses. Please configure in profile.", color = Color.Gray, fontSize = 13.sp)
                    } else {
                        savedAddresses.forEach { addr ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedAddress = addr },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedAddress?.id == addr.id) SandyGold else Color.White
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (selectedAddress?.id == addr.id) DeepCrimson else Color.LightGray
                                )
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (addr.title.contains("Home")) Icons.Default.Home else Icons.Default.Work,
                                        "Address Icon",
                                        tint = DeepCrimson
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(addr.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(addr.addressLine, fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Payment Methods
            item {
                Column {
                    Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("UPI (GooglePay/PhonePe)", "Debit/Credit Card", "Cash on Delivery").forEach { pMethod ->
                        val code = when {
                            pMethod.startsWith("UPI") -> "UPI"
                            pMethod.startsWith("Debit") -> "CARD"
                            else -> "CASH"
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedPayment = code },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPayment == code) SandyGold else Color.White
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (selectedPayment == code) DeepCrimson else Color.LightGray
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    when (code) {
                                        "UPI" -> Icons.Default.QrCode
                                        "CARD" -> Icons.Default.CreditCard
                                        else -> Icons.Default.Payments
                                    },
                                    "Payment Method",
                                    tint = DesertOrange
                                )
                                Text(pMethod, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        val addr = selectedAddress
        Button(
            onClick = {
                if (addr != null) {
                    viewModel.checkout(addr, selectedPayment)
                }
            },
            enabled = addr != null,
            colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
                .testTag("place_order_button")
        ) {
            Text("Place Order", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun CustomerTrackingScreen(viewModel: FoodDeliveryViewModel) {
    val activeOrder by viewModel.selectedOrderForTracking.collectAsState()
    val riderLat by viewModel.riderSimulatedLat.collectAsState()
    val riderLng by viewModel.riderSimulatedLng.collectAsState()

    val o = activeOrder

    if (o == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.PinDrop, "GPS", modifier = Modifier.size(64.dp), tint = SandyGold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No active delivery tracking", fontWeight = FontWeight.Bold, color = CharcoalGray)
            Button(
                onClick = { viewModel.navigateCustomer("ORDERS") },
                colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("View Past Orders")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Order #${o.id}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepCrimson)
                        Text("Total Bill: ₹${o.totalAmount.toInt()}", fontSize = 13.sp, color = Color.Gray)
                    }
                    Box(
                        modifier = Modifier
                            .background(SandyGold, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            o.status,
                            fontWeight = FontWeight.Bold,
                            color = DeepCrimson,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Map Simulation Card
            item {
                BarmerMapSimulation(
                    riderLat = riderLat,
                    riderLng = riderLng,
                    customerLat = o.deliveryLat,
                    customerLng = o.deliveryLng
                )
            }

            // Real-Time Delivery Progress Checklist
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SandyGold)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Live Delivery Status", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalGray)

                        val steps = listOf("PLACED", "ACCEPTED", "PREPARING", "READY", "PICKED_UP", "DELIVERED")
                        val currentIdx = steps.indexOf(o.status)

                        steps.forEachIndexed { idx, step ->
                            val isCompleted = idx <= currentIdx
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isCompleted) SageGreen else Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(Icons.Default.Check, "Done", tint = Color.White, modifier = Modifier.size(14.dp))
                                    } else {
                                        Text((idx + 1).toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = when (step) {
                                        "PLACED" -> "Order Sent to Kitchen"
                                        "ACCEPTED" -> "Kitchen Confirmed Order"
                                        "PREPARING" -> "Preparing Warm Delicacy"
                                        "READY" -> "Order Ready for Pickup"
                                        "PICKED_UP" -> "Out for Delivery (Scooter on the way!)"
                                        else -> "Arrived! Enjoy your food 🍛"
                                    },
                                    fontWeight = if (step == o.status) FontWeight.Bold else FontWeight.Normal,
                                    color = if (step == o.status) DeepCrimson else if (isCompleted) CharcoalGray else Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Rider Info Card
            if (o.riderId != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(SandyGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsBike, "Rider", tint = DeepCrimson)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(o.riderName ?: "Local Rider", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Barmer Super Dispatch Partner", fontSize = 12.sp, color = Color.Gray)
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
}

@Composable
fun CustomerOrdersScreen(viewModel: FoodDeliveryViewModel) {
    val orders by viewModel.allOrders.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "My Order History",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DeepCrimson,
            modifier = Modifier.padding(16.dp)
        )

        val customerOrders = orders.filter { it.customerId == "user_barmer" }

        if (customerOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No past orders found.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(customerOrders) { ord ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectRestaurant(RestaurantEntity(id = ord.restaurantId, name = ord.restaurantName, description = "", address = "", cuisineTags = "", rating = 4.5)) // dummy rest to navigate to tracking/details if active
                                viewModel.checkout(AddressEntity(customerId = "", title = "", addressLine = ord.deliveryAddress, lat = ord.deliveryLat, lng = ord.deliveryLng), "CASH") // mock trigger
                                viewModel.navigateCustomer("LIVE_TRACKING")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, SandyGold)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ord.restaurantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Box(
                                    modifier = Modifier
                                        .background(SandyGold, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(ord.status, fontSize = 11.sp, color = DeepCrimson, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Total: ₹${ord.totalAmount.toInt()} • Paid via ${ord.paymentMethod}", fontSize = 13.sp, color = Color.Gray)
                            Text("Address: ${ord.deliveryAddress}", fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (ord.status != "DELIVERED" && ord.status != "CANCELLED") {
                                Button(
                                    onClick = {
                                        // set selected tracking
                                        viewModel.checkout(AddressEntity(customerId = "", title = "", addressLine = ord.deliveryAddress, lat = ord.deliveryLat, lng = ord.deliveryLng), "CASH") // update tracking target
                                        viewModel.navigateCustomer("LIVE_TRACKING")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DesertOrange),
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Track Live Delivery", fontSize = 12.sp, color = Color.White)
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
fun CustomerProfileScreen(viewModel: FoodDeliveryViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            email = it.email
            phone = it.phone
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text("My Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepCrimson)
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DeepCrimson)
            )
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DeepCrimson)
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DeepCrimson)
            )
        }

        item {
            Button(
                onClick = { viewModel.updateCustomerProfile(name, email, phone) },
                colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile Changes")
            }
        }

        item {
            Text("My Saved Addresses", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CharcoalGray)
        }

        items(savedAddresses) { addr ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SandyGold)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (addr.title.contains("Home")) Icons.Default.Home else Icons.Default.Work,
                        "Address Icon",
                        tint = DeepCrimson
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(addr.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(addr.addressLine, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
