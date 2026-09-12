package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FoodDeliveryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FoodDeliveryRepository(database.foodDeliveryDao())

    // --- APP CONTROLS & NAVIGATION STATE ---
    private val _currentRole = MutableStateFlow("CUSTOMER") // CUSTOMER, RESTAURANT, RIDER, ADMIN
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    // Customer navigation sub-screens: "HOME", "SEARCH", "RESTAURANT", "CART", "CHECKOUT", "ORDERS", "LIVE_TRACKING", "PROFILE"
    private val _customerScreen = MutableStateFlow("HOME")
    val customerScreen: StateFlow<String> = _customerScreen.asStateFlow()

    // Restaurant sub-screens: "DASHBOARD", "ORDERS", "MENU", "EARNINGS", "PROFILE"
    private val _restaurantScreen = MutableStateFlow("DASHBOARD")
    val restaurantScreen: StateFlow<String> = _restaurantScreen.asStateFlow()

    // Rider sub-screens: "AVAILABLE_ORDERS", "ACTIVE_DELIVERY", "NAVIGATION", "EARNINGS", "PROFILE"
    private val _riderScreen = MutableStateFlow("AVAILABLE_ORDERS")
    val riderScreen: StateFlow<String> = _riderScreen.asStateFlow()

    // Admin sub-screens: "DASHBOARD", "RESTAURANTS", "RIDERS", "ORDERS", "KYC", "PAYMENTS", "SERVICE_AREA", "ANALYTICS"
    private val _adminScreen = MutableStateFlow("DASHBOARD")
    val adminScreen: StateFlow<String> = _adminScreen.asStateFlow()

    // Selected Entities
    private val _selectedRestaurant = MutableStateFlow<RestaurantEntity?>(null)
    val selectedRestaurant: StateFlow<RestaurantEntity?> = _selectedRestaurant.asStateFlow()

    private val _selectedOrderForTracking = MutableStateFlow<OrderEntity?>(null)
    val selectedOrderForTracking: StateFlow<OrderEntity?> = _selectedOrderForTracking.asStateFlow()

    // Dynamic Tracking Location for Rider (Simulated)
    private val _riderSimulatedLat = MutableStateFlow(25.7535)
    val riderSimulatedLat: StateFlow<Double> = _riderSimulatedLat.asStateFlow()

    private val _riderSimulatedLng = MutableStateFlow(71.3910)
    val riderSimulatedLng: StateFlow<Double> = _riderSimulatedLng.asStateFlow()

    // --- RECTIVE FLOWS FROM DATABASE ---
    val restaurants: StateFlow<List<RestaurantEntity>> = repository.allRestaurants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableOrders: StateFlow<List<OrderEntity>> = repository.availableOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRiders: StateFlow<List<RiderEntity>> = repository.allRiders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allKycDocuments: StateFlow<List<KycDocumentEntity>> = repository.allKycDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart State
    private val _cartItems = MutableStateFlow<Map<MenuItemEntity, Int>>(emptyMap())
    val cartItems: StateFlow<Map<MenuItemEntity, Int>> = _cartItems.asStateFlow()

    // Saved Addresses
    val savedAddresses: StateFlow<List<AddressEntity>> = repository.getAddressesForCustomer("user_barmer")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active User
    val currentUser: StateFlow<UserEntity?> = repository.getUserById("user_barmer")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current active restaurant owner / rider (using ID = 1 for simple single-device multi-role demo)
    val currentRider: StateFlow<RiderEntity?> = repository.getRiderById(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentRestaurant: StateFlow<RestaurantEntity?> = repository.getRestaurantById(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Notifications Flow
    val notifications: StateFlow<List<NotificationEntity>> = repository.getNotificationsForUser("user_barmer")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed initial data if database is empty
        viewModelScope.launch {
            repository.allRestaurants.first().let { list ->
                if (list.isEmpty()) {
                    seedDatabase()
                }
            }
        }
    }

    // --- ROLE SWAPPING ---
    fun switchRole(role: String) {
        _currentRole.value = role
    }

    fun navigateCustomer(screen: String) {
        _customerScreen.value = screen
    }

    fun navigateRestaurant(screen: String) {
        _restaurantScreen.value = screen
    }

    fun navigateRider(screen: String) {
        _riderScreen.value = screen
    }

    fun navigateAdmin(screen: String) {
        _adminScreen.value = screen
    }

    fun selectRestaurant(restaurant: RestaurantEntity) {
        _selectedRestaurant.value = restaurant
        _customerScreen.value = "RESTAURANT"
    }

    fun getMenuItemsForRestaurant(restaurantId: Int): Flow<List<MenuItemEntity>> {
        return repository.getMenuItemsForRestaurant(restaurantId)
    }

    fun getOrderItems(orderId: Int): Flow<List<OrderItemEntity>> {
        return repository.getOrderItems(orderId)
    }

    // --- CART MANIPULATION ---
    fun addToCart(item: MenuItemEntity) {
        val current = _cartItems.value.toMutableMap()
        current[item] = (current[item] ?: 0) + 1
        _cartItems.value = current
    }

    fun removeFromCart(item: MenuItemEntity) {
        val current = _cartItems.value.toMutableMap()
        val count = current[item] ?: 0
        if (count <= 1) {
            current.remove(item)
        } else {
            current[item] = count - 1
        }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    // --- ORDER PLACEMENT ---
    fun checkout(address: AddressEntity, paymentMethod: String) {
        viewModelScope.launch {
            val cart = _cartItems.value
            if (cart.isEmpty()) return@launch

            val totalAmount = cart.entries.sumOf { it.key.price * it.value } + 40.0 // 40 is standard delivery
            val rest = _selectedRestaurant.value ?: return@launch

            val order = OrderEntity(
                customerId = "user_barmer",
                customerName = "Malaram Choudhary",
                restaurantId = rest.id,
                restaurantName = rest.name,
                status = "PLACED",
                totalAmount = totalAmount,
                deliveryAddress = address.addressLine,
                deliveryLat = address.lat,
                deliveryLng = address.lng,
                paymentMethod = paymentMethod,
                paymentStatus = if (paymentMethod == "CASH") "PENDING" else "COMPLETED"
            )

            val orderId = repository.insertOrder(order).toInt()

            cart.forEach { (item, qty) ->
                repository.insertOrderItem(
                    OrderItemEntity(
                        orderId = orderId,
                        menuItemId = item.id,
                        itemName = item.name,
                        price = item.price,
                        quantity = qty
                    )
                )
            }

            // Create notification for customer
            repository.insertNotification(
                NotificationEntity(
                    userId = "user_barmer",
                    title = "Order Placed! 🍛",
                    message = "Your order with ${rest.name} has been placed successfully."
                )
            )

            // Create notification for Restaurant
            repository.insertNotification(
                NotificationEntity(
                    userId = "restaurant_${rest.id}",
                    title = "New Order Recieved!",
                    message = "New order (#$orderId) containing ${cart.size} items."
                )
            )

            // Auto select this order for customer live tracking
            val insertedOrder = order.copy(id = orderId)
            _selectedOrderForTracking.value = insertedOrder

            clearCart()
            _customerScreen.value = "LIVE_TRACKING"
        }
    }

    // --- ACTIONS BY ROLES ---

    // Restaurant: Accept Order
    fun acceptOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(status = "ACCEPTED")
            repository.updateOrder(updated)
            if (_selectedOrderForTracking.value?.id == order.id) {
                _selectedOrderForTracking.value = updated
            }
            repository.insertNotification(
                NotificationEntity(
                    userId = order.customerId,
                    title = "Order Accepted 👨‍🍳",
                    message = "${order.restaurantName} is preparing your food."
                )
            )
        }
    }

    // Restaurant: Preparing Order
    fun prepareOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(status = "PREPARING")
            repository.updateOrder(updated)
            if (_selectedOrderForTracking.value?.id == order.id) {
                _selectedOrderForTracking.value = updated
            }
        }
    }

    // Restaurant: Dispatch / Ready for Rider
    fun readyOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(status = "READY")
            repository.updateOrder(updated)
            if (_selectedOrderForTracking.value?.id == order.id) {
                _selectedOrderForTracking.value = updated
            }
            repository.insertNotification(
                NotificationEntity(
                    userId = order.customerId,
                    title = "Order Ready! 🛵",
                    message = "Your meal is ready for pickup."
                )
            )
        }
    }

    // Rider: Accept / Pick up delivery
    fun pickUpOrder(order: OrderEntity, rider: RiderEntity) {
        viewModelScope.launch {
            val updatedOrder = order.copy(
                status = "PICKED_UP",
                riderId = rider.id,
                riderName = rider.name
            )
            repository.updateOrder(updatedOrder)
            
            // Set rider status to BUSY
            repository.updateRider(rider.copy(status = "BUSY"))

            if (_selectedOrderForTracking.value?.id == order.id) {
                _selectedOrderForTracking.value = updatedOrder
            }

            repository.insertNotification(
                NotificationEntity(
                    userId = order.customerId,
                    title = "Out for Delivery! 🚀",
                    message = "Rider ${rider.name} is on the way to your address."
                )
            )

            // Start rider live movement simulation
            simulateRiderMovement(updatedOrder, rider)
        }
    }

    // Simulating GPS location movement in Barmer coordinates
    private fun simulateRiderMovement(order: OrderEntity, rider: RiderEntity) {
        viewModelScope.launch {
            // Restaurant location (assume coordinates Gandhi Chowk: 25.7535, 71.3910)
            val startLat = 25.7535
            val startLng = 71.3910

            // Customer location
            val endLat = order.deliveryLat
            val endLng = order.deliveryLng

            val steps = 10
            for (i in 0..steps) {
                delay(2000) // update coordinates every 2 seconds
                val fraction = i.toDouble() / steps
                val currentLat = startLat + (endLat - startLat) * fraction
                val currentLng = startLng + (endLng - startLng) * fraction

                _riderSimulatedLat.value = currentLat
                _riderSimulatedLng.value = currentLng

                // Update rider entity location in database as well
                repository.updateRider(rider.copy(currentLat = currentLat, currentLng = currentLng))
            }

            // Arrived & Delivered
            val deliveredOrder = order.copy(status = "DELIVERED", paymentStatus = "COMPLETED")
            repository.updateOrder(deliveredOrder)
            repository.updateRider(rider.copy(status = "AVAILABLE", earnings = rider.earnings + 60.0)) // Rider gets 60 delivery fee
            
            if (_selectedOrderForTracking.value?.id == order.id) {
                _selectedOrderForTracking.value = deliveredOrder
            }

            repository.insertNotification(
                NotificationEntity(
                    userId = order.customerId,
                    title = "Delivered! 🎉",
                    message = "Enjoy your warm Barmer food! Share your rating."
                )
            )
        }
    }

    // Admin: Approve / Reject KYC Documents
    fun approveKyc(doc: KycDocumentEntity) {
        viewModelScope.launch {
            repository.updateKycDocument(doc.copy(status = "APPROVED"))
            if (doc.ownerType == "RIDER") {
                repository.getRiderById(doc.ownerId).first()?.let { rider ->
                    repository.updateRider(rider.copy(kycStatus = "APPROVED"))
                }
            } else {
                repository.getRestaurantById(doc.ownerId).first()?.let { rest ->
                    repository.updateRestaurant(rest.copy(kycStatus = "APPROVED"))
                }
            }
        }
    }

    fun rejectKyc(doc: KycDocumentEntity, comments: String) {
        viewModelScope.launch {
            repository.updateKycDocument(doc.copy(status = "REJECTED", comments = comments))
            if (doc.ownerType == "RIDER") {
                repository.getRiderById(doc.ownerId).first()?.let { rider ->
                    repository.updateRider(rider.copy(kycStatus = "REJECTED"))
                }
            } else {
                repository.getRestaurantById(doc.ownerId).first()?.let { rest ->
                    repository.updateRestaurant(rest.copy(kycStatus = "REJECTED"))
                }
            }
        }
    }

    // Admin: Toggle Restaurant active status
    fun toggleRestaurantStatus(restaurant: RestaurantEntity) {
        viewModelScope.launch {
            repository.updateRestaurant(restaurant.copy(isOpen = !restaurant.isOpen))
        }
    }

    // Update Customer Profile
    fun updateCustomerProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            repository.insertUser(
                UserEntity(
                    id = "user_barmer",
                    name = name,
                    email = email,
                    phone = phone,
                    role = "CUSTOMER"
                )
            )
        }
    }

    // --- SEED DATABASE ---
    private suspend fun seedDatabase() {
        // 1. Core Users
        repository.insertUser(
            UserEntity(
                id = "user_barmer",
                name = "Malaram Choudhary",
                email = "malaramofficial@gmail.com",
                phone = "98290XXXXX",
                role = "CUSTOMER"
            )
        )

        // 2. Predefined Restaurants
        val r1 = repository.insertRestaurant(
            RestaurantEntity(
                name = "Desert Haveli Restaurant",
                description = "Traditional Rajasthani thalis and Desert culinary specialties",
                address = "Station Road, near Barmer Fort, Barmer",
                cuisineTags = "Traditional, Rajasthani, Thali",
                rating = 4.8,
                isOpen = true,
                bannerImageTag = "haveli"
            )
        ).toInt()

        val r2 = repository.insertRestaurant(
            RestaurantEntity(
                name = "Thar Desert Cafe & Sweets",
                description = "Popular local cafe featuring warm sweets, tea, and bajra snacks",
                address = "Gandhi Chowk, Barmer",
                cuisineTags = "Desserts, Beverages, Snacks",
                rating = 4.5,
                isOpen = true,
                bannerImageTag = "cafe"
            )
        ).toInt()

        val r3 = repository.insertRestaurant(
            RestaurantEntity(
                name = "Marwar Rasoi Veg Diner",
                description = "Pure veg family diner serving Authentic Gatte ki Sabji and Lehsun Chutney",
                address = "Roy Colony Road, Shastri Nagar, Barmer",
                cuisineTags = "North Indian, Rajasthani, Veg",
                rating = 4.6,
                isOpen = true,
                bannerImageTag = "rasoi"
            )
        ).toInt()

        val r4 = repository.insertRestaurant(
            RestaurantEntity(
                name = "Royal Rajputana Diner",
                description = "Experience the heritage fine dining cuisine of western Rajasthan",
                address = "NH-15, Bypass Road, Barmer",
                cuisineTags = "Royal, Traditional, Premium",
                rating = 4.9,
                isOpen = true,
                bannerImageTag = "royal"
            )
        ).toInt()

        // 3. Menu Items
        // R1
        repository.insertMenuItem(MenuItemEntity(restaurantId = r1, name = "Dal Baati Churma Thali", description = "Two baked wheat baatis served with spicy panchmel dal, rich churma, garlic chutney, and pure ghee", price = 250.0, category = "Traditional"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r1, name = "Ker Sangri Masala", description = "Authentic desert dried beans and desert berries cooked with traditional local spices", price = 180.0, category = "Traditional"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r1, name = "Bajre ki Roti (Set of 2)", description = "Clay-oven baked pearl millet flatbreads topped with organic homemade butter", price = 60.0, category = "Breads"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r1, name = "Rajasthani Kadhi Pakoda", description = "Tangy gram-flour and buttermilk curry cooked with fried dumplings", price = 120.0, category = "Main Course"))

        // R2
        repository.insertMenuItem(MenuItemEntity(restaurantId = r2, name = "Barmeri Mawa Kachori", description = "Crispy fried pastry stuffed with sweet khoya and nuts, dipped in saffron syrup", price = 90.0, category = "Desserts"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r2, name = "Desi Kulhad Masala Chai", description = "Cardamom-ginger infused milk tea brewed in earthen clay cups", price = 30.0, category = "Beverages"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r2, name = "Pyaz ki Kachori (2 Pcs)", description = "Spicy caramelized onion filling inside flaky deep fried golden shells", price = 60.0, category = "Snacks"))

        // R3
        repository.insertMenuItem(MenuItemEntity(restaurantId = r3, name = "Gatte ki Sabji Masala", description = "Steamed gram flour rolls simmered in a creamy, yogurt-based spiced sauce", price = 150.0, category = "Traditional"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r3, name = "Rajasthani Garlic Chutney", description = "Fiery-hot garlic, red chili, and oil paste crushed on traditional stone", price = 45.0, category = "Sides"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r3, name = "Saffron Shrikhand", description = "Creamy, strained sweet yogurt flavored with green cardamom and pure saffron", price = 80.0, category = "Desserts"))

        // R4
        repository.insertMenuItem(MenuItemEntity(restaurantId = r4, name = "Grand Rajputana Thali", description = "Luxurious royal thali with Ker Sangri, Gatte ki Sabji, Dal Baati, garlic chutney, churma, 2 bajra rotis, papad, salad, and a glass of chhaas", price = 390.0, category = "Traditional"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r4, name = "Spiced Smoked Butter Milk", description = "Chilled local buttermilk infused with fresh coriander and clay-smoked cumin", price = 50.0, category = "Beverages"))

        // 4. Riders
        repository.insertRider(
            RiderEntity(
                name = "Ramesh Kumar",
                phone = "98291XXXXX",
                vehicleNumber = "RJ-16-SA-1234",
                currentLat = 25.7510,
                currentLng = 71.3965,
                earnings = 180.0,
                kycStatus = "APPROVED",
                licenseNumber = "RJ16/DL/2023-010"
            )
        )

        repository.insertRider(
            RiderEntity(
                name = "Suresh Singh",
                phone = "98292XXXXX",
                vehicleNumber = "RJ-16-SA-5678",
                currentLat = 25.7535,
                currentLng = 71.3910,
                earnings = 120.0,
                kycStatus = "APPROVED",
                licenseNumber = "RJ16/DL/2022-441"
            )
        )

        // 5. Saved Addresses
        repository.insertAddress(
            AddressEntity(
                customerId = "user_barmer",
                title = "Home (Shastri Nagar)",
                addressLine = "Flat 101, Sun City Heights, Shastri Nagar, Barmer",
                lat = 25.7520,
                lng = 71.3980
            )
        )

        repository.insertAddress(
            AddressEntity(
                customerId = "user_barmer",
                title = "Office (Roy Colony)",
                addressLine = "Co-Working Hub, First Floor, Roy Colony, Barmer",
                lat = 25.7540,
                lng = 71.3930
            )
        )

        // 6. Seed some default KYC Document for demonstration
        repository.insertKycDocument(
            KycDocumentEntity(
                ownerId = r2,
                ownerType = "RESTAURANT",
                documentType = "FSSAI License",
                documentNumber = "FSSAI-1234567890",
                status = "PENDING"
            )
        )
    }
}

class FoodDeliveryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodDeliveryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodDeliveryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
