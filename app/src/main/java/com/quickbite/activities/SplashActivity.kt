package com.quickbite.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.quickbite.databinding.ActivitySplashBinding
import com.quickbite.utils.NetworkHelper

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNetworkAndProceed()
    }

    private fun checkNetworkAndProceed() {
        if (NetworkHelper.isNetworkAvailable(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }, 2000)
        } else {
            Snackbar.make(
                binding.root,
                "No internet connection. Please check your network.",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Retry") {
                checkNetworkAndProceed()
            }.show()
        }
    }
}