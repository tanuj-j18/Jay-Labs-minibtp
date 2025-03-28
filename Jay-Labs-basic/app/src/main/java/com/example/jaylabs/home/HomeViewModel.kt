package com.example.jaylabs.home

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jaylabs.models.ModelResponse
import com.example.jaylabs.network.JayLabsApi
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
    private val context: Application  // ✅ Inject Application safely
) : ViewModel() {

    private val _modelResponse = MutableStateFlow<HomeEvent<ModelResponse>>(HomeEvent.Empty)
    val modelResponse = _modelResponse

    fun getSelectedUri(imageUri: Uri?) {
        getResponse(imageUri)
    }

    fun getResponse(imageUri: Uri?) {
        if(imageUri==null) return
        _modelResponse.value = HomeEvent.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val appContext = context as Context  // ✅ Cast Application to Context
                val file = File(appContext.cacheDir, "upload.jpg") // Use app cache directory

                appContext.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    file.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
                }

                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = jayLabsApi.uploadImage(body)

                if (response.isSuccessful) {
                    _modelResponse.value = HomeEvent.Success(response.body()!!)
                    Log.d("jaylabs", response.body().toString())
                } else {
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
