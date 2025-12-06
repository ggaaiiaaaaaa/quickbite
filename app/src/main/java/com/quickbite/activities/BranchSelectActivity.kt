package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.adapters.BranchAdapter
import com.quickbite.databinding.ActivityBranchSelectBinding // Corrected import
import com.quickbite.models.Branch
import com.quickbite.utils.DatabaseHelper
import com.quickbite.utils.PreferenceHelper
import kotlinx.coroutines.launch

class BranchSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBranchSelectBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefHelper: PreferenceHelper
    private lateinit var branchAdapter: BranchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBranchSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper()
        prefHelper = PreferenceHelper(this)

        setupToolbar()
        setupRecyclerView()
        loadBranches()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        // If the user is a guest, they shouldn't go back to the Auth screen
        if (!prefHelper.isGuestMode()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        branchAdapter = BranchAdapter { branch ->
            onBranchSelected(branch)
        }
        binding.rvBranches.apply {
            layoutManager = LinearLayoutManager(this@BranchSelectActivity)
            adapter = branchAdapter
        }
    }

    private fun loadBranches() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = dbHelper.fetchBranches()
                binding.progressBar.visibility = View.GONE

                if (response.success) {
                    branchAdapter.submitList(response.branches)
                } else {
                    Toast.makeText(this@BranchSelectActivity, response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@BranchSelectActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onBranchSelected(branch: Branch) {
        // Use the dedicated helper method to save the branch ID
        prefHelper.saveSelectedBranch(branch.id)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        // This will take the user back to the previous activity.
        // If coming from login, it might be better to override onBackPressed()
        // for more specific navigation logic.
        finish()
        return true
    }
}
