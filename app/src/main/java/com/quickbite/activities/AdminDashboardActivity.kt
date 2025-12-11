package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.quickbite.R
import com.quickbite.Role

class AdminDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Admin Dashboard"

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        loadAdminInfo()
        handleUserRole()
        loadRealTimeData()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    if (isTaskRoot) {
                        finishAffinity()
                    } else {
                        finish()
                    }
                }
            }
        })
    }

    private fun loadAdminInfo() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val adminNameTextView = headerView.findViewById<TextView>(R.id.tvAdminName)

        // TODO: Replace with your logic to get the admin's name
        adminNameTextView.text = "Admin User"
    }

    private fun handleUserRole() {
        val userRole = intent.getStringExtra("USER_ROLE")
        val tvUserRole = findViewById<TextView>(R.id.tvUserRole)
        val llRoleBasedButtons = findViewById<LinearLayout>(R.id.llRoleBasedButtons)
        val llManagerButtons = findViewById<LinearLayout>(R.id.llManagerButtons)
        val llKitchenStaffButtons = findViewById<LinearLayout>(R.id.llKitchenStaffButtons)
        val llCashierButtons = findViewById<LinearLayout>(R.id.llCashierButtons)

        if (userRole != null) {
            tvUserRole.text = "Role: ${userRole.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }}"
            tvUserRole.visibility = View.VISIBLE
            llRoleBasedButtons.visibility = View.VISIBLE

            when (Role.valueOf(userRole)) {
                Role.MANAGER -> llManagerButtons.visibility = View.VISIBLE
                Role.KITCHEN_STAFF -> llKitchenStaffButtons.visibility = View.VISIBLE
                Role.CASHIER -> llCashierButtons.visibility = View.VISIBLE
            }
        }
    }

    private fun loadRealTimeData() {
        // TODO: Replace with your logic to fetch real-time data from your database
        val tvActiveOrdersCount = findViewById<TextView>(R.id.tvActiveOrdersCount)
        val tvTodaysRevenueAmount = findViewById<TextView>(R.id.tvTodaysRevenueAmount)
        val tvPendingOrdersCount = findViewById<TextView>(R.id.tvPendingOrdersCount)

        tvActiveOrdersCount.text = "5"
        tvTodaysRevenueAmount.text = "$567"
        tvPendingOrdersCount.text = "2"
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                Toast.makeText(this, "Overview is already displayed", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_add_products -> {
                val intent = Intent(this, AddEditProductActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_view_products -> {
                val intent = Intent(this, ViewProductsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_transactions -> {
                val intent = Intent(this, TransactionsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_order_management -> {
                val intent = Intent(this, LiveOrdersActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_menu_management -> {
                val intent = Intent(this, MenuManagementActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_inventory -> {
                val intent = Intent(this, InventoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_reports -> {
                val intent = Intent(this, ReportsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_user_management -> {
                val intent = Intent(this, UserManagementActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SystemSettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_sign_out -> {
                signOut()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut() {
        // TODO: Add your sign-out logic here (e.g., clear session, navigate to login)
        Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AdminAuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
