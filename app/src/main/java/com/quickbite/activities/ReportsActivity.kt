package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quickbite.databinding.ActivityReportsBinding

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Reports & Analytics"

        binding.btnSalesReports.setOnClickListener {
            startActivity(Intent(this, SalesReportsActivity::class.java))
        }

        binding.btnPerformanceAnalytics.setOnClickListener {
            startActivity(Intent(this, PerformanceAnalyticsActivity::class.java))
        }
    }
}