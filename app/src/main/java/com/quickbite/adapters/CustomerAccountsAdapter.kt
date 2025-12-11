package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.R
import com.quickbite.models.User

class CustomerAccountsAdapter(
    private val onOrderHistoryClick: (User) -> Unit
) : ListAdapter<User, CustomerAccountsAdapter.CustomerViewHolder>(CustomerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_account, parent, false)
        return CustomerViewHolder(view, onOrderHistoryClick)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = getItem(position)
        holder.bind(customer)
    }

    class CustomerViewHolder(itemView: View, private val onOrderHistoryClick: (User) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvCustomerName)
        private val emailTextView: TextView = itemView.findViewById(R.id.tvCustomerEmail)
        private val viewOrderHistoryButton: Button = itemView.findViewById(R.id.btnViewOrderHistory)

        fun bind(customer: User) {
            nameTextView.text = customer.name
            emailTextView.text = customer.email

            viewOrderHistoryButton.setOnClickListener {
                onOrderHistoryClick(customer)
            }
        }
    }
}

class CustomerDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
