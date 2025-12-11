package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.quickbite.databinding.ActivityProductDetailBinding
import com.quickbite.utils.DatabaseHelper
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var dbHelper: DatabaseHelper
    private var productId: Int = -1
    private var productIdString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper()

        // Handle both Int and String product IDs
        productId = intent.getIntExtra("product_id", -1)
        if (productId == -1) {
            productIdString = intent.getStringExtra("product_id") ?: ""
            productId = productIdString.toIntOrNull() ?: -1
        } else {
            productIdString = productId.toString()
        }

        setupToolbar()
        loadProductDetails()

        binding.btnCustomize.setOnClickListener {
            val intent = Intent(this, CustomizeItemActivity::class.java).apply {
                putExtra("product_id", productId)
                putExtra("product_name", binding.tvProductName.text.toString())
                putExtra("product_image_url", productIdString)
                putExtra("product_price", binding.tvProductPrice.text.toString()
                    .replace("₱", "")
                    .replace(",", "")
                    .toDoubleOrNull() ?: 0.0)
            }
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadProductDetails() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = dbHelper.fetchProductDetails(productId)
                binding.progressBar.visibility = View.GONE

                if (response.success) {
                    val product = response.product

                    binding.tvProductName.text = product.name
                    binding.tvProductPrice.text = "₱%.2f".format(product.price)
                    binding.tvProductDescription.text = product.description
                    binding.tvCalories.text = "Calories: ${product.calories} kcal"
                    binding.tvFat.text = "Fat: ${product.fat}g"
                    binding.tvCarbs.text = "Carbohydrates: ${product.carbs}g"
                    binding.tvProtein.text = "Protein: ${product.protein}g"
                    binding.tvAllergenInfo.text = "Contains: ${product.allergens}"

                    binding.chipFreshTag.visibility =
                        if (product.isFreshToday) View.VISIBLE else View.GONE

                    Glide.with(this@ProductDetailActivity)
                        .load(product.imageUrl)
                        .into(binding.ivProductImage)
                } else {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Product not found",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}