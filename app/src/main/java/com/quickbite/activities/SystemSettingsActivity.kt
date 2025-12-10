package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quickbite.databinding.ActivitySystemSettingsBinding

class SystemSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySystemSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySystemSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPaymentGateway.setOnClickListener {
            // TODO: Implement Payment Gateway Integration
        }

        binding.btnPromoCodes.setOnClickListener {
            // TODO: Implement Promo Code Management
        }

        binding.btnBranchSettings.setOnClickListener {
            // TODO: Implement Branch Settings
        }

        binding.btnOperatingHours.setOnClickListener {
            // TODO: Implement Operating Hours Configuration
        }

        binding.btnAppAnnouncements.setOnClickListener {
            // TODO: Implement App Announcements/Banners
        }
    }
}
