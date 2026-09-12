package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDeliveryDao {

    // --- USERS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    // --- RESTAURANTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantEntity): Long

    @Query("SELECT * FROM restaurants ORDER BY rating DESC")
    fun getAllRestaurants(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    fun getRestaurantById(id: Int): Flow<RestaurantEntity?>

    @Update
    suspend fun updateRestaurant(restaurant: RestaurantEntity)

    // --- MENU ITEMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItemEntity)

    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId")
    fun getMenuItemsForRestaurant(restaurantId: Int): Flow<List<MenuItemEntity>>

    @Update
    suspend fun updateMenuItem(item: MenuItemEntity)

    @Query("DELETE FROM menu_items WHERE id = :id")
    suspend fun deleteMenuItemById(id: Int)

    // --- ORDERS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItemEntity)

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE restaurantId = :restaurantId ORDER BY createdAt DESC")
    fun getOrdersForRestaurant(restaurantId: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE riderId = :riderId ORDER BY createdAt DESC")
    fun getOrdersForRider(riderId: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = 'PLACED' OR status = 'READY' ORDER BY createdAt DESC")
    fun getAvailableOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: Int): Flow<OrderEntity?>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: Int): Flow<List<OrderItemEntity>>

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // --- RIDERS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRider(rider: RiderEntity): Long

    @Query("SELECT * FROM riders")
    fun getAllRiders(): Flow<List<RiderEntity>>

    @Query("SELECT * FROM riders WHERE id = :id")
    fun getRiderById(id: Int): Flow<RiderEntity?>

    @Update
    suspend fun updateRider(rider: RiderEntity)

    // --- ADDRESSES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Query("SELECT * FROM addresses WHERE customerId = :customerId")
    fun getAddressesForCustomer(customerId: String): Flow<List<AddressEntity>>

    // --- KYC DOCUMENTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKycDocument(doc: KycDocumentEntity)

    @Query("SELECT * FROM kyc_documents ORDER BY updatedAt DESC")
    fun getAllKycDocuments(): Flow<List<KycDocumentEntity>>

    @Update
    suspend fun updateKycDocument(doc: KycDocumentEntity)

    // --- NOTIFICATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>
}
