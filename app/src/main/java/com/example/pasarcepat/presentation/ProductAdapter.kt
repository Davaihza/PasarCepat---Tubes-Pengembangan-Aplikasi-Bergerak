package com.example.pasarcepat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit,
    private val onMoreClick: (Product) -> Unit = {} // Default empty for backward compatibility if needed, but better to enforce
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvReviews: TextView = itemView.findViewById(R.id.tvReviews)
        val btnMore: ImageView = itemView.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text = product.title
        holder.tvPrice.text = product.price ?: "N/A"
        
        holder.tvRating.text = product.rating ?: "-"
        holder.tvReviews.text = "${product.ratingCount ?: 0} Reviews"

        Glide.with(holder.itemView.context)
            .load(product.photoUrl)
            .placeholder(R.drawable.ic_app_logo)
            .into(holder.ivProduct)

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        holder.btnMore.setOnClickListener {
            onMoreClick(product)
        }
    }

    override fun getItemCount() = products.size
    
    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
