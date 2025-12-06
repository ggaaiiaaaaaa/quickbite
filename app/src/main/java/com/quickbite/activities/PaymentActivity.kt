package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.quickbite.databinding.ActivityPaymentBinding
import com.quickbite.models.OrderItemRequest
import com.quickbite.models.OrderRequest
import com.quickbite.utils.CartManager
import com.quickbite.utils.DatabaseHelper
import com.quickbite.utils.PreferenceHelper
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefHelper: PreferenceHelper
    private var paymentMethod = "Cash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper()
        prefHelper = PreferenceHelper(this)

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
        val subtotal = CartManager.getSubtotal()
        val discount = CartManager.getDiscount()
        val total = CartManager.getTotal()

        binding.tvSubtotal.text = "₱%.2f".format(subtotal)
        binding.tvTotal.text = "₱%.2f".format(total)

        if (discount > 0) {
            binding.llDiscount.visibility = View.VISIBLE
            binding.tvDiscount.text = "-₱%.2f".format(discount)
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
                val pickupTime = intent.getStringExtra("pickup_time") ?: ""
                val promoCode = intent.getStringExtra("promo_code") ?: ""
                val specialInstructions = intent.getStringExtra("special_instructions") ?: ""

                // Convert cart items to order items
                val orderItems = CartManager.getItems().map { cartItem ->
                    OrderItemRequest(
                        productId = cartItem.productId,
                        quantity = cartItem.quantity,
                        customizations = Gson().toJson(cartItem.customizations),
                        price = cartItem.totalPrice
                    )
                }

                val orderRequest = OrderRequest(
                    userId = prefHelper.getUserId(),
                    branchId = prefHelper.getSelectedBranchId(),
                    orderType = orderType,
                    tableNumber = tableNumber,
                    pickupTime = pickupTime,
                    items = orderItems,
                    paymentMethod = paymentMethod,
                    subtotal = CartManager.getSubtotal(),
                    discount = CartManager.getDiscount(),
                    total = CartManager.getTotal(),
                    promoCode = promoCode,
                    specialInstructions = specialInstructions
                )

                val response = dbHelper.submitOrder(orderRequest)

                binding.progressBar.visibility = View.GONE

                if (response.success) {
                    Toast.makeText(this@PaymentActivity, "Order placed successfully!", Toast.LENGTH_SHORT).show()

                    // Clear cart
                    CartManager.clearCart()

                    // Navigate to order tracking
                    val intent = Intent(this@PaymentActivity, OrderTrackingActivity::class.java)
                    intent.putExtra("order_id", response.orderId)
                    intent.putExtra("order_number", response.orderNumber)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    binding.btnPayNow.isEnabled = true
                    Toast.makeText(this@PaymentActivity, response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnPayNow.isEnabled = true
                Toast.makeText(this@PaymentActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
