package com.example.pasarcepat.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Product

class DetailActivity : AppCompatActivity() {

    private lateinit var rvFeatured: RecyclerView
    private lateinit var featuredAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Get Product from Intent
        val product = intent.getParcelableExtra<Product>("PRODUCT_DATA")

        if (product != null) {
            setupUI(product)
            setupFeatured()
        } else {
            Toast.makeText(this, "Error loading product", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupUI(product: Product) {
        val ivImage = findViewById<ImageView>(R.id.ivDetailImage)
        val tvName = findViewById<TextView>(R.id.tvDetailName)
        val tvPrice = findViewById<TextView>(R.id.tvDetailPrice)
        val tvRating = findViewById<TextView>(R.id.tvDetailRating)
        val tvReviews = findViewById<TextView>(R.id.tvDetailReviews)
        val tvDesc = findViewById<TextView>(R.id.tvDetailDescription)
        val btnCart = findViewById<Button>(R.id.btnAddToCart)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnCartHeader = findViewById<ImageView>(R.id.btnCartHeader)
        val btnShare = findViewById<ImageView>(R.id.btnShare)
        val btnWishlist = findViewById<Button>(R.id.btnAddWishlist)
        val sectionStore = findViewById<LinearLayout>(R.id.sectionStore)
        val btnSeeAllReviews = findViewById<Button>(R.id.btnSeeAllReviews)

        tvName.text = product.title
        tvPrice.text = product.price ?: "N/A"
        tvRating.text = product.rating ?: "-"
        tvReviews.text = "${product.ratingCount ?: 0} Reviews"
        
        tvDesc.text = product.description ?: "The speaker unit contains a diaphragm that is precision-grown from NAC Audio bio-cellulose, making it stiffer, lighter and stronger than regular PET speaker units, and allowing the sound-producing diaphragm to vibrate without the levels of distortion found in other speakers."

        Glide.with(this)
            .load(product.photoUrl)
            .placeholder(R.drawable.ic_app_logo)
            .into(ivImage)

        btnBack.setOnClickListener { finish() }
        
        btnCartHeader.setOnClickListener {
            startActivity(android.content.Intent(this, CartActivity::class.java))
        }

        btnShare.setOnClickListener {
            Toast.makeText(this, "Shared!", Toast.LENGTH_SHORT).show()
        }

        btnWishlist.setOnClickListener {
            addToWishlist(product)
        }

        btnCart.setOnClickListener {
            addToCart(product)
        }
        
        sectionStore.setOnClickListener {
             startActivity(android.content.Intent(this, SellerActivity::class.java))
        }
        
        btnSeeAllReviews?.setOnClickListener {
             startActivity(android.content.Intent(this, ReviewActivity::class.java))
        }
    }
    
    private fun setupFeatured() {
        rvFeatured = findViewById(R.id.rvFeatured)
        
        featuredAdapter = ProductAdapter(emptyList(), { product ->
            val intent = android.content.Intent(this, DetailActivity::class.java)
            intent.putExtra("PRODUCT_DATA", product)
            startActivity(intent)
        })
        rvFeatured.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFeatured.adapter = featuredAdapter
        
        // Load dummy featured
        val products = mutableListOf<Product>()
        for (i in 1..5) {
             products.add(
                Product(
                    asin = "feat_detail_$i",
                    title = "Featured Item #$i",
                    price = "Rp 250.000",
                    photoUrl = "https://picsum.photos/200/200?random=${i+100}",
                    rating = "4.8",
                    ratingCount = 120,
                    description = "Description..."
                )
            )
        }
        featuredAdapter.updateData(products)
    }

    private fun addToCart(product: Product) {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            startActivity(android.content.Intent(this, LoginActivity::class.java))
            return
        }
        
        val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val cartRef = database.getReference("users").child(user.uid).child("cart")
        
        // Check if item already exists to update quantity
        cartRef.orderByChild("asin").equalTo(product.asin).addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
             override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                 if (snapshot.exists()) {
                     for (child in snapshot.children) {
                         val existingItem = child.getValue(com.example.pasarcepat.data.model.CartItem::class.java)
                         if (existingItem != null) {
                             cartRef.child(existingItem.id).child("quantity").setValue(existingItem.quantity + 1)
                             Toast.makeText(this@DetailActivity, "Quantity updated in cart!", Toast.LENGTH_SHORT).show()
                             return
                         }
                     }
                 } else {
                     // Add new item
                     val cartId = cartRef.push().key ?: return
                     val cartItem = com.example.pasarcepat.data.model.CartItem(
                        id = cartId,
                        asin = product.asin,
                        title = product.title,
                        price = product.price ?: "0",
                        imageUrl = product.photoUrl ?: "",
                        quantity = 1
                    )
                    cartRef.child(cartId).setValue(cartItem)
                        .addOnSuccessListener {
                            Toast.makeText(this@DetailActivity, "Berhasil masuk keranjang!", Toast.LENGTH_SHORT).show()
                        }
                 }
             }

             override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                 Toast.makeText(this@DetailActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
             }
        })
    }
    
    private fun addToWishlist(product: Product) {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val wishRef = database.getReference("users").child(user.uid).child("wishlist")
        
        wishRef.orderByChild("asin").equalTo(product.asin).addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
             override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                 if (!snapshot.exists()) {
                      val wishId = wishRef.push().key ?: return
                      wishRef.child(wishId).setValue(product)
                        .addOnSuccessListener {
                            Toast.makeText(this@DetailActivity, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
                        }
                 } else {
                     Toast.makeText(this@DetailActivity, "Already in Wishlist", Toast.LENGTH_SHORT).show()
                 }
             }
             override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }
}
