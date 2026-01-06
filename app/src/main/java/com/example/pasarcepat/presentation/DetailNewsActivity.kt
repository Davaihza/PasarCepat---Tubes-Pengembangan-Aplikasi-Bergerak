package com.example.pasarcepat.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.News

class DetailNewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_news)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
        
        val news = intent.getParcelableExtra<News>("EXTRA_NEWS")

        val ivDetailImage = findViewById<ImageView>(R.id.ivDetailImage)
        val tvDetailTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvDetailDate = findViewById<TextView>(R.id.tvDetailDate)
        val tvDetailDesc = findViewById<TextView>(R.id.tvDetailDesc)
        
        if (news != null) {
            tvDetailTitle.text = news.title
            tvDetailDate.text = news.date
            tvDetailDesc.text = "The speaker unit contains a diaphragm that is precision-grown from NAC Audio bio-cellulose, making it stiff, lighter and stronger than regular PET speaker units, and allowing the sound-producing diaphragm to vibrate without the levels of distortion found in other speakers.\n\nThe speaker unit contains a diaphragm that is precision-grown from NAC Audio bio-cellulose, making it stiff, lighter and stronger than regular PET speaker units, and allowing the sound-producing diaphragm to vibrate without the levels of distortion found in other speakers."
            
            Glide.with(this)
                .load(news.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivDetailImage)
        }

        findViewById<Button>(R.id.btnSeeAll).setOnClickListener {
             finish()
        }

        val rvOtherNews = findViewById<RecyclerView>(R.id.rvOtherNews)
        rvOtherNews.layoutManager = LinearLayoutManager(this)
        
        val otherNewsData = listOf(
             News(
                "Philosophy That Addresses Topics Such As Goodness", 
                "13 Jan 2021", 
                "Agar tetap kinclong, bodi motor tentu harus dirawat...",
                "https://picsum.photos/200/200?random=1"
            ),
             News(
                "Many Inquiries Outside Of Academia Are Philosophical In The Broad Sense", 
                "13 Jan 2021", 
                "In one general sense, philosophy is associated with wisdom...",
                "https://picsum.photos/200/200?random=2"
            )
        )
        rvOtherNews.adapter = NewsAdapter(otherNewsData) {
            // Optional: Handle click on other news
        }
    }
}
