package com.quickbite.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quickbite.R
import com.quickbite.databinding.ActivityOrderHistoryBinding
import com.quickbite.fragments.OrderHistoryFragment

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, OrderHistoryFragment())
                .commit()
        }
    }
}
