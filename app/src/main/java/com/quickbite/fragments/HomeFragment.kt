package com.quickbite.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.activities.ProductDetailActivity
import com.quickbite.adapters.CategoryAdapter
import com.quickbite.adapters.MenuProductAdapter
import com.quickbite.adapters.PromoAdapter
import com.quickbite.databinding.FragmentHomeBinding
import com.quickbite.utils.DatabaseHelper
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var menuAdapter: MenuProductAdapter
    private lateinit var seasonalAdapter: MenuProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var promoAdapter: PromoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper()
        setupRecyclerViews()
        setupSearchBar()
        loadData()
    }

    private fun setupRecyclerViews() {
        // Categories
        categoryAdapter = CategoryAdapter { category ->
            filterByCategory(category)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // Seasonal Items
        seasonalAdapter = MenuProductAdapter { product ->
            openProductDetails(product.id)
        }
        binding.rvSeasonalItems.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = seasonalAdapter
        }

        // All Menu Items
        menuAdapter = MenuProductAdapter { product ->
            openProductDetails(product.id)
        }
        binding.rvMenuItems.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = menuAdapter
        }

        // Featured Promos
        promoAdapter = PromoAdapter()
        binding.vpFeaturedPromos.adapter = promoAdapter
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length >= 3) {
                    searchProducts(query)
                } else if (query.isEmpty()) {
                    loadMenu()
                }
            }
        })

        binding.ivFilter.setOnClickListener {
            // Show filter dialog
            showFilterDialog()
        }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Load categories
                val categories = listOf("All", "Donuts", "Coffee", "Beverages", "Breakfast", "Snacks")
                categoryAdapter.submitList(categories)

                // Load seasonal items
                val seasonalResponse = dbHelper.fetchSeasonalItems()
                if (seasonalResponse.success) {
                    seasonalAdapter.submitList(seasonalResponse.products)
                }

                // Load all menu items
                loadMenu()

                binding.progressBar.visibility = View.GONE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMenu(category: String? = null) {
        lifecycleScope.launch {
            try {
                val response = dbHelper.fetchMenu(category)
                if (response.success) {
                    menuAdapter.submitList(response.products)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchProducts(query: String) {
        lifecycleScope.launch {
            try {
                val response = dbHelper.searchProducts(query)
                if (response.success) {
                    menuAdapter.submitList(response.products)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterByCategory(category: String) {
        if (category == "All") {
            loadMenu()
        } else {
            loadMenu(category)
        }
    }

    private fun showFilterDialog() {
        // Implement filter bottom sheet
        Toast.makeText(requireContext(), "Filter dialog coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun openProductDetails(productId: Int) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("product_id", productId)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}