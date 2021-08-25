package com.example.test

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiConfig {
    @Multipart
    @POST("retrofit_example/upload_image.php")
    fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("file") name: RequestBody
    ): Call<ServerResponse>

    @Multipart
    @POST("retrofit_example/upload_multiple_files.php")
    fun uploadMulFile(
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part
    ): Call<ServerResponse>

}