package com.example.pasarcepat.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Order
import com.example.pasarcepat.data.model.CartItem

class OrderActivity : AppCompatActivity() {

    private lateinit var rvOrder: RecyclerView
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        setupRecyclerView()
        setupBottomNav()
        loadOrderData()
    }

    private fun setupRecyclerView() {
        rvOrder = findViewById(R.id.rvOrder)
        
        adapter = OrderAdapter(emptyList()) { order ->
             // Maybe go to order detail? For now just Toast
             android.widget.Toast.makeText(this, "Order Details: ${order.orderId}", android.widget.Toast.LENGTH_SHORT).show()
        }
        rvOrder.layoutManager = LinearLayoutManager(this)
        rvOrder.adapter = adapter
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_order
        
        bottomNav.setOnItemSelectedListener { item ->
            val intent: android.content.Intent? = when(item.itemId) {
                R.id.nav_home -> android.content.Intent(this, MainActivity::class.java)
                R.id.nav_wishlist -> android.content.Intent(this, WishlistActivity::class.java)
                R.id.nav_order -> null
                R.id.nav_account -> android.content.Intent(this, AccountActivity::class.java)
                else -> null
            }
            if (intent != null) {
                intent.flags = android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                overridePendingTransition(0, 0)
                false 
            } else {
                true
            }
        }
    }

    private fun loadOrderData() {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user == null) return

        val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val orderRef = database.getReference("users").child(user.uid).child("orders")
        
        orderRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (child in snapshot.children) {
                    val order = child.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }
                // Sort by timestamp desc (newest first)
                orders.sortByDescending { it.timestamp }
                adapter.updateData(orders)
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }
}
