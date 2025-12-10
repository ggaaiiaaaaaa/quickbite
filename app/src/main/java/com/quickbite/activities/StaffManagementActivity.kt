package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.R
import com.quickbite.adapters.StaffManagementAdapter
import com.quickbite.databinding.ActivityStaffManagementBinding
import com.quickbite.models.User

class StaffManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffManagementBinding
    private lateinit var adapter: StaffManagementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Replace with actual data from your database
        val staff = listOf(
            User(name = "Admin User", email = "admin@example.com", role = "Manager"),
            User(name = "Kitchen Staff", email = "kitchen@example.com", role = "Kitchen Staff")
        )

        adapter = StaffManagementAdapter(staff)
        binding.rvStaffAccounts.adapter = adapter
        binding.rvStaffAccounts.layoutManager = LinearLayoutManager(this)

        binding.fabAddStaff.setOnClickListener {
            startActivity(Intent(this, AddEditStaffActivity::class.java))
        }
    }
}
