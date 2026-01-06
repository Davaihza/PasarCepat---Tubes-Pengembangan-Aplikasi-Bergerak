package com.example.pasarcepat.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.AmazonResponse
import com.example.pasarcepat.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {


    private lateinit var rvFeatured: RecyclerView
    private lateinit var rvBestSellers: RecyclerView
    private lateinit var rvNewArrivals: RecyclerView
    private lateinit var rvTopRated: RecyclerView
    private lateinit var rvSpecialOffers: RecyclerView
    
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    
    private lateinit var adapterFeatured: ProductAdapter
    private lateinit var adapterBestSellers: ProductAdapter
    private lateinit var adapterNewArrivals: ProductAdapter
    private lateinit var adapterTopRated: ProductAdapter
    private lateinit var adapterSpecialOffers: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvFeatured = findViewById(R.id.rvFeatured)
        rvBestSellers = findViewById(R.id.rvBestSellers)
        rvNewArrivals = findViewById(R.id.rvNewArrivals)
        rvTopRated = findViewById(R.id.rvTopRated)
        rvSpecialOffers = findViewById(R.id.rvSpecialOffers)
        
        progressBar = findViewById(R.id.progressBar)
        searchView = findViewById(R.id.searchView)
        
        findViewById<android.widget.ImageButton>(R.id.btnOpenCart).setOnClickListener {
            startActivity(android.content.Intent(this, CartActivity::class.java))
        }

        findViewById<android.widget.ImageView>(R.id.btnWishlist).setOnClickListener {
            startActivity(android.content.Intent(this, WishlistActivity::class.java))
        }


        setupRecyclerViews()
        setupSearch()
        setupBottomNav()
        setupCategories()
        
        // Initial load with random mixed categories for both sections
        loadHomeData()
        setupNews()
    }
    
    private fun setupBottomNav() {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home // Highlight Home by default
        
        bottomNav.setOnItemSelectedListener { item ->
            val intent: android.content.Intent? = when(item.itemId) {
                R.id.nav_home -> null // Already here
                R.id.nav_wishlist -> android.content.Intent(this, WishlistActivity::class.java)
                R.id.nav_order -> android.content.Intent(this, OrderActivity::class.java)
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
    
    private fun setupNews() {
        val rvNews = findViewById<RecyclerView>(R.id.rvNews)
        rvNews.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        
        val newsData = listOf(
            com.example.pasarcepat.data.model.News(
                "Philosophy That Addresses Topics Such As Goodness", 
                "13 Jan 2021", 
                "Agar tetap kinclong, bodi motor tentu harus dirawat...",
                "https://picsum.photos/200/200?random=1"
            ),
             com.example.pasarcepat.data.model.News(
                "Many Inquiries Outside Of Academia Are Philosophical In The Broad Sense", 
                "13 Jan 2021", 
                "In one general sense, philosophy is associated with wisdom...",
                "https://picsum.photos/200/200?random=2"
            ),
             com.example.pasarcepat.data.model.News(
                "Tips Merawat Bodi Mobil agar Tidak Terlihat Kusam", 
                "13 Jan 2021", 
                "Agar tetap kinclong, bodi mobil tentu harus dirawat dengan baik...",
                "https://picsum.photos/200/200?random=3"
            )
        )
        
        rvNews.adapter = NewsAdapter(newsData) { news ->
            val intent = android.content.Intent(this, DetailNewsActivity::class.java)
            intent.putExtra("EXTRA_NEWS", news)
            startActivity(intent)
        }

        findViewById<View>(R.id.btnSeeAllNews).setOnClickListener {
            startActivity(android.content.Intent(this, NewsActivity::class.java))
        }
    }
    
    private fun setupCategories() {
        findViewById<View>(R.id.catFoods).setOnClickListener { navigateToCategory("Food") }
        findViewById<View>(R.id.catGift).setOnClickListener { navigateToCategory("Gift") }
        findViewById<View>(R.id.catFashion).setOnClickListener { navigateToCategory("Fashion") }
        findViewById<View>(R.id.catCompute).setOnClickListener { navigateToCategory("Computer") }

        findViewById<View>(R.id.btnSeeAllCategories).setOnClickListener {
            showAllCategoriesDialog()
        }
    }

    private fun navigateToCategory(category: String) {
        val intent = android.content.Intent(this, CategoryProductActivity::class.java)
        intent.putExtra("EXTRA_CATEGORY", category)
        startActivity(intent)
    }

    private fun showAllCategoriesDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_all_categories, null)
        dialog.setContentView(view)

        view.findViewById<View>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showProductActionDialog(product: com.example.pasarcepat.data.model.Product) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_product_action, null)
        dialog.setContentView(view)

        view.findViewById<View>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }
        
        view.findViewById<View>(R.id.actionWishlist).setOnClickListener {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
                val wishRef = database.getReference("users").child(user.uid).child("wishlist")
                val wishId = wishRef.push().key ?: return@setOnClickListener
                wishRef.child(wishId).setValue(product).addOnSuccessListener {
                     Toast.makeText(this, "Added to Wishlist: ${product.title}", Toast.LENGTH_SHORT).show()
                }
            } else {
                 Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        
        view.findViewById<View>(R.id.actionShare).setOnClickListener {
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_TEXT, "Check out this product: ${product.title} - ${product.price}")
                type = "text/plain"
            }
            startActivity(android.content.Intent.createChooser(shareIntent, "Share via"))
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnAddToCart).setOnClickListener {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
                val cartRef = database.getReference("users").child(user.uid).child("cart")
                val cartId = cartRef.push().key ?: return@setOnClickListener
                
                val cartItem = com.example.pasarcepat.data.model.CartItem(
                    id = cartId,
                    asin = product.asin,
                    title = product.title,
                    price = product.price ?: "0",
                    imageUrl = product.photoUrl ?: "",
                    quantity = 1
                )
                cartRef.child(cartId).setValue(cartItem).addOnSuccessListener {
                    Toast.makeText(this, "Added to Cart: ${product.title}", Toast.LENGTH_SHORT).show()
                }
            } else {
                 Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupRecyclerViews() {
        val commonClickListener = { product: com.example.pasarcepat.data.model.Product ->
            val intent = android.content.Intent(this, DetailActivity::class.java)
            intent.putExtra("PRODUCT_DATA", product)
            startActivity(intent)
        }

        val commonMoreClickListener = { product: com.example.pasarcepat.data.model.Product ->
            showProductActionDialog(product)
        }

        adapterFeatured = ProductAdapter(emptyList(), commonClickListener, commonMoreClickListener)
        rvFeatured.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rvFeatured.adapter = adapterFeatured
        
        adapterBestSellers = ProductAdapter(emptyList(), commonClickListener, commonMoreClickListener)
        rvBestSellers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rvBestSellers.adapter = adapterBestSellers

        adapterNewArrivals = ProductAdapter(emptyList(), commonClickListener, commonMoreClickListener)
        rvNewArrivals.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rvNewArrivals.adapter = adapterNewArrivals

        adapterTopRated = ProductAdapter(emptyList(), commonClickListener, commonMoreClickListener)
        rvTopRated.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rvTopRated.adapter = adapterTopRated

        adapterSpecialOffers = ProductAdapter(emptyList(), commonClickListener, commonMoreClickListener)
        rvSpecialOffers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rvSpecialOffers.adapter = adapterSpecialOffers
    }
    
    private fun loadHomeData() {
        // Load Featured (e.g. Headphones)
        fetchProducts("Headphone", adapterFeatured)
        // Load Best Sellers (e.g. Tools/Drills)
        fetchProducts("Drill", adapterBestSellers)
        // Load New Arrivals (e.g. Gaming)
        fetchProducts("Gaming", adapterNewArrivals)
        // Load Top Rated (e.g. Watch)
        fetchProducts("Watch", adapterTopRated)
        // Load Special Offers (e.g. Shoes)
        fetchProducts("Shoes", adapterSpecialOffers)
    }

    private fun getDummyProducts(type: String): List<com.example.pasarcepat.data.model.Product> {
        val products = mutableListOf<com.example.pasarcepat.data.model.Product>()
        for (i in 1..5) {
            products.add(
                com.example.pasarcepat.data.model.Product(
                    asin = "dummy_${type}_$i",
                    title = "$type Item #$i - Premium Quality",
                    price = "Rp ${100_000 + (i * 25_000)}",
                    photoUrl = "https://picsum.photos/200/200?random=${type.hashCode() + i}",
                    rating = "4.${i % 5 + 5}",
                    ratingCount = 100 * i,
                    description = "This is a high quality $type that you will love. It has many features and is very durable."
                )
            )
        }
        return products
    }

    private fun setupSearch() {
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startActivity(android.content.Intent(this, SearchActivity::class.java))
                searchView.clearFocus() // Remove focus so it doesn't stay focused when returning
            }
        }
        
        // Also handle click just in case
        searchView.setOnClickListener {
             startActivity(android.content.Intent(this, SearchActivity::class.java))
        }
    }

    private fun fetchProducts(query: String, targetAdapter: ProductAdapter) {
        // progressBar.visibility = View.VISIBLE // Optional: Don't show global progress for individual section loads to avoid flickering
        
        val call = RetrofitClient.instance.searchProducts(query)
        
        call.enqueue(object : Callback<AmazonResponse> {
            override fun onResponse(
                call: Call<AmazonResponse>,
                response: Response<AmazonResponse>
            ) {
                // progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val products = response.body()?.data?.products
                    if (!products.isNullOrEmpty()) {
                        targetAdapter.updateData(products)
                    } else {
                        // Fallback if list is empty
                        useDummyData(query, targetAdapter)
                    }
                } else {
                    Log.e("API_ERROR", "Error: ${response.code()} ${response.message()}")
                    // Fallback on API error (e.g. 429 Too Many Requests)
                    useDummyData(query, targetAdapter)
                }
            }

            override fun onFailure(call: Call<AmazonResponse>, t: Throwable) {
                // progressBar.visibility = View.GONE
                Log.e("API_ERROR", "Failure: ${t.message}")
                // Fallback on Network failure
                useDummyData(query, targetAdapter)
            }
        })
    }

    private fun useDummyData(query: String, targetAdapter: ProductAdapter) {
        Log.d("FALLBACK", "Using dummy data for $query")
        targetAdapter.updateData(getDummyProducts(query))
    }
}
