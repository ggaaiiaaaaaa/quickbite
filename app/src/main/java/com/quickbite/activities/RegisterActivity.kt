package com.quickbite.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.quickbite.R
import com.quickbite.databinding.ActivityRegisterBinding
import com.quickbite.utils.DatabaseHelper
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            if (validateInput()) {
                performRegistration()
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.name_is_required)
            return false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.email_is_required)
            return false
        }
        if (phone.isEmpty()) {
            binding.tilPhone.error = getString(R.string.phone_is_required)
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.password_is_required)
            return false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.passwords_do_not_match)
            return false
        }

        return true
    }

    private fun performRegistration() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = dbHelper.registerUser(
                    name = binding.etName.text.toString().trim(),
                    email = binding.etEmail.text.toString().trim(),
                    password = binding.etPassword.text.toString(),
                    phone = binding.etPhone.text.toString().trim()
                )

                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true

                if (response.success) {
                    Toast.makeText(this@RegisterActivity, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                Toast.makeText(this@RegisterActivity, getString(R.string.error_message, e.message), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
