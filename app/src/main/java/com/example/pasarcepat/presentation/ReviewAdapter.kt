package com.example.pasarcepat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Review

class ReviewAdapter(
    private var reviews: List<Review>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivUserAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        val containerStars: LinearLayout = itemView.findViewById(R.id.layoutRating_container)
        val tvDate: TextView = itemView.findViewById(R.id.tvReviewDate)
        val tvContent: TextView = itemView.findViewById(R.id.tvReviewContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        
        holder.tvName.text = review.userName
        holder.tvDate.text = review.date
        holder.tvContent.text = review.content
        
        // Handle stars dynamically roughly based on rating int
        val starContainer = holder.containerStars
        
        // Simple star coloring logic
        for (i in 0 until starContainer.childCount) {
            val star = starContainer.getChildAt(i) as? ImageView
            if (i < review.rating) {
                star?.setColorFilter(android.graphics.Color.parseColor("#FFC107")) // Yellow
            } else {
                star?.setColorFilter(android.graphics.Color.parseColor("#E0E0E0")) // Grey
            }
        }
        
        Glide.with(holder.itemView.context)
            .load(review.userAvatarUrl)
            .placeholder(R.drawable.ic_account)
            .circleCrop()
            .into(holder.ivAvatar)
    }

    override fun getItemCount() = reviews.size
    
    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
