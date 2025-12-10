package com.quickbite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.R
import com.quickbite.adapters.InventoryAdapter
import com.quickbite.databinding.ActivityInventoryBinding
import com.quickbite.models.InventoryItem

class InventoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInventoryBinding
    private lateinit var inventoryAdapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Inventory"

        setupRecyclerView()

        binding.fabAddInventoryItem.setOnClickListener {
            startActivity(Intent(this, AddEditInventoryItemActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadInventory()
    }

    private fun setupRecyclerView() {
        inventoryAdapter = InventoryAdapter { inventoryItem ->
            val intent = Intent(this, AddEditInventoryItemActivity::class.java)
            intent.putExtra("inventoryItem", inventoryItem)
            startActivity(intent)
        }
        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(this@InventoryActivity)
            adapter = inventoryAdapter
        }
    }

    private fun loadInventory() {
        // TODO: Load inventory from Firestore
        val inventoryList = listOf(
            InventoryItem("1", "Flour", 10, 5),
            InventoryItem("2", "Sugar", 20, 10),
            InventoryItem("3", "Eggs", 12, 6)
        )
        inventoryAdapter.submitList(inventoryList)
    }
}