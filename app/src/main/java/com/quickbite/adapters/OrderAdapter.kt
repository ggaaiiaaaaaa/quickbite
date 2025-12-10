package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.R
import com.quickbite.models.Order

class OrderAdapter(
    private val orders: List<Order>,
    private val onOrderClickListener: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        private val tvOrderType: TextView = itemView.findViewById(R.id.tvOrderType)
        private val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        private val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)

        fun bind(order: Order) {
            tvOrderId.text = order.orderId
            tvCustomerName.text = order.customerName
            tvOrderType.text = order.orderType
            tvOrderStatus.text = order.orderStatus

            btnViewDetails.setOnClickListener {
                onOrderClickListener(order)
            }
        }
    }
}
