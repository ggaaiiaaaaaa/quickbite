package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quickbite.R
import com.quickbite.databinding.ItemMenuProductBinding
import com.quickbite.models.Product

class MenuProductAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, MenuProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemMenuProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onProductClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemMenuProductBinding,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                tvProductName.text = product.name
                tvProductDescription.text = product.description
                tvProductPrice.text = "₱%.2f".format(product.price)

                tvFreshTag.visibility = if (product.isFreshToday) View.VISIBLE else View.GONE

                Glide.with(root.context)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.placeholder_donut)
                    .into(ivProductImage)

                root.setOnClickListener { onProductClick(product) }
                ivAddToCart.setOnClickListener { onProductClick(product) }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}