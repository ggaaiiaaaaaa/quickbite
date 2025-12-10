package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.R
import com.quickbite.models.User

class CustomerAccountsAdapter(private val customers: List<User>) : RecyclerView.Adapter<CustomerAccountsAdapter.CustomerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_account, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customers[position]
        holder.bind(customer)
    }

    override fun getItemCount() = customers.size

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvCustomerName)
        private val emailTextView: TextView = itemView.findViewById(R.id.tvCustomerEmail)
        private val viewOrderHistoryButton: Button = itemView.findViewById(R.id.btnViewOrderHistory)

        fun bind(customer: User) {
            nameTextView.text = customer.name
            emailTextView.text = customer.email

            viewOrderHistoryButton.setOnClickListener {
                // TODO: Handle click to view order history
            }
        }
    }
}
