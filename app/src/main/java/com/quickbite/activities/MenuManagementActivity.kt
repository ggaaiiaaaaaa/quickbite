package com.quickbite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.adapters.ProductAdapter
import com.quickbite.databinding.ActivityMenuManagementBinding
import com.quickbite.models.Product

class MenuManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuManagementBinding
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(this, AddEditProductActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Menu Management"
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            val intent = Intent(this, AddEditProductActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@MenuManagementActivity)
        }
    }

    private fun loadProducts() {
        // TODO: Load products from your database
        val products = listOf(
            Product(id = "1", name = "Dunkin' Original Coffee", price = 2.50, category = "Drinks"),
            Product(id = "2", name = "Glazed Donut", price = 1.25, category = "Donuts"),
            Product(id = "3", name = "Breakfast Sandwich", price = 4.50, category = "Sandwiches")
        )
        productAdapter.submitList(products)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
