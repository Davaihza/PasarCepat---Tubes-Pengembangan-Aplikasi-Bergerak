package com.example.pasarcepat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvItems: TextView = itemView.findViewById(R.id.tvItems)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        
        holder.tvOrderId.text = "Order #${order.orderId.takeLast(6).uppercase()}"
        
        // Simple date format
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(order.timestamp))
        
        holder.tvStatus.text = order.status
        
        // Items summary
        val itemCount = order.items.sumOf { it.quantity }
        val itemNames = order.items.joinToString(", ") { it.title }
        holder.tvItems.text = "$itemCount Items: $itemNames"
        
        holder.tvTotal.text = "Rp ${order.totalAmount.toLong()}"

        holder.itemView.setOnClickListener {
            onItemClick(order)
        }
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
