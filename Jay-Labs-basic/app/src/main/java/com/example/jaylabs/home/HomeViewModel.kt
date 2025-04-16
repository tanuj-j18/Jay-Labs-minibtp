package com.example.jaylabs.home

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jaylabs.models.ModelResponse
import com.example.jaylabs.network.JayLabsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jayLabsApi: JayLabsApi,
    private val context: Application,  // ✅ Inject Application safely
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _modelResponse = MutableStateFlow<HomeEvent<ModelResponse>>(HomeEvent.Empty)
    val modelResponse = _modelResponse
    val user=firebaseAuth.currentUser
    fun getSelectedUri(imageUri: Uri?) {
        getResponse(imageUri)
    }

    fun getResponse(
        imageUri: Uri?,
        sex: String = "male",
        age: String = "40",
        anatomSite: String = "torso"
    ) {
        if (imageUri == null) return
        _modelResponse.value = HomeEvent.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val appContext = context as Context
                val file = File(appContext.cacheDir, "upload.jpg")

                appContext.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    file.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
                }

                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val filePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // Form fields as RequestBody
                val sexPart = RequestBody.create("text/plain".toMediaTypeOrNull(), sex)
                val agePart = RequestBody.create("text/plain".toMediaTypeOrNull(), age)
                val sitePart = RequestBody.create("text/plain".toMediaTypeOrNull(), anatomSite)

                // API call
                val response = jayLabsApi.uploadImage(
                    file = filePart,
                    sex = sexPart,
                    age = agePart,
                    anatomSite = sitePart
                )

                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    _modelResponse.value = HomeEvent.Success(responseBody)
                    Log.d("jaylabs", responseBody.toString())

                    // ✅ Save report to Firestore


                    user?.uid?.let { userId ->
                        val reportData = hashMapOf(
                            "timestamp" to System.currentTimeMillis(),
                            "sex" to sex,
                            "age" to age,
                            "anatomSite" to anatomSite,
                            "melanomaProbability" to responseBody.diagnosis.Melanoma,
                            "nevusProbability" to responseBody.diagnosis.Nevus,
                            "interpretation" to responseBody.interpretation
                        )

                        firestore.collection("users")
                            .document(userId)
                            .collection("reports")
                            .add(reportData)
                            .addOnSuccessListener {
                                Log.d("jaylabs", "Report added under user $userId with ID: ${it.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.e("jaylabs", "Error adding user report to Firestore", e)
                            }
                    }
                }
                else {
                    _modelResponse.value = HomeEvent.Error("Server error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _modelResponse.value = HomeEvent.Error("Exception: ${e.message}")
            }
        }
    }

    fun resetState() {
        _modelResponse.value = HomeEvent.Empty
    }

    sealed class HomeEvent<out T> {
        data class Success<out T>(val data: T) : HomeEvent<T>()
        data class Error(val message: String) : HomeEvent<Nothing>()
        object Loading : HomeEvent<Nothing>()
        object Empty : HomeEvent<Nothing>()
    }
}
