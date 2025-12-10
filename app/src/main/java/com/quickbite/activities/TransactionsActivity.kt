package com.quickbite.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.R
import com.quickbite.adapters.TransactionAdapter
import com.quickbite.models.Transaction
import java.util.Date

class TransactionsActivity : AppCompatActivity() {

    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        rvTransactions = findViewById(R.id.rvTransactions)
        rvTransactions.layoutManager = LinearLayoutManager(this)

        // TODO: Replace with actual transaction data
        val transactions = listOf(
            Transaction("12345", Date(), "John Doe", 50.00, "Completed"),
            Transaction("67890", Date(), "Jane Smith", 75.50, "Pending")
        )

        transactionAdapter = TransactionAdapter(transactions)
        rvTransactions.adapter = transactionAdapter
    }
}