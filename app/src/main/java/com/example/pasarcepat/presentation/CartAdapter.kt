package com.example.pasarcepat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.CartItem

class CartAdapter(
    private var items: List<CartItem>,
    private val onAction: (CartItem, String) -> Unit // action: "delete", "plus", "minus", "check" (optional)
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivCartItem)
        val tvName: TextView = itemView.findViewById(R.id.tvCartName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val btnPlus: ImageButton = itemView.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = itemView.findViewById(R.id.btnMinus)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val cbItem: android.widget.CheckBox = itemView.findViewById(R.id.cbItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.title
        holder.tvPrice.text = item.price
        holder.tvQuantity.text = item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.ivProduct)

        holder.btnDelete.setOnClickListener { onAction(item, "delete") }
        holder.btnPlus.setOnClickListener { onAction(item, "plus") }
        holder.btnMinus.setOnClickListener { onAction(item, "minus") }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
