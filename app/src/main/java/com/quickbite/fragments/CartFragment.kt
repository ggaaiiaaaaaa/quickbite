package com.quickbite.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.quickbite.activities.OrderTypeActivity
import com.quickbite.adapters.CartAdapter
import com.quickbite.database.AppDatabase
import com.quickbite.databinding.FragmentCartBinding
import com.quickbite.repository.CartRepository
import com.quickbite.utils.PromoManager
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartRepository: CartRepository
    private lateinit var promoManager: PromoManager
    private lateinit var cartAdapter: CartAdapter
    private val currentUserId: String get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var currentDiscount = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartRepository = CartRepository(AppDatabase.getDatabase(requireContext()))
        promoManager = PromoManager(requireContext())

        setupRecyclerView()
        setupButtons()
        setupPromoCode()
        observeCart()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { position, newQuantity ->
                updateQuantity(position, newQuantity)
            },
            onRemoveClicked = { position ->
                removeItem(position)
            },
            onEditClicked = { position ->
                // Navigate to customize screen
                Toast.makeText(requireContext(), "Edit functionality", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupButtons() {
        binding.btnProceedToCheckout.setOnClickListener {
            proceedToCheckout()
        }

        binding.btnBrowseMenu.setOnClickListener {
            // Navigate to home fragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, com.quickbite.fragments.HomeFragment())
                .commit()
        }
    }

    private fun setupPromoCode() {
        binding.btnApplyPromo.setOnClickListener {
            applyPromoCode()
        }

        // Load active promo if exists
        val activePromo = promoManager.getActivePromo(currentUserId)
        if (activePromo != null) {
            binding.etPromoCode.setText(activePromo.code)
            currentDiscount = activePromo.discount
            updateDiscountUI()
        }
    }

    private fun applyPromoCode() {
        val code = binding.etPromoCode.text.toString().trim()
        if (code.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a promo code", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            var subtotal = 0.0
            cartRepository.getSubtotal(currentUserId).collect {
                subtotal = it
            }

            when (val result = promoManager.applyPromo(currentUserId, code, subtotal)) {
                is com.quickbite.utils.PromoResult.Success -> {
                    currentDiscount = result.discount
                    updateDiscountUI()
                    Toast.makeText(
                        requireContext(),
                        "Promo applied! You saved ₱%.2f".format(result.discount),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is com.quickbite.utils.PromoResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeCart() {
        lifecycleScope.launch {
            cartRepository.getCartItems(currentUserId).collect { items ->
                if (items.isEmpty()) {
                    showEmptyCart()
                } else {
                    showCartItems(items)
                }
            }
        }

        // Observe item count
        lifecycleScope.launch {
            cartRepository.getTotalItemCount(currentUserId).collect { count ->
                binding.tvItemCount.text = "$count items"
            }
        }
    }

    private fun showEmptyCart() {
        binding.rvCartItems.visibility = View.GONE
        binding.cardPromo.visibility = View.GONE
        binding.cardSummary.visibility = View.GONE
        binding.btnProceedToCheckout.visibility = View.GONE
        binding.llEmptyCart.visibility = View.VISIBLE
    }

    private fun showCartItems(items: List<com.quickbite.database.entities.CartItemEntity>) {
        binding.rvCartItems.visibility = View.VISIBLE
        binding.cardPromo.visibility = View.VISIBLE
        binding.cardSummary.visibility = View.VISIBLE
        binding.btnProceedToCheckout.visibility = View.VISIBLE
        binding.llEmptyCart.visibility = View.GONE

        // Convert entities to cart items for adapter
        val cartItems = items.map { entity ->
            cartRepository.mapToCartItem(entity)
        }

        cartAdapter.submitList(cartItems)

        // Update prices
        val subtotal = items.sumOf { it.totalPrice }

        // Recalculate discount based on current subtotal
        if (promoManager.hasActivePromo(currentUserId)) {
            currentDiscount = promoManager.calculateDiscount(currentUserId, subtotal)
        }

        binding.tvSubtotal.text = "₱%.2f".format(subtotal)
        updateDiscountUI()

        val total = subtotal - currentDiscount
        binding.tvTotal.text = "₱%.2f".format(total)
    }

    private fun updateDiscountUI() {
        if (currentDiscount > 0) {
            binding.llDiscount.visibility = View.VISIBLE
            binding.tvDiscount.text = "-₱%.2f".format(currentDiscount)
        } else {
            binding.llDiscount.visibility = View.GONE
        }
    }

    private fun updateQuantity(position: Int, newQuantity: Int) {
        lifecycleScope.launch {
            cartRepository.updateQuantity(currentUserId, position, newQuantity)
        }
    }

    private fun removeItem(position: Int) {
        lifecycleScope.launch {
            cartRepository.removeItemByPosition(currentUserId, position)
            Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun proceedToCheckout() {
        lifecycleScope.launch {
            val summary = cartRepository.getCartSummary(currentUserId)

            if (summary.items.isEmpty()) {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val intent = Intent(requireContext(), OrderTypeActivity::class.java).apply {
                putExtra("subtotal", summary.subtotal)
                putExtra("discount", currentDiscount)
                putExtra("total", summary.subtotal - currentDiscount)
                putExtra("promo_code", promoManager.getActivePromo(currentUserId)?.code ?: "")
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}