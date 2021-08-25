package com.example.test

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppConfig {
    private val BASE_URL: String = "http://mushtaq.16mb.com/"
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}