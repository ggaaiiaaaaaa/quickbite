package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.database.AppDatabase
import com.quickbite.databinding.ActivityAuthBinding
import com.quickbite.repository.FirebaseAuthRepository
import com.quickbite.utils.PreferenceHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var authRepository: FirebaseAuthRepository
    private lateinit var prefHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize helpers
        prefHelper = PreferenceHelper(this)

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
            lifecycleScope.launch {
                authRepository.signInAnonymously().fold(
                    onSuccess = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            // Save guest mode
                            prefHelper.setGuestMode(true)
                            prefHelper.saveFirebaseUid(uid)
                            prefHelper.saveUserData(-1, "Guest User", "")
                        }
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
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        return true
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = {
                    // Get user data from Firebase
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        try {
                            val userDoc = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(user.uid)
                                .get()
                                .await()

                            val userName = userDoc.getString("name") ?: user.email ?: "User"
                            val userEmail = user.email ?: ""

                            // Save to preferences
                            prefHelper.saveFirebaseUid(user.uid)
                            prefHelper.saveUserData(user.uid.hashCode(), userName, userEmail)
                            prefHelper.setGuestMode(false)

                            Toast.makeText(this@AuthActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        } catch (e: Exception) {
                            Toast.makeText(this@AuthActivity, "Error loading user data", Toast.LENGTH_SHORT).show()
                        }
                    }
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