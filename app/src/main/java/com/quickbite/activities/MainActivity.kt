package com.quickbite.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.R
import com.quickbite.database.AppDatabase
import com.quickbite.databinding.ActivityMainBinding
import com.quickbite.fragments.CartFragment
import com.quickbite.fragments.HomeFragment
import com.quickbite.fragments.OrderHistoryFragment
import com.quickbite.fragments.ProfileFragment
import com.quickbite.repository.FirebaseProductRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productRepository: FirebaseProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Initialize repository
        productRepository = FirebaseProductRepository(
            FirebaseFirestore.getInstance(),
            AppDatabase.getDatabase(this)
        )

        // Sync products from Firebase to SQLite
        syncData()

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun syncData() {
        lifecycleScope.launch {
            productRepository.syncProductsFromFirebase()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_orders -> {
                    loadFragment(OrderHistoryFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}