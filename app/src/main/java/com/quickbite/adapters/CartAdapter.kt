package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.databinding.ItemCartProductBinding
import com.quickbite.models.CartItem

class CartAdapter(
    private val onQuantityChanged: (Int, Int) -> Unit,
    private val onRemoveClicked: (Int) -> Unit,
    private val onEditClicked: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding, onQuantityChanged, onRemoveClicked, onEditClicked)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class CartViewHolder(
        private val binding: ItemCartProductBinding,
        private val onQuantityChanged: (Int, Int) -> Unit,
        private val onRemoveClicked: (Int) -> Unit,
        private val onEditClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem, position: Int) {
            binding.apply {
                tvProductName.text = item.productName
                tvItemPrice.text = "₱%.2f".format(item.totalPrice)
                tvQuantity.text = item.quantity.toString()

                // Build customizations string
                val customizations = buildList {
                    if (item.customizations.size.isNotEmpty()) add(item.customizations.size)
                    if (item.customizations.milkType.isNotEmpty()) add(item.customizations.milkType)
                    if (item.customizations.extraShot) add("Extra Shot")
                    if (item.customizations.whippedCream) add("Whipped Cream")
                    if (item.customizations.caramelDrizzle) add("Caramel Drizzle")
                    if (item.customizations.filling.isNotEmpty()) add(item.customizations.filling)
                    if (item.customizations.glaze.isNotEmpty()) add(item.customizations.glaze)
                }.joinToString(", ")

                tvCustomizations.text = customizations.ifEmpty { "No customizations" }

                btnIncrement.setOnClickListener {
                    onQuantityChanged(position, item.quantity + 1)
                }

                btnDecrement.setOnClickListener {
                    if (item.quantity > 1) {
                        onQuantityChanged(position, item.quantity - 1)
                    }
                }

                btnRemove.setOnClickListener {
                    onRemoveClicked(position)
                }

                tvEdit.setOnClickListener {
                    onEditClicked(position)
                }
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.productId == newItem.productId && oldItem.customizations == newItem.customizations
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem == newItem
    }
}