package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.databinding.ItemOrderHistoryBinding
import com.quickbite.models.OrderHistoryItem

class OrderHistoryAdapter(
    private val onReorderClick: (OrderHistoryItem) -> Unit
) : ListAdapter<OrderHistoryItem, OrderHistoryAdapter.OrderHistoryViewHolder>(OrderHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderHistoryViewHolder(binding, onReorderClick)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderHistoryViewHolder(
        private val binding: ItemOrderHistoryBinding,
        private val onReorderClick: (OrderHistoryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderHistoryItem) {
            binding.apply {
                tvOrderNumber.text = order.orderNumber
                tvOrderDate.text = order.date
                tvOrderTotal.text = "₱%.2f".format(order.total)
                tvOrderStatus.text = order.status

                val itemCount = order.items.sumOf { it.quantity }
                tvItemCount.text = "$itemCount item${if (itemCount > 1) "s" else ""}"

                btnReorder.setOnClickListener {
                    onReorderClick(order)
                }
            }
        }
    }

    class OrderHistoryDiffCallback : DiffUtil.ItemCallback<OrderHistoryItem>() {
        override fun areItemsTheSame(oldItem: OrderHistoryItem, newItem: OrderHistoryItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: OrderHistoryItem, newItem: OrderHistoryItem) = oldItem == newItem
    }
}