package com.example.pasarcepat.data.model

import com.google.gson.annotations.SerializedName

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class AmazonResponse(
    @SerializedName("data")
    val data: AmazonData
)

data class AmazonData(
    @SerializedName("products")
    val products: List<Product>
)

@Parcelize
data class Product(
    @SerializedName("asin")
    val asin: String = "",
    
    @SerializedName("product_title")
    val title: String = "",
    
    @SerializedName("product_price")
    val price: String? = null,
    
    @SerializedName("product_photo")
    val photoUrl: String? = null,

    @SerializedName("product_star_rating")
    val rating: String? = null,

    @SerializedName("product_num_ratings")
    val ratingCount: Int? = 0,

    @SerializedName("product_description") // Adding description just in case the API returns it
    val description: String? = null
) : Parcelable
