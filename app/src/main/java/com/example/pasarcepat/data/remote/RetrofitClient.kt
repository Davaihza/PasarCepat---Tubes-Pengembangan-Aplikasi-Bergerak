package com.example.pasarcepat.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://real-time-amazon-data.p.rapidapi.com/"

    private val client = OkHttpClient.Builder().apply {
        addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("X-RapidAPI-Key", "03eb68055fmsh5a0fafee3b58505p15903cjsnf0d70bddedd8")
                .header("X-RapidAPI-Host", "real-time-amazon-data.p.rapidapi.com")
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
    }.build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
