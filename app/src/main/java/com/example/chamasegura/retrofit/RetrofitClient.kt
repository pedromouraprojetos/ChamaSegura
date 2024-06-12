package com.example.chamasegura.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://xhiksopvwtyhddxvskmn.supabase.co/"

    // OkHttpClient with logging interceptor
    private val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }.build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient) // Set the custom OkHttpClient with logging interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
