package com.example.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

/** Firestore is the source of truth; Room is the local/offline cache. */
class FirebaseSyncRepository(private val db: FoodDeliveryDao) {
    private val firestore = FirebaseFirestore.getInstance()
    private var restaurantsListener: ListenerRegistration? = null
    private var ordersListener: ListenerRegistration? = null
    private var notificationsListener: ListenerRegistration? = null

    fun startCatalogSync() {
        restaurantsListener?.remove()
        restaurantsListener = firestore.collection("restaurants")
            .whereEqualTo("approved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    snapshot.documents.forEach { doc ->
                        val id = doc.id.toIntOrNull() ?: return@forEach
                        val restaurant = RestaurantEntity(
                            id = id,
                            name = doc.getString("name") ?: return@forEach,
                            description = doc.getString("description") ?: "",
                            address = doc.getString("address") ?: "",
                            cuisineTags = (doc.get("cuisineTags") as? List<*>)?.filterIsInstance<String>()?.joinToString(", ")
                                ?: doc.getString("cuisineTags") ?: "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            isOpen = doc.getBoolean("isOpen") ?: false,
                            kycStatus = doc.getString("kycStatus") ?: "APPROVED",
                            bannerImageTag = doc.getString("bannerImageTag") ?: "default"
                        )
                        db.insertRestaurant(restaurant)
                        syncMenu(id, doc.reference)
                    }
                }
            }
    }

    private fun syncMenu(restaurantId: Int, restaurantRef: com.google.firebase.firestore.DocumentReference) {
        restaurantRef.collection("menuItems").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                snapshot.documents.forEach { doc ->
                    val id = doc.id.toIntOrNull() ?: return@forEach
                    db.insertMenuItem(
                        MenuItemEntity(
                            id = id,
                            restaurantId = restaurantId,
                            name = doc.getString("name") ?: return@forEach,
                            description = doc.getString("description") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            category = doc.getString("category") ?: "Other",
                            isAvailable = doc.getBoolean("isAvailable") ?: false,
                            imageTag = doc.getString("imageTag") ?: "default"
                        )
                    )
                }
            }
        }
    }

    fun startCustomerOrderSync(uid: String) {
        ordersListener?.remove()
        ordersListener = firestore.collection("orders")
            .whereEqualTo("customerId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    snapshot.documents.forEach { doc ->
                        val id = doc.id.toIntOrNull() ?: (doc.id.hashCode() and Int.MAX_VALUE)
                        val address = doc.get("deliveryAddress") as? Map<*, *> ?: emptyMap<String, Any>()
                        db.insertOrder(
                            OrderEntity(
                                id = id,
                                customerId = doc.getString("customerId") ?: uid,
                                customerName = doc.getString("customerName") ?: "",
                                restaurantId = doc.getString("restaurantId")?.toIntOrNull() ?: (doc.getLong("restaurantId")?.toInt() ?: 0),
                                restaurantName = doc.getString("restaurantName") ?: "",
                                riderId = doc.getString("riderId")?.toIntOrNull(),
                                riderName = doc.getString("riderName"),
                                status = doc.getString("status") ?: "PLACED",
                                totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                                deliveryAddress = address["addressLine"]?.toString() ?: doc.getString("deliveryAddress") ?: "",
                                deliveryLat = (address["lat"] as? Number)?.toDouble() ?: 0.0,
                                deliveryLng = (address["lng"] as? Number)?.toDouble() ?: 0.0,
                                paymentMethod = doc.getString("paymentMethod") ?: "COD",
                                paymentStatus = doc.getString("paymentStatus") ?: "PENDING",
                                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
    }

    fun startNotificationSync(uid: String) {
        notificationsListener?.remove()
        notificationsListener = firestore.collection("notifications")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    snapshot.documents.forEach { doc ->
                        db.insertNotification(
                            NotificationEntity(
                                id = doc.id.toIntOrNull() ?: (doc.id.hashCode() and Int.MAX_VALUE),
                                userId = uid,
                                title = doc.getString("title") ?: "BarmerEats",
                                message = doc.getString("message") ?: "",
                                isRead = doc.getBoolean("isRead") ?: false,
                                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
    }

    suspend fun refreshUser(uid: String) {
        val snap = firestore.collection("users").document(uid).get().await()
        if (!snap.exists()) return
        db.insertUser(
            UserEntity(
                id = uid,
                name = snap.getString("name") ?: "",
                email = snap.getString("email") ?: "",
                phone = snap.getString("phone") ?: "",
                role = snap.getString("role") ?: "CUSTOMER",
                createdAt = snap.getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()
            )
        )
    }

    fun stop() {
        restaurantsListener?.remove()
        ordersListener?.remove()
        notificationsListener?.remove()
        restaurantsListener = null
        ordersListener = null
        notificationsListener = null
    }
}
