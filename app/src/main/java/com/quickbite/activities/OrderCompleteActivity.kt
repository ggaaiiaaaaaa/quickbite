package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.quickbite.databinding.ActivityOrderCompleteBinding
import com.quickbite.utils.DatabaseHelper
import com.quickbite.utils.PreferenceHelper
import kotlinx.coroutines.launch

class OrderCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderCompleteBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefHelper: PreferenceHelper
    private var orderId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper()
        prefHelper = PreferenceHelper(this)

        orderId = intent.getIntExtra("order_id", -1)
        val orderNumber = intent.getStringExtra("order_number") ?: ""

        setupToolbar()
        binding.tvOrderNumber.text = "Order $orderNumber"

        setupButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupButtons() {
        binding.btnSubmitRating.setOnClickListener {
            submitRating()
        }

        binding.btnBackToHome.setOnClickListener {
            navigateToHome()
        }
    }

    private fun submitRating() {
        val rating = binding.ratingBar.rating
        val review = binding.etReview.text.toString()

        lifecycleScope.launch {
            try {
                val response = dbHelper.submitRating(
                    orderId = orderId,
                    userId = prefHelper.getUserId(),
                    rating = rating,
                    review = review
                )

                if (response.success) {
                    Toast.makeText(this@OrderCompleteActivity, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                    binding.btnSubmitRating.isEnabled = false
                }
            } catch (e: Exception) {
                Toast.makeText(this@OrderCompleteActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}