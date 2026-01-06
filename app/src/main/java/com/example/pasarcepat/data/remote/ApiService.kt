package com.example.pasarcepat.data.remote

import com.example.pasarcepat.data.model.AmazonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("country") country: String = "US"
    ): Call<AmazonResponse>
}
