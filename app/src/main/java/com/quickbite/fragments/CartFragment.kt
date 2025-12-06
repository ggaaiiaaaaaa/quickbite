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
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartRepository: CartRepository
    private lateinit var cartAdapter: CartAdapter
    private val currentUserId: String get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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

        setupRecyclerView()
        setupButtons()
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
            lifecycleScope.launch {
                cartRepository.getCartItems(currentUserId).collect { items ->
                    if (items.isNotEmpty()) {
                        startActivity(Intent(requireContext(), OrderTypeActivity::class.java))
                    } else {
                        Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                    }
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
            com.quickbite.models.CartItem(
                productId = entity.productId.toIntOrNull() ?: 0,
                productName = entity.productName,
                basePrice = entity.basePrice,
                quantity = entity.quantity,
                customizations = com.google.gson.Gson().fromJson(
                    entity.customizations,
                    com.quickbite.models.CustomizationOptions::class.java
                ),
                totalPrice = entity.totalPrice,
                imageUrl = entity.productImageUrl
            )
        }

        cartAdapter.submitList(cartItems)

        val subtotal = items.sumOf { it.totalPrice }
        binding.tvSubtotal.text = "₱%.2f".format(subtotal)
        binding.tvTotal.text = "₱%.2f".format(subtotal)
        binding.tvItemCount.text = "${items.size} items"
    }

    private fun updateQuantity(position: Int, newQuantity: Int) {
        lifecycleScope.launch {
            cartRepository.getCartItems(currentUserId).collect { items ->
                if (position < items.size) {
                    val item = items[position].copy(
                        quantity = newQuantity,
                        totalPrice = items[position].basePrice * newQuantity
                    )
                    cartRepository.updateCartItem(item)
                }
            }
        }
    }

    private fun removeItem(position: Int) {
        lifecycleScope.launch {
            cartRepository.getCartItems(currentUserId).collect { items ->
                if (position < items.size) {
                    cartRepository.removeFromCart(items[position])
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}