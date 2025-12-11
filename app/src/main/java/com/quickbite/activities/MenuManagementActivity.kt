package com.quickbite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.adapters.ProductAdapter
import com.quickbite.databinding.ActivityMenuManagementBinding
import com.quickbite.models.Product

class MenuManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuManagementBinding
    private lateinit var productAdapter: ProductAdapter
    private val firestore by lazy { FirebaseFirestore.getInstance() }

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
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                // TODO: Handle click on product
                Toast.makeText(this, "Clicked on ${product.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { product ->
                val intent = Intent(this, AddEditProductActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@MenuManagementActivity)
        }
    }

    private fun loadProducts() {
        firestore.collection("products").get()
            .addOnSuccessListener { documents ->
                val products = documents.toObjects(Product::class.java)
                productAdapter.submitList(products)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
