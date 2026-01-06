package com.example.pasarcepat.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Product

class WishlistActivity : AppCompatActivity() {
    
    private lateinit var rvWishlist: RecyclerView
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        setupRecyclerView()
        setupBottomNav()
        loadWishlistData()
    }

    private fun setupRecyclerView() {
        rvWishlist = findViewById(R.id.rvWishlist)
        
        adapter = ProductAdapter(emptyList(), { product ->
             val intent = android.content.Intent(this, DetailActivity::class.java)
             intent.putExtra("PRODUCT_DATA", product)
             startActivity(intent)
        })
        rvWishlist.layoutManager = GridLayoutManager(this, 2)
        rvWishlist.adapter = adapter
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_wishlist 
        
        bottomNav.setOnItemSelectedListener { item ->
            val intent: android.content.Intent? = when(item.itemId) {
                R.id.nav_home -> android.content.Intent(this, MainActivity::class.java)
                R.id.nav_wishlist -> null
                R.id.nav_order -> android.content.Intent(this, OrderActivity::class.java)
                R.id.nav_account -> android.content.Intent(this, AccountActivity::class.java)
                else -> null
            }
            if (intent != null) {
                // Clear backstack to avoid loop? Or just start. 
                // Usually Main is root. 
                intent.flags = android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                overridePendingTransition(0, 0) // No animation
                false
            } else {
                true
            }
        }
    }

    private fun loadWishlistData() {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // Toast.makeText(this, "Login to view wishlist", android.widget.Toast.LENGTH_SHORT).show()
            // Optional: redirect to login
            return
        }

        val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val wishRef = database.getReference("users").child(user.uid).child("wishlist")

        wishRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
             override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                 val products = mutableListOf<Product>()
                 for (child in snapshot.children) {
                     val product = child.getValue(Product::class.java)
                     if (product != null) {
                         products.add(product)
                     }
                 }
                 adapter.updateData(products)
             }
             override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }
}
