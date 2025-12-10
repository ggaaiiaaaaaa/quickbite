package com.quickbite.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.R
import com.quickbite.adapters.CustomerAccountsAdapter
import com.quickbite.databinding.ActivityCustomerAccountsBinding
import com.quickbite.models.User

class CustomerAccountsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerAccountsBinding
    private lateinit var adapter: CustomerAccountsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Replace with actual data from your database
        val customers = listOf(
            User(name = "John Doe", email = "john.doe@example.com"),
            User(name = "Jane Smith", email = "jane.smith@example.com")
        )

        adapter = CustomerAccountsAdapter(customers)
        binding.rvCustomerAccounts.adapter = adapter
        binding.rvCustomerAccounts.layoutManager = LinearLayoutManager(this)
    }
}
