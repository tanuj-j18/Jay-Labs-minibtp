package com.example.jaylabs.auth.signup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jaylabs.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _signUpState = MutableStateFlow<AuthResult<FirebaseUser?>>(AuthResult.Idle)
    val signUpState: StateFlow<AuthResult<FirebaseUser?>> = _signUpState

    fun signUpUser(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _signUpState.value = AuthResult.Loading
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                user?.let {
                    // Store additional user data in Firestore
                    val userData = mapOf(
                        "uid" to it.uid,
                        "email" to it.email,
                        "fullName" to fullName
                    )
                    firestore.collection("users").document(it.uid).set(userData).await()
                    _signUpState.value = AuthResult.Success(user)
                }
            } catch (e: FirebaseAuthException) {
                _signUpState.value = AuthResult.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
