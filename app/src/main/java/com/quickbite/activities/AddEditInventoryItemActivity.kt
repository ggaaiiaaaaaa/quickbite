package com.quickbite.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.databinding.ActivityAddEditInventoryItemBinding
import com.quickbite.models.InventoryItem

class AddEditInventoryItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditInventoryItemBinding
    private var inventoryItem: InventoryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditInventoryItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inventoryItem = intent.getParcelableExtra("inventory_item", InventoryItem::class.java)

        if (inventoryItem != null) {
            supportActionBar?.title = "Edit Inventory Item"
            binding.etItemName.setText(inventoryItem!!.name)
            binding.etQuantity.setText(inventoryItem!!.quantity.toString())
            binding.etLowStockThreshold.setText(inventoryItem!!.lowStockThreshold.toString())
        } else {
            supportActionBar?.title = "Add Inventory Item"
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSave.setOnClickListener {
            saveInventoryItem()
        }
    }

    private fun saveInventoryItem() {
        val name = binding.etItemName.text.toString().trim()
        val quantity = binding.etQuantity.text.toString().toIntOrNull()
        val lowStockThreshold = binding.etLowStockThreshold.text.toString().toIntOrNull()

        if (name.isEmpty() || quantity == null || lowStockThreshold == null) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("inventory")

        val id = inventoryItem?.id ?: collection.document().id
        val newItem = InventoryItem(id, name, quantity, lowStockThreshold)

        collection.document(id).set(newItem)
            .addOnSuccessListener { 
                finish()
            }
            .addOnFailureListener { 
                Toast.makeText(this, "Failed to save item", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
