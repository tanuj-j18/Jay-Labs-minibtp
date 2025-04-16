package com.example.jaylabs.network

import com.example.jaylabs.models.ModelResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface JayLabsApi {

    @Multipart
    @POST("predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("sex") sex: RequestBody,
        @Part("age") age: RequestBody,
        @Part("anatom_site") anatomSite: RequestBody
    ): Response<ModelResponse>
}
