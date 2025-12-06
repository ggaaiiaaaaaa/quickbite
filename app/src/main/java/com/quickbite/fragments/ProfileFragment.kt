package com.quickbite.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.quickbite.activities.AuthActivity
import com.quickbite.activities.OrderHistoryActivity
import com.quickbite.databinding.FragmentProfileBinding
import com.quickbite.utils.PreferenceHelper

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefHelper: PreferenceHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefHelper = PreferenceHelper(requireContext())

        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        binding.tvUserName.text = prefHelper.getUserName()
        binding.tvUserEmail.text = prefHelper.getSavedEmail()
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            // Navigate to edit profile
        }

        binding.llOrderHistory.setOnClickListener {
            startActivity(Intent(requireContext(), OrderHistoryActivity::class.java))
        }

        binding.llFavorites.setOnClickListener {
            // Navigate to favorites
        }

        binding.llPaymentMethods.setOnClickListener {
            // Navigate to payment methods
        }

        binding.llAbout.setOnClickListener {
            // Show about dialog
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        prefHelper.clearUserData()
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
