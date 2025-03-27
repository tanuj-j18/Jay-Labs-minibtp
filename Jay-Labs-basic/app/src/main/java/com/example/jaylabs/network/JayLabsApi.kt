package com.example.jaylabs.network

import com.example.jaylabs.models.ModelResponse
import okhttp3.MultipartBody
import retrofit2.Response

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface JayLabsApi {


    @Multipart
    @POST("predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<ModelResponse>
}