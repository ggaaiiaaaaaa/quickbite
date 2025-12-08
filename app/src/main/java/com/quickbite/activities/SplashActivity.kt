package com.quickbite.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
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

        // Add animations
        animateLogo()
        animateText()

        checkNetworkAndProceed()
    }

    private fun animateLogo() {
        // Scale animation
        val scaleX = ObjectAnimator.ofFloat(binding.ivLogo, "scaleX", 0.5f, 1.1f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivLogo, "scaleY", 0.5f, 1.1f, 1.0f)

        scaleX.duration = 800
        scaleY.duration = 800
        scaleX.interpolator = AccelerateDecelerateInterpolator()
        scaleY.interpolator = AccelerateDecelerateInterpolator()

        scaleX.start()
        scaleY.start()

        // Rotation animation
        val rotation = ObjectAnimator.ofFloat(binding.ivLogo, "rotation", 0f, 360f)
        rotation.duration = 1500
        rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.start()
    }

    private fun animateText() {
        // Fade in animation for app name
        binding.tvAppName.alpha = 0f
        binding.tvAppName.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()

        // Slide up animation for app name
        binding.tvAppName.translationY = 50f
        binding.tvAppName.animate()
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(300)
            .start()

        // Fade in animation for tagline
        binding.tvTagline.alpha = 0f
        binding.tvTagline.animate()
            .alpha(0.8f)
            .setDuration(1000)
            .setStartDelay(600)
            .start()
    }

    private fun checkNetworkAndProceed() {
        if (NetworkHelper.isNetworkAvailable(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                // Add transition animation
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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