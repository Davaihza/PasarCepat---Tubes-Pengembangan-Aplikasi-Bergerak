package com.example.pasarcepat.data.model

data class CartItem(
    val id: String = "",
    val asin: String = "",
    val title: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val quantity: Int = 1
)
