package com.example.pasarcepat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R

class TextIconAdapter(
    private var items: List<String>,
    private val isHistory: Boolean,
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: ((String) -> Unit)? = null // Only for history
) : RecyclerView.Adapter<TextIconAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvQuery: TextView = view.findViewById(R.id.tvQuery)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvQuery.text = item
        
        if (isHistory) {
            holder.ivIcon.setImageResource(R.drawable.ic_clock)
            holder.ivDelete.visibility = View.VISIBLE
            holder.ivDelete.setOnClickListener { onDeleteClick?.invoke(item) }
        } else {
            // Suggestions
            holder.ivIcon.setImageResource(R.drawable.ic_search) // Use search icon/loupe
            holder.ivDelete.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }
}
