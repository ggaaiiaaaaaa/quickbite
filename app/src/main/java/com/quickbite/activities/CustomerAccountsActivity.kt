package com.quickbite.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.adapters.CustomerAccountsAdapter
import com.quickbite.databinding.ActivityCustomerAccountsBinding
import com.quickbite.models.User

class CustomerAccountsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerAccountsBinding
    private lateinit var adapter: CustomerAccountsAdapter
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadCustomers()
    }

    override fun onResume() {
        super.onResume()
        loadCustomers()
    }

    private fun setupRecyclerView() {
        adapter = CustomerAccountsAdapter { customer ->
            // TODO: Handle click to view order history
            Toast.makeText(
                this,
                "Viewing order history for ${customer.name}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvCustomerAccounts.apply {
            layoutManager = LinearLayoutManager(this@CustomerAccountsActivity)
            adapter = this@CustomerAccountsActivity.adapter
        }
    }

    private fun loadCustomers() {
        firestore.collection("users")
            .whereEqualTo("role", "customer")
            .get()
            .addOnSuccessListener { documents ->
                val customers = documents.toObjects(User::class.java)
                adapter.submitList(customers)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load customers: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
