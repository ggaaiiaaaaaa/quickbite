package com.quickbite.activities

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quickbite.databinding.ActivityOrderTypeBinding
import java.util.*

class OrderTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderTypeBinding
    private var orderType = "Dine-In"
    private var tableNumber = ""
    private var pickupTime = "ASAP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupOrderTypeSelection()
        setupContinueButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupOrderTypeSelection() {
        binding.rgOrderType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbDineIn.id -> {
                    orderType = "Dine-In"
                    binding.cardDineInOptions.visibility = View.VISIBLE
                    binding.cardTakeOutOptions.visibility = View.GONE
                }
                binding.rbTakeOut.id -> {
                    orderType = "Take-Out"
                    binding.cardDineInOptions.visibility = View.GONE
                    binding.cardTakeOutOptions.visibility = View.VISIBLE
                }
            }
        }

        // Setup scheduled time picker
        binding.rgPickupTime.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbScheduled.id) {
                binding.tilScheduledTime.visibility = View.VISIBLE
            } else {
                binding.tilScheduledTime.visibility = View.GONE
                pickupTime = "ASAP"
            }
        }

        binding.etScheduledTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            pickupTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.etScheduledTime.setText(pickupTime)
        }, hour, minute, true).show()
    }

    private fun setupContinueButton() {
        binding.btnContinue.setOnClickListener {
            if (validateInput()) {
                val intent = Intent(this, PaymentActivity::class.java).apply {
                    putExtra("order_type", orderType)
                    putExtra("table_number", tableNumber)
                    putExtra("pickup_time", pickupTime)
                }
                startActivity(intent)
            }
        }
    }

    private fun validateInput(): Boolean {
        if (orderType == "Dine-In") {
            tableNumber = binding.etTableNumber.text.toString().trim()
            if (tableNumber.isEmpty()) {
                Toast.makeText(this, "Please enter table number", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }
}