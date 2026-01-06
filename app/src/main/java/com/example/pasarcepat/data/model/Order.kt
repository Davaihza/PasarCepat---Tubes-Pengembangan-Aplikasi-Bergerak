package com.example.pasarcepat.data.model

data class Order(
    val orderId: String = "",
    val userId: String = "user_default", // Hardcoded for now
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = "PAID",
    val timestamp: Long = System.currentTimeMillis()
)
