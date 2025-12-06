package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.quickbite.R
import com.quickbite.databinding.ActivityOrderTrackingBinding
import com.quickbite.utils.DatabaseHelper
import com.quickbite.utils.NotificationHelper
import kotlinx.coroutines.launch

class OrderTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderTrackingBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var notificationHelper: NotificationHelper
    private var orderId = -1
    private var orderNumber = ""
    private var currentStatus = "Received"
    private val handler = Handler(Looper.getMainLooper())

    private val statusCheckRunnable = object : Runnable {
        override fun run() {
            checkOrderStatus()
            handler.postDelayed(this, 5000) // Check every 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper()
        notificationHelper = NotificationHelper(this)

        orderId = intent.getIntExtra("order_id", -1)
        orderNumber = intent.getStringExtra("order_number") ?: ""

        setupToolbar()
        displayOrderNumber()
        startStatusPolling()

        binding.btnDone.setOnClickListener {
            navigateToRating()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun displayOrderNumber() {
        binding.tvOrderNumber.text = orderNumber
    }

    private fun startStatusPolling() {
        handler.post(statusCheckRunnable)
    }

    private fun checkOrderStatus() {
        lifecycleScope.launch {
            try {
                val response = dbHelper.getOrderStatus(orderId)
                if (response.success) {
                    updateOrderStatus(response.status, response.estimatedTime)
                }
            } catch (e: Exception) {
                // Handle silently for polling
            }
        }
    }

    private fun updateOrderStatus(status: String, estimatedTime: Int) {
        if (status != currentStatus) {
            currentStatus = status
            notificationHelper.showOrderStatusNotification(orderNumber, status)
        }

        binding.tvEstimatedTime.text = "Estimated: $estimatedTime minutes"

        when (status) {
            "Received" -> {
                updateStatusUI(1, 0, 0, 0)
            }
            "Preparing" -> {
                updateStatusUI(1, 1, 0, 0)
            }
            "Ready" -> {
                updateStatusUI(1, 1, 1, 0)
                binding.btnDone.visibility = View.VISIBLE
            }
            "Completed" -> {
                updateStatusUI(1, 1, 1, 1)
                stopStatusPolling()
                binding.btnDone.visibility = View.VISIBLE
            }
        }
    }

    private fun updateStatusUI(status1: Int, status2: Int, status3: Int, status4: Int) {
        // Update status 1
        binding.ivStatus1.setColorFilter(
            if (status1 == 1) getColor(R.color.dunkin_orange) else getColor(android.R.color.darker_gray)
        )
        binding.lineStatus1.setBackgroundColor(
            if (status2 == 1) getColor(R.color.dunkin_orange) else getColor(android.R.color.darker_gray)
        )

        // Update status 2
        binding.ivStatus2.setColorFilter(
            if (status2 == 1) getColor(R.color.dunkin_orange) else getColor(android.R.color.darker_gray)
        )
        binding.lineStatus2.setBackgroundColor(
            if (status3 == 1) getColor(R.color.dunkin_orange) else getColor(android.R.color.darker_gray)
        )

        // Update status 3
        binding.ivStatus3.setColorFilter(
            if (status3 == 1) getColor(R.color.dunkin_orange) else getColor(android.R.color.darker_gray)
        )
        binding.lineStatus3.setBackgroundColor(
            if (status4 == 1) getColor(R.color.dunkin_orange) else getColor(android.R.color.darker_gray)
        )

        // Update status 4
        binding.ivStatus4.setColorFilter(
            if (status4 == 1) getColor(R.color.dunkin_orange) else getColor(android.R.color.darker_gray)
        )
    }

    private fun stopStatusPolling() {
        handler.removeCallbacks(statusCheckRunnable)
    }

    private fun navigateToRating() {
        val intent = Intent(this, OrderCompleteActivity::class.java)
        intent.putExtra("order_id", orderId)
        intent.putExtra("order_number", orderNumber)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStatusPolling()
    }
}