package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.R
import com.quickbite.models.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private val transactions: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTransactionId: TextView = itemView.findViewById(R.id.tvTransactionId)
        private val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        private val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)

        fun bind(transaction: Transaction) {
            tvTransactionId.text = transaction.id
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            tvTransactionDate.text = sdf.format(transaction.date)
            tvCustomerName.text = transaction.customerName
            tvTotalAmount.text = String.format("$%.2f", transaction.totalAmount)
            tvStatus.text = transaction.status

            btnViewDetails.setOnClickListener {
                // Handle view details click
            }
        }
    }
}