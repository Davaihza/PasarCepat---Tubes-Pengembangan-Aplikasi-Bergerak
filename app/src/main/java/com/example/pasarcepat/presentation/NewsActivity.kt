package com.example.pasarcepat.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pasarcepat.R
import com.example.pasarcepat.data.model.News

class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val btnBack = findViewById<android.widget.ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val rvNewsList = findViewById<RecyclerView>(R.id.rvNewsList)
        rvNewsList.layoutManager = LinearLayoutManager(this)

        val newsData = listOf(
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
            ),
             News(
                "Tips Merawat Bodi Mobil agar Tidak Terlihat Kusam", 
                "13 Jan 2021", 
                "Agar tetap kinclong, bodi mobil tentu harus dirawat dengan baik...",
                "https://picsum.photos/200/200?random=3"
            ),
             News(
                "Many Inquiries Outside Of Academia Are Philosophical In The Broad Sense", 
                "13 Jan 2021", 
                "In one general sense, philosophy is associated with wisdom...",
                "https://picsum.photos/200/200?random=4"
            ),
             News(
                "Tips Merawat Bodi Mobil agar Tidak Terlihat Kusam", 
                "13 Jan 2021", 
                "Agar tetap kinclong, bodi mobil tentu harus dirawat dengan baik...",
                "https://picsum.photos/200/200?random=5"
            )
        )

        val adapter = NewsAdapter(newsData) { news ->
            val intent = Intent(this, DetailNewsActivity::class.java)
            intent.putExtra("EXTRA_NEWS", news)
            startActivity(intent)
        }
        rvNewsList.adapter = adapter
    }
}
