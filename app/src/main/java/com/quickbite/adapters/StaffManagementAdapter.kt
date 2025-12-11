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

class StaffManagementAdapter(
    private val onEditClick: (User) -> Unit
) : ListAdapter<User, StaffManagementAdapter.StaffViewHolder>(StaffDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff_account, parent, false)
        return StaffViewHolder(view, onEditClick)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staffMember = getItem(position)
        holder.bind(staffMember)
    }

    class StaffViewHolder(itemView: View, private val onEditClick: (User) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvStaffName)
        private val roleTextView: TextView = itemView.findViewById(R.id.tvStaffRole)
        private val editButton: Button = itemView.findViewById(R.id.btnEditStaff)

        fun bind(staffMember: User) {
            nameTextView.text = staffMember.name
            roleTextView.text = "Role: ${staffMember.role}"

            editButton.setOnClickListener {
                onEditClick(staffMember)
            }
        }
    }
}

class StaffDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
