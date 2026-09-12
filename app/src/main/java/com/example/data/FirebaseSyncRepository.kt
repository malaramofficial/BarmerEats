package com.example.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** Firestore is the source of truth; Room is the local/offline cache. */
class FirebaseSyncRepository(private val db: FoodDeliveryDao) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var restaurantsListener: ListenerRegistration? = null
    private var ordersListener: ListenerRegistration? = null
    private var notificationsListener: ListenerRegistration? = null
    private val menuListeners = mutableMapOf<Int, ListenerRegistration>()

    init {
        startCatalogSync()
        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                ordersListener?.remove(); ordersListener = null
                notificationsListener?.remove(); notificationsListener = null
            } else {
                scope.launch { refreshUser(uid) }
                startCustomerOrderSync(uid)
                startNotificationSync(uid)
            }
        }
    }

    fun startCatalogSync() {
        restaurantsListener?.remove()
        restaurantsListener = firestore.collection("restaurants")
            .whereEqualTo("approved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    snapshot.documents.forEach { doc ->
                        val id = doc.id.toIntOrNull() ?: return@forEach
                        db.insertRestaurant(
                            RestaurantEntity(
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
                        )
                        syncMenu(id, doc.reference)
                    }
                }
            }
    }

    private fun syncMenu(restaurantId: Int, restaurantRef: DocumentReference) {
        menuListeners[restaurantId]?.remove()
        menuListeners[restaurantId] = restaurantRef.collection("menuItems")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
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
                scope.launch {
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
                scope.launch {
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
        restaurantsListener?.remove(); restaurantsListener = null
        ordersListener?.remove(); ordersListener = null
        notificationsListener?.remove(); notificationsListener = null
        menuListeners.values.forEach { it.remove() }
        menuListeners.clear()
    }
}
