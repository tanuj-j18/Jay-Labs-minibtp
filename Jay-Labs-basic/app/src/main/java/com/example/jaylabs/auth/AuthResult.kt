package com.example.jaylabs.auth

sealed class AuthResult<out T> {
    data object Idle: AuthResult<Nothing>()
    data object Loading : AuthResult<Nothing>()
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}
