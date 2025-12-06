package com.quickbite.fragments
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.activities.ProductDetailActivity
import com.quickbite.adapters.CategoryAdapter
import com.quickbite.adapters.MenuProductAdapter
import com.quickbite.adapters.PromoAdapter
import com.quickbite.database.AppDatabase
import com.quickbite.databinding.FragmentHomeBinding
import com.quickbite.repository.FirebaseProductRepository
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var productRepository: FirebaseProductRepository
    private lateinit var menuAdapter: MenuProductAdapter
    private lateinit var seasonalAdapter: MenuProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter

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

        productRepository = FirebaseProductRepository(
            FirebaseFirestore.getInstance(),
            AppDatabase.getDatabase(requireContext())
        )

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

        // Promos
        binding.vpFeaturedPromos.adapter = PromoAdapter()
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
                    loadAllProducts()
                }
            }
        })
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE

        // Load categories
        val categories = listOf("All", "Donuts", "Coffee", "Beverages", "Breakfast", "Snacks")
        categoryAdapter.submitList(categories)

        // Load seasonal items from SQLite (synced from Firebase)
        lifecycleScope.launch {
            productRepository.getSeasonalProducts().collect { products ->
                seasonalAdapter.submitList(products.map { it.toProduct() })
            }
        }

        // Load all products
        loadAllProducts()

        binding.progressBar.visibility = View.GONE
    }

    private fun loadAllProducts() {
        lifecycleScope.launch {
            productRepository.getLocalProducts().collect { products ->
                menuAdapter.submitList(products.map { it.toProduct() })
            }
        }
    }

    private fun filterByCategory(category: String) {
        if (category == "All") {
            loadAllProducts()
        } else {
            lifecycleScope.launch {
                productRepository.getProductsByCategory(category).collect { products ->
                    menuAdapter.submitList(products.map { it.toProduct() })
                }
            }
        }
    }

    private fun searchProducts(query: String) {
        lifecycleScope.launch {
            productRepository.searchProducts(query).collect { products ->
                menuAdapter.submitList(products.map { it.toProduct() })
            }
        }
    }

    private fun openProductDetails(productId: String) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("product_id", productId)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Extension function to convert Entity to Model
private fun com.quickbite.database.entities.ProductEntity.toProduct() =
    com.quickbite.models.Product(
        id = this.id.toIntOrNull() ?: 0,
        name = this.name,
        description = this.description,
        price = this.price,
        category = this.category,
        imageUrl = this.imageUrl,
        isSeasonal = this.isSeasonal,
        isFreshToday = this.isFreshToday,
        calories = this.calories,
        fat = this.fat,
        carbs = this.carbs,
        protein = this.protein,
        allergens = this.allergens
    )
