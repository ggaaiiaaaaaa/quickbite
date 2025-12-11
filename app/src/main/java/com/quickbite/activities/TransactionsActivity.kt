package com.quickbite.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.quickbite.adapters.TransactionAdapter
import com.quickbite.databinding.ActivityTransactionsBinding
import com.quickbite.models.OrderItem
import com.quickbite.models.Transaction
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class TransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionsBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val transactions = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadTransactions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Transactions"
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(transactions)
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@TransactionsActivity)
            adapter = transactionAdapter
        }
    }

    private fun loadTransactions() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val snapshot = firestore.collection("orders")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()

                transactions.clear()

                for (doc in snapshot.documents) {
                    val transaction = Transaction(
                        transactionId = doc.id,
                        customerName = doc.getString("customerName") ?: "Unknown",
                        totalAmount = doc.getDouble("total") ?: 0.0,
                        status = doc.getString("status") ?: "Pending",
                        date = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        items = emptyList() // Load items separately if needed
                    )
                    transactions.add(transaction)
                }

                transactionAdapter.notifyDataSetChanged()

                if (transactions.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvTransactions.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvTransactions.visibility = View.VISIBLE
                }

                binding.progressBar.visibility = View.GONE

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@TransactionsActivity,
                    "Error loading transactions: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}