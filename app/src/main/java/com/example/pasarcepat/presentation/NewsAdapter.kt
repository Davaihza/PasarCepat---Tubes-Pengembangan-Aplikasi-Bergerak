package com.example.pasarcepat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.News

class NewsAdapter(
    private val newsList: List<News>,
    private val onItemClick: ((News) -> Unit)? = null
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivNewsImage)
        val tvTitle: TextView = view.findViewById(R.id.tvNewsTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvNewsDesc)
        val tvDate: TextView = view.findViewById(R.id.tvNewsDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.tvTitle.text = news.title
        holder.tvDesc.text = news.description
        holder.tvDate.text = news.date
        
        Glide.with(holder.itemView.context)
            .load(news.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.ivImage)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(news)
        }
    }

    override fun getItemCount() = newsList.size
}
