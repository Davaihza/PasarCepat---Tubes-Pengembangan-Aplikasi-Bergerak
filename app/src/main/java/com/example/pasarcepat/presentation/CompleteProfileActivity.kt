package com.example.pasarcepat.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.pasarcepat.R

class CompleteProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        val email = intent.getStringExtra("EMAIL") ?: ""

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etReferral = findViewById<EditText>(R.id.etReferral)
        val btnConfirmation = findViewById<Button>(R.id.btnConfirmation)
        val ivTogglePassword = findViewById<ImageView>(R.id.ivTogglePassword)

        var isPasswordVisible = false
        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                ivTogglePassword.setImageResource(R.drawable.ic_visibility)
            } else {
                etPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off)
            }
            etPassword.setSelection(etPassword.text.length)
        }

        btnConfirmation.setOnClickListener {
            val fullName = etFullName.text.toString()
            val password = etPassword.text.toString()
            val referral = etReferral.text.toString()

            if (fullName.isNotEmpty() && password.isNotEmpty()) {
                if (password.length >= 6) {
                    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val uid = user?.uid ?: ""
                                
                                // Save additional user data
                                val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                val userRef = database.getReference("users").child(uid)
                                
                                val userData = hashMapOf(
                                    "fullName" to fullName,
                                    "email" to email,
                                    "referral" to referral
                                )
                                
                                userRef.setValue(userData).addOnCompleteListener {
                                     Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
                                     val intent = Intent(this, LoginActivity::class.java)
                                     intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                     startActivity(intent)
                                     finish()
                                }
                            } else {
                                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                     Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
