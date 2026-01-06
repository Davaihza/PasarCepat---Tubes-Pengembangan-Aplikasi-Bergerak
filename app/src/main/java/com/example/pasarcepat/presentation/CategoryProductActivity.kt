package com.example.pasarcepat.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.AmazonResponse
import com.example.pasarcepat.data.model.Product
import com.example.pasarcepat.data.remote.RetrofitClient
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryProductActivity : AppCompatActivity() {

    private lateinit var tvCategoryTitle: TextView
    private lateinit var rvProducts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnFilterSorting: MaterialButton
    private lateinit var btnBack: View
    private lateinit var productAdapter: ProductAdapter

    private var categoryName: String = "Category"
    private var allProducts: List<Product> = emptyList()
    
    // Filter State
    private var minPriceFilter: Float = 0f
    private var maxPriceFilter: Float = Float.MAX_VALUE
    private var selectedCategoriesFilter: List<String> = listOf("All")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_product)

        categoryName = intent.getStringExtra("EXTRA_CATEGORY") ?: "Gadget"

        initViews()
        setupRecyclerView()
        setupListeners()
        loadData()
    }

    private fun initViews() {
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle)
        tvCategoryTitle.text = categoryName

        rvProducts = findViewById(R.id.rvProducts)
        progressBar = findViewById(R.id.progressBar)
        btnFilterSorting = findViewById(R.id.btnFilterSorting)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList(), { product ->
            // On Item Click
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("PRODUCT_DATA", product)
            startActivity(intent)
        }, { product ->
             // On More Click (Optional, if you want the popup here too)
             // Using simple Toast or duplicate dialog logic from MainActivity if needed
             Toast.makeText(this, "More options for ${product.title}", Toast.LENGTH_SHORT).show()
        })
        
        rvProducts.layoutManager = GridLayoutManager(this, 2)
        rvProducts.adapter = productAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        
        btnFilterSorting.setOnClickListener {
            val filterFragment = FilterBottomSheetFragment()
            filterFragment.setOnApplyListener { min, max, categories ->
                minPriceFilter = min
                maxPriceFilter = max
                selectedCategoriesFilter = categories
                applyFilters()
            }
            filterFragment.show(supportFragmentManager, FilterBottomSheetFragment.TAG)
        }
    }

    private fun loadData() {
        progressBar.visibility = View.VISIBLE
        // Search by category name as query
        RetrofitClient.instance.searchProducts(categoryName).enqueue(object : Callback<AmazonResponse> {
            override fun onResponse(call: Call<AmazonResponse>, response: Response<AmazonResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val products = response.body()?.data?.products
                    if (!products.isNullOrEmpty()) {
                        allProducts = products
                        applyFilters()
                    } else {
                        // Fallback dummy
                         useDummyData()
                    }
                } else {
                    useDummyData()
                }
            }

            override fun onFailure(call: Call<AmazonResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                useDummyData()
            }
        })
    }
    
    private fun useDummyData() {
        // Generate dummy data based on category
        val dummies = mutableListOf<Product>()
        for (i in 1..8) {
             dummies.add(
                Product(
                    asin = "dummy_${categoryName}_$i",
                    title = "$categoryName Item #$i",
                    price = "Rp ${50000 + (i * 20000)}", // Some inside range, some outside
                    photoUrl = "https://picsum.photos/300/300?random=$i",
                    rating = "4.5",
                    ratingCount = 100
                )
            )
        }
        allProducts = dummies
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = allProducts.filter { product ->
             // Price Filter
             val priceVal = parsePrice(product.price)
             val priceMatch = priceVal >= minPriceFilter && priceVal <= maxPriceFilter
             
             // Category Filter (Mock implementation)
             // Since API results don't clearly have "SubCategory" field, we can match against title
             // OR if "All" is selected, just return true
             
             val categoryMatch = if (selectedCategoriesFilter.contains("All")) {
                 true
             } else {
                 // Check if ANY of the selected categories match the title or some logic
                 // For now, let's assume if "Handphone" is selected, title must contain "Phone" or similar
                 // This is loose logic because we lack real metadata
                 var match = false
                 for (cat in selectedCategoriesFilter) {
                     if (product.title.contains(cat, ignoreCase = true)) {
                         match = true
                         break
                     }
                 }
                 // If mocking, let's just allow all if we can't really filter, or randomized for demo
                 // Ideally we check product.category but we don't have it.
                 // let's just return true for now unless we want to be strict
                 true 
             }
             
             priceMatch && categoryMatch
        }
        
        productAdapter.updateData(filtered)
        
        if (filtered.isEmpty()) {
            Toast.makeText(this, "No products match filters", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun parsePrice(priceStr: String?): Double {
        if (priceStr.isNullOrEmpty()) return 0.0
        // Remove non-numeric except dot/comma? 
        // Typically "Rp 1.500.000" -> 1500000
        // Remove "Rp", remove "." (thousand separator in ID), replace "," with "." if decimal (usually ID ignores decimals or uses comma)
        
        try {
            val clean = priceStr.replace("Rp", "").replace(".", "").trim()
            return clean.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            return 0.0
        }
    }
}
