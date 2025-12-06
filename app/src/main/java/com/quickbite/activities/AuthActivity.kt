package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.database.AppDatabase
import com.quickbite.activities.databinding.ActivityAuthBinding
import com.quickbite.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var authRepository: FirebaseAuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth Repository
        authRepository = FirebaseAuthRepository(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance(),
            AppDatabase.getDatabase(this)
        )

        // Check if user is already logged in
        if (authRepository.currentUser != null) {
            navigateToMain()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnGuest.setOnClickListener {
            // For guest mode, create anonymous account
            lifecycleScope.launch {
                authRepository.signInAnonymously().fold(
                    onSuccess = {
                        navigateToMain()
                    },
                    onFailure = { e ->
                        Toast.makeText(this@AuthActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        // Clear errors if input is valid
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        return true
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = {
                    Toast.makeText(this@AuthActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                },
                onFailure = { error ->
                    Toast.makeText(this@AuthActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, BranchSelectActivity::class.java))
        finish()
    }
}
