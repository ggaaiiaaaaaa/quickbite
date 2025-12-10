package com.quickbite.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.quickbite.R
import com.quickbite.databinding.ActivityOrderDetailsBinding
import com.quickbite.models.Order

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order = intent.getParcelableExtra("order", Order::class.java)

        supportActionBar?.title = "Order Details"

        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        order?.let {
            binding.tvOrderId.text = "Order #${it.orderId}"
            binding.tvCustomerName.text = "Customer: ${it.customerName}"
            binding.tvOrderItems.text = "Items: ${it.items.joinToString()}"
            binding.tvSpecialInstructions.text = "Instructions: ${it.specialInstructions}"
            setupStatusSpinner(it.orderStatus)
        }

        // TODO: Populate kitchen staff spinner from your user database
        val kitchenStaff = arrayOf("Staff 1", "Staff 2", "Staff 3")
        val staffAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kitchenStaff)
        binding.spinnerKitchenStaff.adapter = staffAdapter
    }

    private fun setupStatusSpinner(currentStatus: String) {
        val statusOptions = arrayOf("Received", "Preparing", "Ready for Pick-Up", "Completed", "Cancelled")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        binding.spinnerStatus.adapter = statusAdapter
        val statusPosition = statusOptions.indexOf(currentStatus)
        if (statusPosition >= 0) {
            binding.spinnerStatus.setSelection(statusPosition)
        }
    }

    private fun setupClickListeners() {
        binding.btnUpdateStatus.setOnClickListener {
            val newStatus = binding.spinnerStatus.selectedItem.toString()
            // TODO: Update order status in Firebase Firestore
            Toast.makeText(this, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
        }

        binding.btnSendToKitchen.setOnClickListener {
            val assignedStaff = binding.spinnerKitchenStaff.selectedItem.toString()
            val isPriority = binding.cbPriority.isChecked
            val prepTime = binding.etPrepTime.text.toString()

            if (prepTime.isNotEmpty() && prepTime.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid preparation time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Send order details to kitchen display system and save in Firebase Firestore
            Toast.makeText(this, "Order sent to kitchen", Toast.LENGTH_SHORT).show()
        }
    }
}
