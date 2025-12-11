package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.quickbite.adapters.StaffManagementAdapter
import com.quickbite.databinding.ActivityStaffManagementBinding
import com.quickbite.models.User

class StaffManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffManagementBinding
    private lateinit var adapter: StaffManagementAdapter
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.fabAddStaff.setOnClickListener {
            startActivity(Intent(this, AddEditStaffActivity::class.java))
        }

        loadStaff()
    }

    override fun onResume() {
        super.onResume()
        loadStaff()
    }

    private fun setupRecyclerView() {
        adapter = StaffManagementAdapter { staffMember ->
            val intent = Intent(this, AddEditStaffActivity::class.java)
            intent.putExtra("staff_member", staffMember)
            startActivity(intent)
        }
        binding.rvStaffAccounts.adapter = adapter
        binding.rvStaffAccounts.layoutManager = LinearLayoutManager(this)
    }

    private fun loadStaff() {
        firestore.collection("users").whereNotEqualTo("role", "customer").get()
            .addOnSuccessListener { documents ->
                val staff = documents.toObjects(User::class.java)
                adapter.submitList(staff)
            }
    }
}
