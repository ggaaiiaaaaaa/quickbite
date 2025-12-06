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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper()
        productId = intent.getIntExtra("product_id", -1)

        setupToolbar()
        loadProductDetails()

        binding.btnCustomize.setOnClickListener {
            val intent = Intent(this, CustomizeItemActivity::class.java)
            intent.putExtra("product_id", productId)
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
        lifecycleScope.launch {
            try {
                val response = dbHelper.fetchProductDetails(productId)
                if (response.success) {
                    val product = response.product

                    binding.tvProductName.text = product.name
                    binding.tvProductPrice.text = "₱${product.price}"
                    binding.tvProductDescription.text = product.description
                    binding.tvCalories.text = "Calories: ${product.calories} kcal"
                    binding.tvFat.text = "Fat: ${product.fat}g"
                    binding.tvCarbs.text = "Carbohydrates: ${product.carbs}g"
                    binding.tvProtein.text = "Protein: ${product.protein}g"
                    binding.tvAllergenInfo.text = "Contains: ${product.allergens}"

                    binding.chipFreshTag.visibility = if (product.isFreshToday) View.VISIBLE else View.GONE

                    Glide.with(this@ProductDetailActivity)
                        .load(product.imageUrl)
                        .into(binding.ivProductImage)
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProductDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}