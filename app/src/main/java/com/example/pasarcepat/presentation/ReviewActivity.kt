package com.example.pasarcepat.presentation

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.Review

class ReviewActivity : AppCompatActivity() {

    private lateinit var rvReviews: RecyclerView
    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvReviews = findViewById(R.id.rvReviews)
        adapter = ReviewAdapter(getDummyReviews())
        rvReviews.layoutManager = LinearLayoutManager(this)
        rvReviews.adapter = adapter
    }

    private fun getDummyReviews(): List<Review> {
        return listOf(
            Review(
                "1",
                "Yelena Belova",
                "https://randomuser.me/api/portraits/women/1.jpg",
                4,
                "2 Minggu yang lalu",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Review(
                "2",
                "Stephen Strange",
                "https://randomuser.me/api/portraits/men/2.jpg",
                5,
                "1 Bulan yang lalu",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Review(
                "3",
                "Peter Parker",
                "https://randomuser.me/api/portraits/men/3.jpg",
                4,
                "2 Bulan yang lalu",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
             Review(
                "4",
                "T'chala",
                "https://randomuser.me/api/portraits/men/4.jpg",
                3,
                "1 Bulan yang lalu",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
             Review(
                "5",
                "Tony Stark",
                "https://randomuser.me/api/portraits/men/5.jpg",
                5,
                "2 Bulan yang lalu",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
             Review(
                "6",
                "Peter Quil",
                "https://randomuser.me/api/portraits/men/6.jpg",
                4,
                "1 Bulan yang lalu",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            )
        )
    }
}
