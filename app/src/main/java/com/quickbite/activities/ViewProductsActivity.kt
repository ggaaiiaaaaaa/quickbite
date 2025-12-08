package com.quickbite.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.R
import com.quickbite.adapters.ProductAdapter
import com.quickbite.databinding.ActivityViewProductsBinding
import com.quickbite.models.Product

class ViewProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewProductsBinding
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "View Products"

        setupRecyclerView()

        // TODO: Load products from your database or API
        val productList = getPlaceholderProducts()
        productAdapter.submitList(productList)

        binding.fabAddProduct.setOnClickListener {
            // TODO: Handle adding a new product
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onEditClick = { product ->
                // TODO: Handle edit product
            },
            onDeleteClick = { product ->
                // TODO: Handle delete product
            }
        )
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@ViewProductsActivity)
        }
    }

    // Placeholder function to generate sample product data
    private fun getPlaceholderProducts(): List<Product> {
        return listOf(
            Product("1", "Product 1", "", 10.99, "", 0.0, 0, ""),
            Product("2", "Product 2", "", 12.99, "", 0.0, 0, ""),
            Product("3", "Product 3", "", 8.99, "", 0.0, 0, ""),
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
