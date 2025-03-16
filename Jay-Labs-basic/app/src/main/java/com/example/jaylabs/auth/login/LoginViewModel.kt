package com.example.jaylabs.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jaylabs.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthResult<FirebaseUser?>>(AuthResult.Idle)
    val loginState: StateFlow<AuthResult<FirebaseUser?>> = _loginState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthResult.Loading
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                _loginState.value = AuthResult.Success(result.user)
            } catch (e: FirebaseAuthException) {
                _loginState.value = AuthResult.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
