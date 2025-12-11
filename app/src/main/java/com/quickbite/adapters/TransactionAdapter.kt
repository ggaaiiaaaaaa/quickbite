package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.R
import com.quickbite.models.Transaction

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val onViewDetailsClick: ((Transaction) -> Unit)? = null
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
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
            tvTransactionId.text = transaction.transactionId
            tvTransactionDate.text = transaction.getFormattedDate()
            tvCustomerName.text = transaction.customerName
            tvTotalAmount.text = String.format("₱%.2f", transaction.totalAmount)
            tvStatus.text = transaction.status

            // Color code status
            val statusColor = when (transaction.status.lowercase()) {
                "completed" -> android.graphics.Color.parseColor("#4CAF50")
                "pending" -> android.graphics.Color.parseColor("#FF9800")
                "cancelled" -> android.graphics.Color.parseColor("#F44336")
                else -> android.graphics.Color.parseColor("#757575")
            }
            tvStatus.setTextColor(statusColor)

            btnViewDetails.setOnClickListener {
                onViewDetailsClick?.invoke(transaction)
            }
        }
    }
}