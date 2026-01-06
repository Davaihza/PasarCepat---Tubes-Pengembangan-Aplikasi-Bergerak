package com.example.pasarcepat.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Product

class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnBack: ImageView
    
    // Containers
    private lateinit var scrollViewInitial: NestedScrollView
    private lateinit var layoutHistory: LinearLayout
    private lateinit var layoutSuggestions: LinearLayout
    private lateinit var layoutFeatured: LinearLayout
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var progressBar: ProgressBar

    // Recyclers
    private lateinit var rvHistory: RecyclerView
    private lateinit var rvSuggestions: RecyclerView
    private lateinit var rvFeatured: RecyclerView

    // Adapters
    private lateinit var historyAdapter: TextIconAdapter
    private lateinit var suggestionsAdapter: TextIconAdapter
    private lateinit var featuredAdapter: ProductAdapter
    private lateinit var searchResultsAdapter: ProductAdapter

    // Data
    private var historyList = mutableListOf("TMA2 Wireless", "Cable", "Macbook")
    private val allSuggestions = listOf("Samsung", "Samsung Galaxy S10", "Sandal", "Shoes", "Sony", "Smart TV")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupRecyclerViews()
        setupListeners()
        
        // Initial State
        showInitialState()
        loadFeaturedData()
    }

    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        btnBack = findViewById(R.id.btnBack)
        
        scrollViewInitial = findViewById(R.id.scrollViewInitial)
        layoutHistory = findViewById(R.id.layoutHistory)
        layoutSuggestions = findViewById(R.id.layoutSuggestions)
        layoutFeatured = findViewById(R.id.layoutFeatured)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        
        rvSearchResults = findViewById(R.id.rvSearchResults)
        rvHistory = findViewById(R.id.rvHistory)
        rvSuggestions = findViewById(R.id.rvSuggestions)
        rvFeatured = findViewById(R.id.rvFeatured)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerViews() {
        // History
        historyAdapter = TextIconAdapter(historyList, isHistory = true, onItemClick = { query ->
            performSearch(query)
        }, onDeleteClick = { item ->
            historyList.remove(item)
            historyAdapter.updateData(historyList)
            if (historyList.isEmpty()) layoutHistory.visibility = View.GONE
        })
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter

        // Suggestions
        suggestionsAdapter = TextIconAdapter(emptyList(), isHistory = false, onItemClick = { query ->
            performSearch(query)
        })
        rvSuggestions.layoutManager = LinearLayoutManager(this)
        rvSuggestions.adapter = suggestionsAdapter

        // Featured (Grid 2 columns)
        featuredAdapter = ProductAdapter(emptyList(), { product ->
            navigateToDetail(product)
        })
        rvFeatured.layoutManager = GridLayoutManager(this, 2)
        rvFeatured.adapter = featuredAdapter

        // Search Results (Grid 2 columns)
        searchResultsAdapter = ProductAdapter(emptyList(), { product ->
            navigateToDetail(product)
        })
        rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        rvSearchResults.adapter = searchResultsAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        etSearch.addTextChangedListener { text ->
            val query = text.toString()
            if (query.isEmpty()) {
                showInitialState()
            } else {
                showSuggestionsState(query)
            }
        }

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val query = etSearch.text.toString()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun showInitialState() {
        scrollViewInitial.visibility = View.VISIBLE
        rvSearchResults.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        
        layoutHistory.visibility = if (historyList.isNotEmpty()) View.VISIBLE else View.GONE
        layoutFeatured.visibility = View.VISIBLE
        layoutSuggestions.visibility = View.GONE // Hide suggestions
    }
    
    private fun showSuggestionsState(query: String) {
        scrollViewInitial.visibility = View.VISIBLE
        rvSearchResults.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        
        layoutHistory.visibility = View.GONE
        layoutFeatured.visibility = View.GONE
        layoutSuggestions.visibility = View.VISIBLE
        
        // Filter suggestions
        val filtered = allSuggestions.filter { it.contains(query, ignoreCase = true) }
        suggestionsAdapter.updateData(filtered)
    }
    
    private fun performSearch(query: String) {
        etSearch.setText(query)
        etSearch.clearFocus()
        
        // Add to history
        if (!historyList.contains(query)) {
            historyList.add(0, query)
            historyAdapter.updateData(historyList)
        }
        
        // Show Loading/Results
        scrollViewInitial.visibility = View.GONE
        layoutSuggestions.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        rvSearchResults.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        
        // Simulate API delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
            
            // Mock Search Logic
            val results = getDummyProducts(query) // Or reuse filter logic
            if (results.isEmpty()) {
                layoutEmpty.visibility = View.VISIBLE
            } else {
                rvSearchResults.visibility = View.VISIBLE
                searchResultsAdapter.updateData(results)
            }
        }, 1000)
    }

    private fun navigateToDetail(product: Product) {
        val intent = android.content.Intent(this, DetailActivity::class.java)
        intent.putExtra("PRODUCT_DATA", product)
        startActivity(intent)
    }
    
    private fun loadFeaturedData() {
        // Reuse dummy data generator or fetch
        // Just mocking for now
        featuredAdapter.updateData(getDummyProducts("Featured"))
    }
    
    private fun getDummyProducts(type: String): List<Product> {
        // If query is "empty" or "fail", return empty list to test empty state
        if (type.equals("Tmil", ignoreCase = true) || type.equals("fail", ignoreCase = true)) {
            return emptyList()
        }
        
        val products = mutableListOf<Product>()
        val count = if (type == "Featured") 4 else 10
        for (i in 1..count) {
             products.add(
                Product(
                    asin = "search_${type}_$i",
                    title = "$type Item #$i",
                    price = "Rp ${100_000 + (i * 25_000)}",
                    photoUrl = "https://picsum.photos/200/200?random=${type.hashCode() + i}",
                    rating = "4.${i % 5 + 5}",
                    ratingCount = 50 * i,
                    description = "Description for $type item $i"
                )
            )
        }
        return products
    }
}
