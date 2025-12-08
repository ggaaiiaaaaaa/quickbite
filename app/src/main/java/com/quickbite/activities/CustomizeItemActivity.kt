package com.quickbite.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.quickbite.database.AppDatabase
import com.quickbite.databinding.ActivityCustomizeItemBinding
import com.quickbite.models.CartItem
import com.quickbite.models.CustomizationOptions
import com.quickbite.repository.CartRepository
import kotlinx.coroutines.launch

class CustomizeItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomizeItemBinding
    private lateinit var cartRepository: CartRepository

    private var basePrice = 45.0
    private var currentPrice = 45.0
    private var quantity = 1
    private var productId = -1
    private var productName = ""
    private var productImageUrl = ""
    private var productType = "" // "donut", "coffee", "beverage"

    private val currentUserId: String get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRepository = CartRepository(AppDatabase.getDatabase(this))

        productId = intent.getIntExtra("product_id", -1)
        productName = intent.getStringExtra("product_name") ?: "Product"
        productImageUrl = intent.getStringExtra("product_image_url") ?: ""
        basePrice = intent.getDoubleExtra("product_price", 45.0)
        productType = intent.getStringExtra("product_type") ?: "donut"

        currentPrice = basePrice

        setupToolbar()
        setupProductInfo()
        setupCustomizationOptions()
        setupQuantityControls()
        setupAddToCart()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupProductInfo() {
        binding.tvProductName.text = productName
        binding.tvBasePrice.text = "Base: ₱%.2f".format(basePrice)
        updateTotalPrice()
    }

    private fun setupCustomizationOptions() {
        // Determine product type and show relevant options
        when (productType.lowercase()) {
            "coffee" -> showCoffeeOptions()
            "donut" -> showDonutOptions()
            "beverage" -> showBeverageOptions()
            else -> showDonutOptions()
        }

        // Setup filling spinner for donuts
        val fillings = listOf("No Filling", "Chocolate Cream", "Vanilla Cream", "Strawberry Jam", "Custard")
        binding.spinnerFilling.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fillings)

        // Setup glaze spinner for donuts
        val glazes = listOf("Chocolate", "Vanilla", "Strawberry", "Caramel", "Plain")
        binding.spinnerGlaze.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, glazes)

        // Setup listeners for price updates
        setupPriceListeners()
    }

    private fun showCoffeeOptions() {
        binding.tvSizeLabel.visibility = android.view.View.VISIBLE
        binding.rgSize.visibility = android.view.View.VISIBLE
        binding.tvMilkLabel.visibility = android.view.View.VISIBLE
        binding.rgMilkType.visibility = android.view.View.VISIBLE
        binding.tvSweetnessLabel.visibility = android.view.View.VISIBLE
        binding.sliderSweetness.visibility = android.view.View.VISIBLE
        binding.tvAddonsLabel.visibility = android.view.View.VISIBLE
        binding.cbExtraShot.visibility = android.view.View.VISIBLE
        binding.cbWhippedCream.visibility = android.view.View.VISIBLE
        binding.cbCaramelDrizzle.visibility = android.view.View.VISIBLE

        // Hide donut options
        binding.tvFillingLabel.visibility = android.view.View.GONE
        binding.spinnerFilling.visibility = android.view.View.GONE
        binding.tvGlazeLabel.visibility = android.view.View.GONE
        binding.spinnerGlaze.visibility = android.view.View.GONE
    }

    private fun showDonutOptions() {
        binding.tvFillingLabel.visibility = android.view.View.VISIBLE
        binding.spinnerFilling.visibility = android.view.View.VISIBLE
        binding.tvGlazeLabel.visibility = android.view.View.VISIBLE
        binding.spinnerGlaze.visibility = android.view.View.VISIBLE

        // Hide coffee options
        binding.tvSizeLabel.visibility = android.view.View.GONE
        binding.rgSize.visibility = android.view.View.GONE
        binding.tvMilkLabel.visibility = android.view.View.GONE
        binding.rgMilkType.visibility = android.view.View.GONE
        binding.tvSweetnessLabel.visibility = android.view.View.GONE
        binding.sliderSweetness.visibility = android.view.View.GONE
        binding.tvAddonsLabel.visibility = android.view.View.GONE
        binding.cbExtraShot.visibility = android.view.View.GONE
        binding.cbWhippedCream.visibility = android.view.View.GONE
        binding.cbCaramelDrizzle.visibility = android.view.View.GONE
    }

    private fun showBeverageOptions() {
        binding.tvSizeLabel.visibility = android.view.View.VISIBLE
        binding.rgSize.visibility = android.view.View.VISIBLE
        binding.tvSweetnessLabel.visibility = android.view.View.VISIBLE
        binding.sliderSweetness.visibility = android.view.View.VISIBLE

        // Hide other options
        binding.tvMilkLabel.visibility = android.view.View.GONE
        binding.rgMilkType.visibility = android.view.View.GONE
        binding.tvAddonsLabel.visibility = android.view.View.GONE
        binding.cbExtraShot.visibility = android.view.View.GONE
        binding.cbWhippedCream.visibility = android.view.View.GONE
        binding.cbCaramelDrizzle.visibility = android.view.View.GONE
        binding.tvFillingLabel.visibility = android.view.View.GONE
        binding.spinnerFilling.visibility = android.view.View.GONE
        binding.tvGlazeLabel.visibility = android.view.View.GONE
        binding.spinnerGlaze.visibility = android.view.View.GONE
    }

    private fun setupPriceListeners() {
        // Size selection
        binding.rgSize.setOnCheckedChangeListener { _, _ ->
            updatePrice()
        }

        // Milk type selection
        binding.rgMilkType.setOnCheckedChangeListener { _, _ ->
            updatePrice()
        }

        // Add-ons
        binding.cbExtraShot.setOnCheckedChangeListener { _, _ -> updatePrice() }
        binding.cbWhippedCream.setOnCheckedChangeListener { _, _ -> updatePrice() }
        binding.cbCaramelDrizzle.setOnCheckedChangeListener { _, _ -> updatePrice() }
    }

    private fun setupQuantityControls() {
        binding.btnIncrement.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
            updateTotalPrice()
        }

        binding.btnDecrement.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
                updateTotalPrice()
            }
        }
    }

    private fun updatePrice() {
        currentPrice = basePrice

        // Add size price
        when (binding.rgSize.checkedRadioButtonId) {
            binding.rbMedium.id -> currentPrice += 15
            binding.rbLarge.id -> currentPrice += 30
        }

        // Add milk type price
        when (binding.rgMilkType.checkedRadioButtonId) {
            binding.rbAlmondMilk.id -> currentPrice += 20
            binding.rbOatMilk.id -> currentPrice += 25
        }

        // Add-ons
        if (binding.cbExtraShot.isChecked) currentPrice += 25
        if (binding.cbWhippedCream.isChecked) currentPrice += 15
        if (binding.cbCaramelDrizzle.isChecked) currentPrice += 10

        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val total = currentPrice * quantity
        binding.tvTotalPrice.text = "₱%.2f".format(total)
    }

    private fun setupAddToCart() {
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun addToCart() {
        val customizations = CustomizationOptions(
            size = getSelectedSize(),
            milkType = getSelectedMilk(),
            sweetness = binding.sliderSweetness.value.toInt(),
            extraShot = binding.cbExtraShot.isChecked,
            whippedCream = binding.cbWhippedCream.isChecked,
            caramelDrizzle = binding.cbCaramelDrizzle.isChecked,
            filling = binding.spinnerFilling.selectedItem?.toString() ?: "",
            glaze = binding.spinnerGlaze.selectedItem?.toString() ?: "",
            specialInstructions = binding.etSpecialInstructions.text.toString()
        )

        val cartItem = CartItem(
            productId = productId,
            productName = productName,
            basePrice = basePrice,
            quantity = quantity,
            customizations = customizations,
            totalPrice = currentPrice * quantity,
            imageUrl = productImageUrl
        )

        lifecycleScope.launch {
            try {
                cartRepository.addToCart(currentUserId, cartItem)
                Toast.makeText(this@CustomizeItemActivity, "Added to cart!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@CustomizeItemActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSelectedSize(): String {
        return when (binding.rgSize.checkedRadioButtonId) {
            binding.rbSmall.id -> "Small"
            binding.rbMedium.id -> "Medium"
            binding.rbLarge.id -> "Large"
            else -> "Small"
        }
    }

    private fun getSelectedMilk(): String {
        return when (binding.rgMilkType.checkedRadioButtonId) {
            binding.rbWholeMilk.id -> "Whole Milk"
            binding.rbAlmondMilk.id -> "Almond Milk"
            binding.rbOatMilk.id -> "Oat Milk"
            else -> "Whole Milk"
        }
    }
}