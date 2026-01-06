package com.example.pasarcepat.presentation

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Product

class SellerActivity : AppCompatActivity() {

    private lateinit var rvSellerProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    
    private var currentProducts = mutableListOf<Product>()
    private var currentSortMode = "Name (A-Z)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvSellerProducts = findViewById(R.id.rvSellerProducts)
        
        setupRecyclerView()
        loadProducts()
        setupListeners()
    }
    
    private fun setupListeners() {
        // Sorting Button
        findViewById<android.widget.Button>(R.id.btnSorting).setOnClickListener {
            showSortingDialog()
        }
    }
    
    fun onShippingClick(view: android.view.View) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_shipping_support, null)
        dialog.setContentView(view)
        
        view.findViewById<android.view.View>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun showSortingDialog() {
         val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
         val view = layoutInflater.inflate(R.layout.dialog_seller_sorting, null)
         dialog.setContentView(view)
         
         val radioGroup = view.findViewById<android.widget.RadioGroup>(R.id.radioGroupSort)
         
         // Set current selection
         when(currentSortMode) {
             "Name (A-Z)" -> radioGroup.check(R.id.rbNameAZ)
             "Name (Z-A)" -> radioGroup.check(R.id.rbNameZA)
             "Price (High-Low)" -> radioGroup.check(R.id.rbPriceHighLow)
             "Price (Low-High)" -> radioGroup.check(R.id.rbPriceLowHigh)
         }

         view.findViewById<android.view.View>(R.id.btnClose).setOnClickListener { dialog.dismiss() }
         
         view.findViewById<android.view.View>(R.id.btnReset).setOnClickListener {
             currentSortMode = "Name (A-Z)"
             sortProducts()
             dialog.dismiss()
         }
         
         view.findViewById<android.view.View>(R.id.btnApply).setOnClickListener {
             val selectedId = radioGroup.checkedRadioButtonId
             if (selectedId != -1) {
                 when(selectedId) {
                     R.id.rbNameAZ -> currentSortMode = "Name (A-Z)"
                     R.id.rbNameZA -> currentSortMode = "Name (Z-A)"
                     R.id.rbPriceHighLow -> currentSortMode = "Price (High-Low)"
                     R.id.rbPriceLowHigh -> currentSortMode = "Price (Low-High)"
                 }
                 sortProducts()
             }
             dialog.dismiss()
         }
         
         dialog.show()
    }
    
    private fun sortProducts() {
        when(currentSortMode) {
            "Name (A-Z)" -> currentProducts.sortBy { it.title }
            "Name (Z-A)" -> currentProducts.sortByDescending { it.title }
            "Price (High-Low)" -> currentProducts.sortByDescending { parsePrice(it.price) }
            "Price (Low-High)" -> currentProducts.sortBy { parsePrice(it.price) }
        }
        adapter.updateData(currentProducts)
    }

    private fun parsePrice(priceStr: String?): Double {
        if (priceStr.isNullOrEmpty()) return 0.0
        try {
            val clean = priceStr.replace("Rp", "").replace(".", "").replace(" ", "").trim()
            return clean.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            return 0.0
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList(), { product ->
             val intent = android.content.Intent(this, DetailActivity::class.java)
             intent.putExtra("PRODUCT_DATA", product)
             startActivity(intent)
        })
        rvSellerProducts.layoutManager = GridLayoutManager(this, 2)
        rvSellerProducts.adapter = adapter
    }

    private fun loadProducts() {
        // Mock data
        val products = mutableListOf<Product>()
        for (i in 1..8) {
             products.add(
                Product(
                    asin = "seller_prod_$i",
                    title = if(i % 2 == 0) "Sony Headphone $i" else "Apple Airpods $i",
                    price = "Rp ${100_000 + (i * 50_000)}",
                    photoUrl = "https://picsum.photos/200/200?random=$i",
                    rating = "4.6",
                    ratingCount = 86
                )
            )
        }
        currentProducts = products
        sortProducts() // Initial sort
    }
}
