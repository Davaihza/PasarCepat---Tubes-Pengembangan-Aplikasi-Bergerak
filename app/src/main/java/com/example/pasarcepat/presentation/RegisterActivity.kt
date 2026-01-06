package com.example.pasarcepat.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.pasarcepat.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        tvSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnContinue.setOnClickListener {
            val email = etEmail.text.toString()
            if (email.isNotEmpty()) {
                val intent = Intent(this, CompleteProfileActivity::class.java)
                intent.putExtra("EMAIL", email)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter your email or phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
