package com.example.pasarcepat.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.CartItem
import com.example.pasarcepat.data.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var btnCheckout: Button
    private lateinit var tvTotalShopping: TextView
    private lateinit var tvShippingCost: TextView
    private lateinit var btnBack: ImageView
    
    private lateinit var adapter: CartAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var cartRef: DatabaseReference
    
    // Store items locally for calculation
    private var currentItems: List<CartItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        rvCart = findViewById(R.id.rvCart)
        btnCheckout = findViewById(R.id.btnCheckout)
        tvTotalShopping = findViewById(R.id.tvTotalAmount)
        tvShippingCost = findViewById(R.id.tvShippingAmount)
        btnBack = findViewById(R.id.btnBack)
        
        database = FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        cartRef = database.getReference("users").child(user.uid).child("cart")

        setupRecyclerView()
        loadCartItems()
        
        btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(emptyList()) { item, action ->
            when (action) {
                "delete" -> deleteItem(item)
                "plus" -> updateQuantity(item, 1)
                "minus" -> updateQuantity(item, -1)
            }
        }
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = adapter
    }

    private fun loadCartItems() {
        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartList = mutableListOf<CartItem>()
                for (child in snapshot.children) {
                    val item = child.getValue(CartItem::class.java)
                    if (item != null) {
                        cartList.add(item)
                    }
                }
                currentItems = cartList
                adapter.updateData(cartList)
                calculateTotal(cartList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Gagal memuat: ${error.message}", Toast.LENGTH_SHORT).show()
                currentItems = emptyList()
                calculateTotal(emptyList())
            }
        })

        btnCheckout.setOnClickListener {
            processCheckout()
        }
    }
    
    private fun calculatePrice(priceStr: String): Double {
        var clean = priceStr.replace(" ", "")
        
        if (clean.contains("$")) {
            clean = clean.replace("$", "").replace(",", "")
            val usd = clean.toDoubleOrNull() ?: 0.0
            return usd * 15500 
        }
        
        clean = clean.replace("IDR", "", true)
                     .replace("Rp", "", true)
                     .replace(".", "")
                     .replace(",00", "") 
                     
        return clean.toDoubleOrNull() ?: 0.0
    }
    
    private fun formatPrice(price: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(price).replace("Rp", "IDR ").replace(",00", "")
    }

    private fun calculateTotal(items: List<CartItem>) {
        var subtotal = 0.0
        for (item in items) {
            val price = calculatePrice(item.price)
            subtotal += (price * item.quantity)
        }
        
        val shipping = if (subtotal > 0) 15000.0 else 0.0
        val grandTotal = subtotal + shipping
        
        tvTotalShopping.text = formatPrice(subtotal)
        tvShippingCost.text = formatPrice(shipping)
        
        btnCheckout.text = "Checkout    ${formatPrice(grandTotal)}"
    }
    
    private fun processCheckout() {
        if (currentItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser ?: return
        val orderRef = database.getReference("users").child(user.uid).child("orders")
        val orderId = orderRef.push().key ?: return
        
        var subtotal = 0.0
        for (item in currentItems) {
            subtotal += (calculatePrice(item.price) * item.quantity)
        }
        val shipping = if (subtotal > 0) 15000.0 else 0.0
        val grandTotal = subtotal + shipping
        
        val order = Order(
            orderId = orderId,
            userId = user.uid,
            items = currentItems,
            totalAmount = grandTotal,
            status = "Processing"
        )
        
        orderRef.child(orderId).setValue(order).addOnSuccessListener {
            cartRef.removeValue()
            startActivity(android.content.Intent(this, SuccessActivity::class.java))
            finish()
        }
    }

    private fun updateQuantity(item: CartItem, change: Int) {
        val newQty = item.quantity + change
        if (newQty < 1) return
        cartRef.child(item.id).child("quantity").setValue(newQty)
    }

    private fun deleteItem(item: CartItem) {
        cartRef.child(item.id).removeValue()
    }
}
