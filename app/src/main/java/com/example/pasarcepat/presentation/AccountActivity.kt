package com.example.pasarcepat.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.pasarcepat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.view.View

class AccountActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var ivTogglePassword: android.widget.ImageView
    private lateinit var btnSave: Button
    private lateinit var btnEdit: Button
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileSubtitle: TextView

    private var isEditing = false
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)
        btnSave = findViewById(R.id.btnSave)
        btnEdit = findViewById(R.id.btnEditProfile)
        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileSubtitle = findViewById(R.id.tvProfileSubtitle)

        setupBottomNav()
        loadUserData()

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnEdit.setOnClickListener {
            toggleEditMode()
        }

        btnSave.setOnClickListener {
            saveUserData()
        }
        
        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                ivTogglePassword.setImageResource(R.drawable.ic_visibility)
            } else {
                etPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off)
            }
            // Move cursor to end
            etPassword.setSelection(etPassword.text.length)
        }
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val database = FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userRef = database.getReference("users").child(user.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Fetch both 'name' (edited) and 'fullName' (original from register)
                    val editedName = snapshot.child("name").value as? String
                    val originalName = snapshot.child("fullName").value as? String
                    val email = snapshot.child("email").value as? String ?: user.email ?: ""
                    val phone = snapshot.child("phone").value as? String ?: ""

                    // Priority: Edited Name -> Original Name -> Google/Auth Display Name
                    val finalName = if (!editedName.isNullOrEmpty()) {
                        editedName
                    } else if (!originalName.isNullOrEmpty()) {
                        originalName
                    } else {
                        user.displayName ?: "User"
                    }

                    etName.setText(finalName)
                    etEmail.setText(email)
                    if (phone.isNotEmpty()) {
                        etPhone.setText(phone)
                    } else {
                        etPhone.setText("")
                        etPhone.hint = "Not phone number added yet"
                    }

                    tvProfileName.text = finalName
                    tvProfileSubtitle.text = email
                } else {
                    // Fallback
                    tvProfileName.text = user.displayName ?: "User"
                    tvProfileSubtitle.text = user.email
                    etEmail.setText(user.email)
                    etPhone.hint = "Not phone number added yet"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AccountActivity, "Failed to load user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserData() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val newPassword = etPassword.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "Name is required"
            return
        }

        if (newPassword.isNotEmpty() && newPassword.length < 6) {
            etPassword.error = "Min 6 characters required"
            return
        }

        val database = FirebaseDatabase.getInstance("https://pasarcepat-dcf94-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userRef = database.getReference("users").child(user.uid)

        val updates = mutableMapOf<String, Any>(
            "name" to name,
            "email" to email,
            "phone" to phone
        )

        // Function to proceed with DB update after Auth checks
        fun updateDatabase() {
            userRef.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                toggleEditMode()
                etPassword.text.clear()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update profile: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Logic to handle Auth updates (Email/Password)
        val emailChanged = email != user.email
        val passwordChanged = newPassword.isNotEmpty()

        if (emailChanged || passwordChanged) {
            // We need to update Auth credentials. This might require re-login.
            
            // Helper to handle email update after potential password update
            fun updateEmailIfNeeded() {
                if (emailChanged) {
                    user.updateEmail(email).addOnCompleteListener { task ->
                       if (task.isSuccessful) {
                           updateDatabase()
                       } else {
                           val msg = task.exception?.message ?: "Unknown error"
                           if (msg.contains("recent login", ignoreCase = true)) {
                               Toast.makeText(this, "Security: Please Logout & Login again to change Email.", Toast.LENGTH_LONG).show()
                           } else {
                               Toast.makeText(this, "Email Update Failed: $msg", Toast.LENGTH_LONG).show()
                           }
                           // Revert DB changes logic if needed, but here we just don't update DB
                       }
                    }
                } else {
                    updateDatabase()
                }
            }

            if (passwordChanged) {
                user.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password updated.", Toast.LENGTH_SHORT).show()
                        updateEmailIfNeeded()
                    } else {
                        val msg = task.exception?.message ?: "Unknown error"
                        if (msg.contains("recent login", ignoreCase = true)) {
                            Toast.makeText(this, "Security: Please Logout & Login again to change Password.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Password Update Failed: $msg", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                updateEmailIfNeeded()
            }
        } else {
            // No sensitive changes, just update DB
            updateDatabase()
        }
    }
    
    private fun toggleEditMode() {
        isEditing = !isEditing
        etName.isEnabled = isEditing
        etEmail.isEnabled = isEditing
        etPhone.isEnabled = isEditing
        etPassword.isEnabled = isEditing 

        btnSave.visibility = if (isEditing) View.VISIBLE else View.GONE
        btnEdit.text = if (isEditing) "Cancel" else "Edit Profile"
        
        if (isEditing) {
             etPassword.hint = "Min 6 chars to change"
        } else {
             etPassword.hint = "********"
             loadUserData() // Revert changes
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_account

        bottomNav.setOnItemSelectedListener { item ->
            val intent: Intent? = when(item.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_wishlist -> Intent(this, WishlistActivity::class.java)
                R.id.nav_order -> Intent(this, OrderActivity::class.java)
                R.id.nav_account -> null // Already here
                else -> null
            }
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                overridePendingTransition(0, 0)
                false
            } else {
                true
            }
        }
    }
}
