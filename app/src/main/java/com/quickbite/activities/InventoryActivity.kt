package com.quickbite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.adapters.InventoryAdapter
import com.quickbite.databinding.ActivityInventoryBinding
import com.quickbite.models.InventoryItem

class InventoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInventoryBinding
    private lateinit var inventoryAdapter: InventoryAdapter
    private val firestore by lazy { FirebaseFirestore.getInstance() }

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
        inventoryAdapter = InventoryAdapter(
            onUpdateClick = { inventoryItem ->
                // TODO: Handle click on update button
                Toast.makeText(this, "Updating ${inventoryItem.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { inventoryItem ->
                val intent = Intent(this, AddEditInventoryItemActivity::class.java)
                intent.putExtra("inventory_item", inventoryItem)
                startActivity(intent)
            }
        )
        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(this@InventoryActivity)
            adapter = inventoryAdapter
        }
    }

    private fun loadInventory() {
        firestore.collection("inventory").get()
            .addOnSuccessListener { documents ->
                val inventoryList = documents.toObjects(InventoryItem::class.java)
                inventoryAdapter.submitList(inventoryList)
            }
    }
}
