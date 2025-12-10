package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.quickbite.R
import com.quickbite.databinding.ActivityAdminAuthBinding

enum class Role {
    MANAGER,
    KITCHEN_STAFF,
    CASHIER
}

class AdminAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAuthBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupRoleSpinner()

        binding.btnLogin.setOnClickListener { handleLogin() }
    }

    private fun setupRoleSpinner() {
        val roles = Role.values().map { it.name.replace("_", " ").lowercase().replaceFirstChar { char -> char.uppercase() } }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val selectedRole = Role.values()[binding.spinnerRole.selectedItemPosition]

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Implement Two-Factor Authentication

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // For now, we'll just navigate to the dashboard with the role
                    // In a real app, you'd verify the user's role from your database
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    intent.putExtra("USER_ROLE", selectedRole.name)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
