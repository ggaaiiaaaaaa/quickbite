package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quickbite.R
import com.quickbite.databinding.ActivityUserManagementBinding

class UserManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "User Management"

        binding.btnCustomerAccounts.setOnClickListener {
            startActivity(Intent(this, CustomerAccountsActivity::class.java))
        }

        binding.btnStaffManagement.setOnClickListener {
            startActivity(Intent(this, StaffManagementActivity::class.java))
        }
    }
}
