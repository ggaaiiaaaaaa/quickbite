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
import com.quickbite.activities.OrderTypeActivity
import com.quickbite.adapters.CartAdapter
import com.quickbite.databinding.FragmentCartBinding
import com.quickbite.utils.CartManager
import com.quickbite.utils.DatabaseHelper
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter
    private lateinit var dbHelper: DatabaseHelper

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

        dbHelper = DatabaseHelper()
        setupRecyclerView()
        setupButtons()
        updateCartUI()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { position, newQuantity ->
                CartManager.updateQuantity(position, newQuantity)
                updateCartUI()
            },
            onRemoveClicked = { position ->
                CartManager.removeItem(position)
                updateCartUI()
            },
            onEditClicked = { position ->
                // Navigate to customize activity
                Toast.makeText(requireContext(), "Edit functionality coming soon", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupButtons() {
        binding.btnApplyPromo.setOnClickListener {
            val promoCode = binding.etPromoCode.text.toString().trim()
            if (promoCode.isNotEmpty()) {
                applyPromoCode(promoCode)
            }
        }

        binding.btnProceedToCheckout.setOnClickListener {
            if (CartManager.getItemCount() > 0) {
                startActivity(Intent(requireContext(), OrderTypeActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBrowseMenu.setOnClickListener {
            // Navigate to home fragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, com.quickbite.fragments.HomeFragment())
                .commit()
        }
    }

    private fun updateCartUI() {
        val items = CartManager.getItems()
        val itemCount = CartManager.getItemCount()

        if (items.isEmpty()) {
            binding.rvCartItems.visibility = View.GONE
            binding.cardPromo.visibility = View.GONE
            binding.cardSummary.visibility = View.GONE
            binding.btnProceedToCheckout.visibility = View.GONE
            binding.llEmptyCart.visibility = View.VISIBLE
        } else {
            binding.rvCartItems.visibility = View.VISIBLE
            binding.cardPromo.visibility = View.VISIBLE
            binding.cardSummary.visibility = View.VISIBLE
            binding.btnProceedToCheckout.visibility = View.VISIBLE
            binding.llEmptyCart.visibility = View.GONE

            cartAdapter.submitList(items)
            binding.tvItemCount.text = "$itemCount item${if (itemCount > 1) "s" else ""}"

            val subtotal = CartManager.getSubtotal()
            val discount = CartManager.getDiscount()
            val total = CartManager.getTotal()

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

    private fun applyPromoCode(code: String) {
        lifecycleScope.launch {
            try {
                val response = dbHelper.validatePromoCode(code)
                if (response.success) {
                    CartManager.applyPromo(code, response.discountAmount)
                    Toast.makeText(requireContext(), "Promo applied!", Toast.LENGTH_SHORT).show()
                    updateCartUI()
                } else {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}