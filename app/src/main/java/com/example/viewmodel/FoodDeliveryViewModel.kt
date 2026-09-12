package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FoodDeliveryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FoodDeliveryRepository(database.foodDeliveryDao())
    private val authRepository = AuthRepository(application, database.foodDeliveryDao())
    val isFirebaseConfigured: StateFlow<Boolean> = authRepository.isFirebaseConfigured
    private val locationTracker = LocationTracker(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentRole = MutableStateFlow("CUSTOMER")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()
    private val _customerScreen = MutableStateFlow("HOME")
    val customerScreen: StateFlow<String> = _customerScreen.asStateFlow()
    private val _restaurantScreen = MutableStateFlow("DASHBOARD")
    val restaurantScreen: StateFlow<String> = _restaurantScreen.asStateFlow()
    private val _riderScreen = MutableStateFlow("AVAILABLE_ORDERS")
    val riderScreen: StateFlow<String> = _riderScreen.asStateFlow()
    private val _adminScreen = MutableStateFlow("DASHBOARD")
    val adminScreen: StateFlow<String> = _adminScreen.asStateFlow()

    private val _selectedRestaurant = MutableStateFlow<RestaurantEntity?>(null)
    val selectedRestaurant: StateFlow<RestaurantEntity?> = _selectedRestaurant.asStateFlow()
    private val _selectedOrderForTracking = MutableStateFlow<OrderEntity?>(null)
    val selectedOrderForTracking: StateFlow<OrderEntity?> = _selectedOrderForTracking.asStateFlow()

    val restaurants: StateFlow<List<RestaurantEntity>> = repository.allRestaurants.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val availableOrders: StateFlow<List<OrderEntity>> = repository.availableOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allRiders: StateFlow<List<RiderEntity>> = repository.allRiders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allKycDocuments: StateFlow<List<KycDocumentEntity>> = repository.allKycDocuments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _cartItems = MutableStateFlow<Map<MenuItemEntity, Int>>(emptyMap())
    val cartItems: StateFlow<Map<MenuItemEntity, Int>> = _cartItems.asStateFlow()

    val savedAddresses: StateFlow<List<AddressEntity>> = _authState.flatMapLatest { state ->
        val uid = (state as? AuthState.Authenticated)?.user?.id ?: return@flatMapLatest emptyFlow()
        repository.getAddressesForCustomer(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<UserEntity?> = _authState.flatMapLatest { state ->
        val uid = (state as? AuthState.Authenticated)?.user?.id ?: return@flatMapLatest emptyFlow()
        repository.getUserById(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentRider: StateFlow<RiderEntity?> = repository.getRiderById(1).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val currentRestaurant: StateFlow<RestaurantEntity?> = repository.getRestaurantById(1).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notifications: StateFlow<List<NotificationEntity>> = _authState.flatMapLatest { state ->
        val uid = (state as? AuthState.Authenticated)?.user?.id ?: return@flatMapLatest emptyFlow()
        repository.getNotificationsForUser(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (repository.allRestaurants.first().isEmpty()) seedDatabase()
        }
    }

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.user)
                    _currentRole.value = result.user.role
                }
                is AuthResult.Error -> _authState.value = AuthState.Error(result.message)
            }
        }
    }

    fun signUpUser(email: String, password: String, name: String, phone: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = authRepository.signUp(email, password, name, phone, role)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.user)
                    _currentRole.value = result.user.role
                }
                is AuthResult.Error -> _authState.value = AuthState.Error(result.message)
            }
        }
    }

    fun logoutUser() {
        authRepository.signOut()
        _authState.value = AuthState.LoggedOut
        _currentRole.value = "CUSTOMER"
        _customerScreen.value = "HOME"
    }

    // Role can only be switched to the authenticated user's trusted role.
    // Production UI should not expose role switching.
    fun switchRole(role: String) {
        val trustedRole = (_authState.value as? AuthState.Authenticated)?.user?.role ?: return
        if (role == trustedRole) _currentRole.value = role
    }

    fun navigateCustomer(screen: String) { _customerScreen.value = screen }
    fun navigateRestaurant(screen: String) { _restaurantScreen.value = screen }
    fun navigateRider(screen: String) { _riderScreen.value = screen }
    fun navigateAdmin(screen: String) { _adminScreen.value = screen }

    fun selectRestaurant(restaurant: RestaurantEntity) {
        _selectedRestaurant.value = restaurant
        _customerScreen.value = "RESTAURANT"
    }

    fun getMenuItemsForRestaurant(restaurantId: Int): Flow<List<MenuItemEntity>> = repository.getMenuItemsForRestaurant(restaurantId)
    fun getOrderItems(orderId: Int): Flow<List<OrderItemEntity>> = repository.getOrderItems(orderId)

    fun addToCart(item: MenuItemEntity) {
        val current = _cartItems.value.toMutableMap()
        current[item] = (current[item] ?: 0) + 1
        _cartItems.value = current
    }

    fun removeFromCart(item: MenuItemEntity) {
        val current = _cartItems.value.toMutableMap()
        val count = current[item] ?: 0
        if (count <= 1) current.remove(item) else current[item] = count - 1
        _cartItems.value = current
    }

    fun clearCart() { _cartItems.value = emptyMap() }

    fun checkout(address: AddressEntity, paymentMethod: String) {
        viewModelScope.launch {
            val cart = _cartItems.value
            val rest = _selectedRestaurant.value ?: return@launch
            val activeUser = (_authState.value as? AuthState.Authenticated)?.user ?: return@launch
            if (cart.isEmpty()) return@launch

            val totalAmount = cart.entries.sumOf { it.key.price * it.value } + 40.0
            val order = OrderEntity(
                customerId = activeUser.id,
                customerName = activeUser.name,
                restaurantId = rest.id,
                restaurantName = rest.name,
                status = "PLACED",
                totalAmount = totalAmount,
                deliveryAddress = address.addressLine,
                deliveryLat = address.lat,
                deliveryLng = address.lng,
                paymentMethod = paymentMethod,
                // Online payment remains pending until a trusted payment provider verifies it.
                paymentStatus = "PENDING"
            )

            val orderId = repository.insertOrder(order).toInt()
            cart.forEach { (item, qty) ->
                repository.insertOrderItem(OrderItemEntity(orderId = orderId, menuItemId = item.id, itemName = item.name, price = item.price, quantity = qty))
            }

            repository.insertNotification(NotificationEntity(userId = activeUser.id, title = "Order Placed! 🍛", message = "Your order with ${rest.name} has been placed successfully."))
            repository.insertNotification(NotificationEntity(userId = "restaurant_${rest.id}", title = "New Order Received!", message = "New order (#$orderId) containing ${cart.size} items."))

            _selectedOrderForTracking.value = order.copy(id = orderId)
            clearCart()
            _customerScreen.value = "LIVE_TRACKING"
        }
    }

    fun acceptOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(status = "ACCEPTED")
            repository.updateOrder(updated)
            if (_selectedOrderForTracking.value?.id == order.id) _selectedOrderForTracking.value = updated
            repository.insertNotification(NotificationEntity(userId = order.customerId, title = "Order Accepted 👨‍🍳", message = "${order.restaurantName} is preparing your food."))
        }
    }

    fun prepareOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(status = "PREPARING")
            repository.updateOrder(updated)
            if (_selectedOrderForTracking.value?.id == order.id) _selectedOrderForTracking.value = updated
        }
    }

    fun readyOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(status = "READY")
            repository.updateOrder(updated)
            if (_selectedOrderForTracking.value?.id == order.id) _selectedOrderForTracking.value = updated
            repository.insertNotification(NotificationEntity(userId = order.customerId, title = "Order Ready! 🛵", message = "Your meal is ready for pickup."))
        }
    }

    // Real delivery movement is intentionally not simulated here. The LocationTracker
    // must be connected to a foreground service and a trusted backend in the next phase.
    fun pickUpOrder(order: OrderEntity, rider: RiderEntity) {
        viewModelScope.launch {
            val updatedOrder = order.copy(status = "PICKED_UP", riderId = rider.id, riderName = rider.name)
            repository.updateOrder(updatedOrder)
            repository.updateRider(rider.copy(status = "BUSY"))
            if (_selectedOrderForTracking.value?.id == order.id) _selectedOrderForTracking.value = updatedOrder
            repository.insertNotification(NotificationEntity(userId = order.customerId, title = "Out for Delivery! 🚀", message = "Rider ${rider.name} is on the way to your address."))
        }
    }

    fun approveKyc(doc: KycDocumentEntity) {
        viewModelScope.launch {
            repository.updateKycDocument(doc.copy(status = "APPROVED"))
            if (doc.ownerType == "RIDER") repository.getRiderById(doc.ownerId).first()?.let { repository.updateRider(it.copy(kycStatus = "APPROVED")) }
            else repository.getRestaurantById(doc.ownerId).first()?.let { repository.updateRestaurant(it.copy(kycStatus = "APPROVED")) }
        }
    }

    fun rejectKyc(doc: KycDocumentEntity, comments: String) {
        viewModelScope.launch {
            repository.updateKycDocument(doc.copy(status = "REJECTED", comments = comments))
            if (doc.ownerType == "RIDER") repository.getRiderById(doc.ownerId).first()?.let { repository.updateRider(it.copy(kycStatus = "REJECTED")) }
            else repository.getRestaurantById(doc.ownerId).first()?.let { repository.updateRestaurant(it.copy(kycStatus = "REJECTED")) }
        }
    }

    fun toggleRestaurantStatus(restaurant: RestaurantEntity) {
        viewModelScope.launch { repository.updateRestaurant(restaurant.copy(isOpen = !restaurant.isOpen)) }
    }

    fun getActiveUserId(): String = (_authState.value as? AuthState.Authenticated)?.user?.id ?: ""

    fun updateCustomerProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            val uid = getActiveUserId()
            if (uid.isBlank()) return@launch
            val existing = repository.getUserById(uid).first()
            repository.insertUser(UserEntity(id = uid, name = name, email = email, phone = phone, role = existing?.role ?: "CUSTOMER"))
        }
    }

    private suspend fun seedDatabase() {
        repository.insertUser(UserEntity(id = "user_barmer", name = "Demo Customer", email = "demo@barmereats.local", phone = "", role = "CUSTOMER"))

        val r1 = repository.insertRestaurant(RestaurantEntity(name = "Desert Haveli Restaurant", description = "Traditional Rajasthani thalis and desert specialties", address = "Station Road, Barmer", cuisineTags = "Traditional, Rajasthani, Thali", rating = 4.8, isOpen = true, bannerImageTag = "haveli")).toInt()
        val r2 = repository.insertRestaurant(RestaurantEntity(name = "Thar Desert Cafe & Sweets", description = "Local cafe with sweets, tea and bajra snacks", address = "Gandhi Chowk, Barmer", cuisineTags = "Desserts, Beverages, Snacks", rating = 4.5, isOpen = true, bannerImageTag = "cafe")).toInt()
        val r3 = repository.insertRestaurant(RestaurantEntity(name = "Marwar Rasoi Veg Diner", description = "Pure veg family diner", address = "Roy Colony Road, Barmer", cuisineTags = "North Indian, Rajasthani, Veg", rating = 4.6, isOpen = true, bannerImageTag = "rasoi")).toInt()
        val r4 = repository.insertRestaurant(RestaurantEntity(name = "Royal Rajputana Diner", description = "Premium western Rajasthan cuisine", address = "Bypass Road, Barmer", cuisineTags = "Royal, Traditional, Premium", rating = 4.9, isOpen = true, bannerImageTag = "royal")).toInt()

        repository.insertMenuItem(MenuItemEntity(restaurantId = r1, name = "Dal Baati Churma Thali", description = "Traditional Rajasthani thali", price = 250.0, category = "Traditional"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r1, name = "Ker Sangri Masala", description = "Authentic desert dish", price = 180.0, category = "Traditional"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r1, name = "Bajre ki Roti", description = "Pearl millet flatbread", price = 60.0, category = "Breads"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r2, name = "Barmeri Mawa Kachori", description = "Sweet khoya kachori", price = 90.0, category = "Desserts"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r2, name = "Masala Chai", description = "Cardamom ginger tea", price = 30.0, category = "Beverages"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r3, name = "Gatte ki Sabji", description = "Rajasthani curry", price = 150.0, category = "Traditional"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r3, name = "Garlic Chutney", description = "Spicy garlic chutney", price = 45.0, category = "Sides"))
        repository.insertMenuItem(MenuItemEntity(restaurantId = r4, name = "Grand Rajputana Thali", description = "Premium royal thali", price = 390.0, category = "Traditional"))

        repository.insertRider(RiderEntity(name = "Demo Rider", phone = "", vehicleNumber = "", currentLat = 25.7510, currentLng = 71.3965, earnings = 0.0, kycStatus = "PENDING", licenseNumber = ""))
        repository.insertAddress(AddressEntity(customerId = "user_barmer", title = "Demo Home", addressLine = "Barmer, Rajasthan", lat = 25.7520, lng = 71.3980))
        repository.insertKycDocument(KycDocumentEntity(ownerId = r2, ownerType = "RESTAURANT", documentType = "FSSAI License", documentNumber = "MASKED", status = "PENDING"))
    }
}

class FoodDeliveryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = FoodDeliveryViewModel(application) as T
}
