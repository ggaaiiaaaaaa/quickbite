package com.quickbite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.adapters.OrderAdapter
import com.quickbite.databinding.ActivityLiveOrdersBinding
import com.quickbite.models.Order

class LiveOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveOrdersBinding
    private lateinit var orderAdapter: OrderAdapter
    private val orders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Live Orders"

        setupRecyclerView()
        loadOrders()

        binding.btnSortByTime.setOnClickListener {
            orders.sortBy { it.orderId } // Assuming orderId is a timestamp
            orderAdapter.notifyDataSetChanged() // In a real app, use DiffUtil for better performance
            Toast.makeText(this, "Sorted by time", Toast.LENGTH_SHORT).show()
        }

        binding.btnSortByType.setOnClickListener {
            orders.sortBy { it.orderType }
            orderAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Sorted by type", Toast.LENGTH_SHORT).show()
        }

        binding.btnSortByPriority.setOnClickListener {
            // TODO: Implement sorting by priority
            Toast.makeText(this, "Sorting by priority not yet implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(orders) { order ->
            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtra("order", order)
            startActivity(intent)
        }
        binding.rvLiveOrders.apply {
            layoutManager = LinearLayoutManager(this@LiveOrdersActivity)
            adapter = orderAdapter
        }
    }

    private fun loadOrders() {
        // TODO: Replace with your actual data from Firebase Firestore
        val newOrders = listOf(
            Order("1", listOf("Burger"), "No onions", "John Doe", "5", "table", "Received"),
            Order("2", listOf("Pizza"), "", "Jane Smith", "", "pickup", "Preparing")
        )
        val startPosition = orders.size
        orders.addAll(newOrders)
        orderAdapter.notifyItemRangeInserted(startPosition, newOrders.size)
    }
}
