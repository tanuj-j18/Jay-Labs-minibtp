package com.example.jaylabs.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _forgotPasswordState = MutableStateFlow<AuthState>(AuthState.Idle)
    val forgotPasswordState: StateFlow<AuthState> = _forgotPasswordState

    fun resetPassword(email: String) {
        _forgotPasswordState.value = AuthState.Loading
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _forgotPasswordState.value = AuthState.Success
                } else {
                    _forgotPasswordState.value =
                        AuthState.Error(task.exception?.localizedMessage ?: "An error occurred")
                }
            }
    }
}


sealed class AuthState {
    object Idle : AuthState() // Initial state, no action performed
    object Loading : AuthState() // Show loading state
    object Success : AuthState() // Operation successful
    data class Error(val message: String) : AuthState() // Handle error state
}
