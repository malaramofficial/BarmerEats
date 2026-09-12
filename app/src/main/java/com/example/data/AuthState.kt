package com.example.data

sealed class AuthState {
    object LoggedOut : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}
