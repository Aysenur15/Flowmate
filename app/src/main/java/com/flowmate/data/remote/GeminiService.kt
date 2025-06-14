package com.flowmate.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Service for interacting with Gemini API
object GeminiService {
    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }
}
