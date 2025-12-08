package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.quickbite.database.AppDatabase
import com.quickbite.databinding.ActivityPaymentBinding
import com.quickbite.models.OrderItemRequest
import com.quickbite.models.OrderRequest
import com.quickbite.repository.CartRepository
import com.quickbite.utils.DatabaseHelper
import com.quickbite.utils.PreferenceHelper
import com.quickbite.utils.PromoManager
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefHelper: PreferenceHelper
    private lateinit var cartRepository: CartRepository
    private lateinit var promoManager: PromoManager

    private var paymentMethod = "Cash"
    private val currentUserId: String get() = prefHelper.getFirebaseUid() ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)
        dbHelper = DatabaseHelper()
        dbHelper.initialize(database)
        prefHelper = PreferenceHelper(this)
        cartRepository = CartRepository(database)
        promoManager = PromoManager(this)

        setupToolbar()
        displayOrderSummary()
        setupPaymentSelection()
        setupPayButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun displayOrderSummary() {
        lifecycleScope.launch {
            val subtotal = intent.getDoubleExtra("subtotal", 0.0).takeIf { it > 0.0 }
                ?: cartRepository.getCartSummary(currentUserId).subtotal

            val discount = promoManager.calculateDiscount(currentUserId, subtotal)

            val total = subtotal - discount

            binding.tvSubtotal.text = "₱%.2f".format(subtotal)
            binding.tvTotal.text = "₱%.2f".format(total)

            if (discount > 0) {
                binding.llDiscount.visibility = View.VISIBLE
                binding.tvDiscount.text = "-₱%.2f".format(discount)
            } else {
                binding.llDiscount.visibility = View.GONE
            }
        }
    }

    private fun setupPaymentSelection() {
        binding.rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            paymentMethod = when (checkedId) {
                binding.rbCash.id -> "Cash"
                binding.rbGcash.id -> "GCash"
                binding.rbMaya.id -> "Maya"
                binding.rbCard.id -> "Card"
                else -> "Cash"
            }
        }
    }

    private fun setupPayButton() {
        binding.btnPayNow.setOnClickListener {
            processPayment()
        }
    }

    private fun processPayment() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnPayNow.isEnabled = false

        lifecycleScope.launch {
            try {
                val orderType = intent.getStringExtra("order_type") ?: "Dine-In"
                val tableNumber = intent.getStringExtra("table_number") ?: ""
                val pickupTime = intent.getStringExtra("pickup_time") ?: "ASAP"
                val promoCode = intent.getStringExtra("promo_code") ?: ""
                val specialInstructions = intent.getStringExtra("special_instructions") ?: ""

                // Get cart summary
                val cartSummary = cartRepository.getCartSummary(currentUserId)

                if (cartSummary.items.isEmpty()) {
                    throw Exception("Cart is empty")
                }

                // Convert cart items to order items
                val orderItems = cartSummary.items.map { cartItem ->
                    OrderItemRequest(
                        productId = cartItem.productId.toIntOrNull() ?: 0,
                        quantity = cartItem.quantity,
                        customizations = cartItem.customizations, // Already JSON string
                        price = cartItem.totalPrice
                    )
                }

                val subtotal = cartSummary.subtotal
                val discount = promoManager.calculateDiscount(currentUserId, subtotal)
                val total = subtotal - discount

                // Get user ID
                val userId = prefHelper.getUserId().takeIf { it != -1 }
                    ?: FirebaseAuth.getInstance().currentUser?.uid?.hashCode()
                    ?: -1

                val orderRequest = OrderRequest(
                    userId = userId,
                    branchId = prefHelper.getSelectedBranchId(),
                    orderType = orderType,
                    tableNumber = tableNumber,
                    pickupTime = pickupTime,
                    items = orderItems,
                    paymentMethod = paymentMethod,
                    subtotal = subtotal,
                    discount = discount,
                    total = total,
                    promoCode = promoCode,
                    specialInstructions = specialInstructions
                )

                val response = dbHelper.submitOrder(orderRequest)

                binding.progressBar.visibility = View.GONE

                if (response.success) {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Order placed successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Clear cart and promo
                    cartRepository.clearCart(currentUserId)
                    promoManager.clearPromo()

                    // Navigate to order tracking
                    val intent = Intent(this@PaymentActivity, OrderTrackingActivity::class.java)
                    intent.putExtra("order_id", response.orderId)
                    intent.putExtra("order_number", response.orderNumber)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    binding.btnPayNow.isEnabled = true
                    Toast.makeText(
                        this@PaymentActivity,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnPayNow.isEnabled = true
                Toast.makeText(
                    this@PaymentActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}