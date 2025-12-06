package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.databinding.ItemBranchBinding
import com.quickbite.models.Branch

class BranchAdapter(
    private val onBranchClick: (Branch) -> Unit
) : ListAdapter<Branch, BranchAdapter.BranchViewHolder>(BranchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        val binding = ItemBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BranchViewHolder(binding, onBranchClick)
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BranchViewHolder(
        private val binding: ItemBranchBinding,
        private val onBranchClick: (Branch) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(branch: Branch) {
            binding.apply {
                tvBranchName.text = branch.name
                tvBranchType.text = branch.type
                tvDistance.text = "${branch.distance} km away"
                tvOperatingHours.text = "Open: ${branch.operatingHours}"

                tvCoffeeMachine.text = when (branch.coffeeMachineStatus) {
                    "available" -> "☕ Available"
                    "busy" -> "☕ Busy"
                    else -> "☕ Unavailable"
                }
                tvCoffeeMachine.setTextColor(
                    when (branch.coffeeMachineStatus) {
                        "available" -> android.graphics.Color.parseColor("#4CAF50")
                        "busy" -> android.graphics.Color.parseColor("#FF9800")
                        else -> android.graphics.Color.parseColor("#F44336")
                    }
                )

                tvDonutAvailability.text = when (branch.donutAvailability) {
                    "high" -> "🍩 In Stock"
                    "low" -> "🍩 Low Stock"
                    else -> "🍩 Out of Stock"
                }
                tvDonutAvailability.setTextColor(
                    when (branch.donutAvailability) {
                        "high" -> android.graphics.Color.parseColor("#4CAF50")
                        "low" -> android.graphics.Color.parseColor("#FF9800")
                        else -> android.graphics.Color.parseColor("#F44336")
                    }
                )

                root.setOnClickListener { onBranchClick(branch) }
            }
        }
    }

    class BranchDiffCallback : DiffUtil.ItemCallback<Branch>() {
        override fun areItemsTheSame(oldItem: Branch, newItem: Branch) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Branch, newItem: Branch) = oldItem == newItem
    }
}