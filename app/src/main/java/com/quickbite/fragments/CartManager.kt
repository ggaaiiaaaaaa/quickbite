package com.quickbite.fragments

import com.quickbite.models.CartItem

object CartManager {
    private val cartItems = mutableListOf<CartItem>()
    private var promoDiscount = 0.0
    private var promoCode = ""

    fun addItem(item: CartItem) {
        val existingItemIndex = cartItems.indexOfFirst {
            it.productId == item.productId && it.customizations == item.customizations
        }

        if (existingItemIndex != -1) {
            val existingItem = cartItems[existingItemIndex]
            cartItems[existingItemIndex] = existingItem.copy(
                quantity = existingItem.quantity + item.quantity,
                totalPrice = (existingItem.quantity + item.quantity) * existingItem.basePrice
            )
        } else {
            cartItems.add(item)
        }
    }

    fun removeItem(position: Int) {
        if (position in cartItems.indices) {
            cartItems.removeAt(position)
        }
    }

    fun updateQuantity(position: Int, newQuantity: Int) {
        if (position in cartItems.indices && newQuantity > 0) {
            val item = cartItems[position]
            cartItems[position] = item.copy(
                quantity = newQuantity,
                totalPrice = newQuantity * item.basePrice
            )
        }
    }

    fun getItems(): List<CartItem> = cartItems.toList()

    fun getItemCount(): Int = cartItems.sumOf { it.quantity }

    fun getSubtotal(): Double = cartItems.sumOf { it.totalPrice }

    fun applyPromo(code: String, discount: Double) {
        promoCode = code
        promoDiscount = discount
    }

    fun getDiscount(): Double = promoDiscount

    fun getTotal(): Double = getSubtotal() - promoDiscount

    fun clearCart() {
        cartItems.clear()
        promoDiscount = 0.0
        promoCode = ""
    }
}