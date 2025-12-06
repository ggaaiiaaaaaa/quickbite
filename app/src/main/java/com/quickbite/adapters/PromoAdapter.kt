package com.quickbite.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quickbite.R
import com.quickbite.databinding.ItemPromoBinding

class PromoAdapter : RecyclerView.Adapter<PromoAdapter.PromoViewHolder>() {

    private val promos = listOf(
        "Buy 1 Get 1 Free Donuts",
        "20% Off on Coffee",
        "Free Donut with Any Coffee Purchase"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val binding = ItemPromoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        holder.bind(promos[position])
    }

    override fun getItemCount() = promos.size

    class PromoViewHolder(private val binding: ItemPromoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(promo: String) {
            binding.tvPromoTitle.text = promo
        }
    }
}