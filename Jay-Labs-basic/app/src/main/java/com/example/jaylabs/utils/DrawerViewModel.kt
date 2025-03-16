package com.example.jaylabs.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _userName = MutableStateFlow("User")
    val userName = _userName

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser
            fetchUserName(user?.uid)
        }
    }

    private fun fetchUserName(userId: String?) {
        _userName.value = "Loading..."

        if (userId == null) {
            _userName.value = "User"
            return
        }

        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(userId).get().await()
                _userName.value = document.getString("fullName") ?: "User"
            } catch (e: Exception) {
                _userName.value = "User"
            }
        }
    }
}
