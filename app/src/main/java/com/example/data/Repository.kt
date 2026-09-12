package com.example.data

import kotlinx.coroutines.flow.Flow

class FoodDeliveryRepository(private val dao: FoodDeliveryDao) {

    // --- USERS ---
    fun getUserById(id: String): Flow<UserEntity?> = dao.getUserById(id)
    suspend fun insertUser(user: UserEntity) = dao.insertUser(user)

    // --- RESTAURANTS ---
    val allRestaurants: Flow<List<RestaurantEntity>> = dao.getAllRestaurants()
    fun getRestaurantById(id: Int): Flow<RestaurantEntity?> = dao.getRestaurantById(id)
    suspend fun insertRestaurant(restaurant: RestaurantEntity): Long = dao.insertRestaurant(restaurant)
    suspend fun updateRestaurant(restaurant: RestaurantEntity) = dao.updateRestaurant(restaurant)

    // --- MENU ITEMS ---
    fun getMenuItemsForRestaurant(restaurantId: Int): Flow<List<MenuItemEntity>> = dao.getMenuItemsForRestaurant(restaurantId)
    suspend fun insertMenuItem(item: MenuItemEntity) = dao.insertMenuItem(item)
    suspend fun updateMenuItem(item: MenuItemEntity) = dao.updateMenuItem(item)
    suspend fun deleteMenuItemById(id: Int) = dao.deleteMenuItemById(id)

    // --- ORDERS ---
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val availableOrders: Flow<List<OrderEntity>> = dao.getAvailableOrders()
    
    fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>> = dao.getOrdersForCustomer(customerId)
    fun getOrdersForRestaurant(restaurantId: Int): Flow<List<OrderEntity>> = dao.getOrdersForRestaurant(restaurantId)
    fun getOrdersForRider(riderId: Int): Flow<List<OrderEntity>> = dao.getOrdersForRider(riderId)
    fun getOrderById(orderId: Int): Flow<OrderEntity?> = dao.getOrderById(orderId)
    fun getOrderItems(orderId: Int): Flow<List<OrderItemEntity>> = dao.getOrderItems(orderId)

    suspend fun insertOrder(order: OrderEntity): Long = dao.insertOrder(order)
    suspend fun insertOrderItem(item: OrderItemEntity) = dao.insertOrderItem(item)
    suspend fun updateOrder(order: OrderEntity) = dao.updateOrder(order)

    // --- RIDERS ---
    val allRiders: Flow<List<RiderEntity>> = dao.getAllRiders()
    fun getRiderById(id: Int): Flow<RiderEntity?> = dao.getRiderById(id)
    suspend fun insertRider(rider: RiderEntity): Long = dao.insertRider(rider)
    suspend fun updateRider(rider: RiderEntity) = dao.updateRider(rider)

    // --- ADDRESSES ---
    fun getAddressesForCustomer(customerId: String): Flow<List<AddressEntity>> = dao.getAddressesForCustomer(customerId)
    suspend fun insertAddress(address: AddressEntity) = dao.insertAddress(address)

    // --- KYC DOCUMENTS ---
    val allKycDocuments: Flow<List<KycDocumentEntity>> = dao.getAllKycDocuments()
    suspend fun insertKycDocument(doc: KycDocumentEntity) = dao.insertKycDocument(doc)
    suspend fun updateKycDocument(doc: KycDocumentEntity) = dao.updateKycDocument(doc)

    // --- NOTIFICATIONS ---
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> = dao.getNotificationsForUser(userId)
    suspend fun insertNotification(notification: NotificationEntity) = dao.insertNotification(notification)
}
