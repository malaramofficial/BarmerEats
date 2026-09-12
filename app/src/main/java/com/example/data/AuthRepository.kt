package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(private val context: Context, private val localDao: FoodDeliveryDao) {
    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private val _isFirebaseConfigured = MutableStateFlow(false)
    val isFirebaseConfigured: StateFlow<Boolean> = _isFirebaseConfigured

    init {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firebaseAuth = FirebaseAuth.getInstance()
                firestore = FirebaseFirestore.getInstance()
                _isFirebaseConfigured.value = true
                Log.d("AuthRepository", "Firebase initialized")
            } else {
                Log.w("AuthRepository", "Firebase is not configured")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase initialization error", e)
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        val auth = firebaseAuth ?: return AuthResult.Error("Firebase Authentication is not configured.")
        val db = firestore ?: return AuthResult.Error("Firebase Firestore is not configured.")
        if (!_isFirebaseConfigured.value) return AuthResult.Error("Firebase Authentication is unavailable.")

        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid ?: return AuthResult.Error("Authentication returned no user.")
            val doc = db.collection("users").document(uid).get().await()

            if (!doc.exists()) {
                auth.signOut()
                return AuthResult.Error("Your account profile was not found. Contact BarmerEats support.")
            }

            val role = doc.getString("role")?.uppercase() ?: "CUSTOMER"
            val safeRole = when (role) {
                "CUSTOMER", "RESTAURANT", "RIDER", "ADMIN" -> role
                else -> "CUSTOMER"
            }
            val user = UserEntity(
                id = uid,
                name = doc.getString("name") ?: result.user?.displayName ?: "User",
                email = result.user?.email ?: email.trim(),
                phone = doc.getString("phone") ?: result.user?.phoneNumber.orEmpty(),
                role = safeRole
            )
            localDao.insertUser(user)
            AuthResult.Success(user)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Invalid email or password.")
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Unable to sign in.")
        }
    }

    suspend fun signUp(email: String, password: String, name: String, phone: String, role: String): AuthResult {
        val auth = firebaseAuth ?: return AuthResult.Error("Firebase Authentication is not configured.")
        val db = firestore ?: return AuthResult.Error("Firebase Firestore is not configured.")
        if (!_isFirebaseConfigured.value) return AuthResult.Error("Firebase Authentication is unavailable.")

        return try {
            val sanitizedRole = when (role.uppercase()) {
                "RESTAURANT", "RIDER" -> role.uppercase()
                else -> "CUSTOMER"
            }
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid ?: return AuthResult.Error("Registration returned no user.")

            val user = UserEntity(uid, name.trim(), email.trim(), phone.trim(), sanitizedRole)
            db.collection("users").document(uid).set(
                mapOf(
                    "id" to uid,
                    "name" to user.name,
                    "email" to user.email,
                    "phone" to user.phone,
                    "role" to sanitizedRole,
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()
            localDao.insertUser(user)
            AuthResult.Success(user)
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Error("This email is already registered on BarmerEats.")
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Failed to create account.")
        }
    }

    fun signOut() {
        firebaseAuth?.signOut()
    }
}
