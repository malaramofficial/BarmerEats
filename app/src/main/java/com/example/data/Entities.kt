package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // "CUSTOMER", "RESTAURANT", "RIDER", "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val address: String,
    val cuisineTags: String, // Comma separated, e.g., "Rajasthani, Traditional"
    val rating: Double,
    val isOpen: Boolean = true,
    val kycStatus: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED"
    val bannerImageTag: String = "default"
)

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String, // "Traditional", "Main Course", "Breads", "Desserts", "Beverages"
    val isAvailable: Boolean = true,
    val imageTag: String = "default"
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: String,
    val customerName: String,
    val restaurantId: Int,
    val restaurantName: String,
    val riderId: Int? = null,
    val riderName: String? = null,
    val status: String, // "PLACED", "ACCEPTED", "PREPARING", "READY", "PICKED_UP", "DELIVERED", "CANCELLED"
    val totalAmount: Double,
    val deliveryAddress: String,
    val deliveryLat: Double,
    val deliveryLng: Double,
    val paymentMethod: String, // "UPI", "CARD", "CASH"
    val paymentStatus: String = "PENDING", // "PENDING", "COMPLETED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val menuItemId: Int,
    val itemName: String,
    val price: Double,
    val quantity: Int
)

@Entity(tableName = "riders")
data class RiderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val vehicleNumber: String,
    val status: String = "AVAILABLE", // "AVAILABLE", "BUSY", "OFFLINE"
    val currentLat: Double,
    val currentLng: Double,
    val earnings: Double = 0.0,
    val kycStatus: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED"
    val licenseNumber: String = ""
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: String,
    val title: String, // "Home", "Work", "Barmer Fort"
    val addressLine: String,
    val lat: Double,
    val lng: Double
)

@Entity(tableName = "kyc_documents")
data class KycDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerId: Int, // riderId or restaurantId
    val ownerType: String, // "RIDER", "RESTAURANT"
    val documentType: String, // "Aadhaar", "FSSAI License", "Driving License"
    val documentNumber: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val comments: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // Can be customerId, restaurantId, riderId or "ADMIN"
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
