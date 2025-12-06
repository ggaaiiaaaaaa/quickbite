package com.quickbite.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickbite.adapters.OrderHistoryAdapter
import com.quickbite.databinding.FragmentOrderHistoryBinding
import com.quickbite.utils.DatabaseHelper
import com.quickbite.utils.PreferenceHelper
import kotlinx.coroutines.launch

class OrderHistoryFragment : Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefHelper: PreferenceHelper
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper()
        prefHelper = PreferenceHelper(requireContext())

        setupRecyclerView()
        loadOrderHistory()
    }

    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter(
            onReorderClick = { order ->
                // Add items to cart and navigate
                Toast.makeText(requireContext(), "Reordering...", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvOrderHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderHistoryAdapter
        }
    }

    private fun loadOrderHistory() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = dbHelper.getOrderHistory(prefHelper.getUserId())
                binding.progressBar.visibility = View.GONE

                if (response.success && response.orders.isNotEmpty()) {
                    binding.rvOrderHistory.visibility = View.VISIBLE
                    binding.llEmptyState.visibility = View.GONE
                    orderHistoryAdapter.submitList(response.orders)
                } else {
                    binding.rvOrderHistory.visibility = View.GONE
                    binding.llEmptyState.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
