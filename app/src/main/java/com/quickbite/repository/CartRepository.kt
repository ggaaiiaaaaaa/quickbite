package com.quickbite.repository

import com.google.gson.Gson
import com.quickbite.database.AppDatabase
import com.quickbite.database.entities.CartItemEntity
import com.quickbite.models.CartItem
import com.quickbite.models.CustomizationOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repository for managing cart operations with Room database
 * Replaces the in-memory CartManager with persistent storage
 */
class CartRepository(private val database: AppDatabase) {

    private val cartDao = database.cartDao()
    private val gson = Gson()

    /**
     * Add item to cart or update quantity if item with same customizations exists
     */
    suspend fun addToCart(userId: String, cartItem: CartItem) {
        // Check if item with same customizations exists
        val existingItems = cartDao.getCartItems(userId).first()
        val existingItem = existingItems.find {
            it.productId == cartItem.productId.toString() &&
                    it.customizations == gson.toJson(cartItem.customizations)
        }

        if (existingItem != null) {
            // Update quantity
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + cartItem.quantity,
                totalPrice = existingItem.basePrice * (existingItem.quantity + cartItem.quantity)
            )
            cartDao.updateCartItem(updatedItem)
        } else {
            // Add new item
            val entity = CartItemEntity(
                productId = cartItem.productId.toString(),
                productName = cartItem.productName,
                productImageUrl = cartItem.imageUrl,
                basePrice = cartItem.basePrice,
                quantity = cartItem.quantity,
                customizations = gson.toJson(cartItem.customizations),
                totalPrice = cartItem.totalPrice,
                userId = userId,
                addedAt = System.currentTimeMillis()
            )
            cartDao.insertCartItem(entity)
        }
    }

    /**
     * Get cart items as Flow for observing changes
     */
    fun getCartItems(userId: String): Flow<List<CartItemEntity>> {
        return cartDao.getCartItems(userId)
    }

    /**
     * Convert CartItemEntity to CartItem model
     */
    fun mapToCartItem(entity: CartItemEntity): CartItem {
        return CartItem(
            productId = entity.productId.toIntOrNull() ?: 0,
            productName = entity.productName,
            basePrice = entity.basePrice,
            quantity = entity.quantity,
            customizations = try {
                gson.fromJson(entity.customizations, CustomizationOptions::class.java)
            } catch (e: Exception) {
                CustomizationOptions() // Return default if parsing fails
            },
            totalPrice = entity.totalPrice,
            imageUrl = entity.productImageUrl
        )
    }

    /**
     * Update cart item quantity
     */
    suspend fun updateCartItem(item: CartItemEntity) {
        val updatedItem = item.copy(
            totalPrice = item.basePrice * item.quantity
        )
        cartDao.updateCartItem(updatedItem)
    }

    /**
     * Update item quantity by position
     */
    suspend fun updateQuantity(userId: String, position: Int, newQuantity: Int) {
        val items = cartDao.getCartItems(userId).first()

        if (position in items.indices && newQuantity > 0) {
            val item = items[position]
            val updatedItem = item.copy(
                quantity = newQuantity,
                totalPrice = item.basePrice * newQuantity
            )
            cartDao.updateCartItem(updatedItem)
        }
    }

    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(item: CartItemEntity) {
        cartDao.deleteCartItem(item)
    }

    /**
     * Remove item by position
     */
    suspend fun removeItemByPosition(userId: String, position: Int) {
        val items = cartDao.getCartItems(userId).first()

        if (position in items.indices) {
            cartDao.deleteCartItem(items[position])
        }
    }

    /**
     * Clear entire cart
     */
    suspend fun clearCart(userId: String) {
        cartDao.clearCart(userId)
    }

    /**
     * Get cart item count as Flow
     */
    fun getCartItemCount(userId: String): Flow<Int> {
        return cartDao.getCartItemCount(userId)
    }

    /**
     * Calculate subtotal
     */
    fun getSubtotal(userId: String): Flow<Double> {
        return getCartItems(userId).map { items ->
            items.sumOf { it.totalPrice }
        }
    }

    /**
     * Calculate total with discount
     */
    fun getTotal(userId: String, discount: Double = 0.0): Flow<Double> {
        return getSubtotal(userId).map { subtotal ->
            subtotal - discount
        }
    }

    /**
     * Get total item count (sum of quantities)
     */
    fun getTotalItemCount(userId: String): Flow<Int> {
        return getCartItems(userId).map { items ->
            items.sumOf { it.quantity }
        }
    }

    /**
     * Check if cart is empty
     */
    fun isCartEmpty(userId: String): Flow<Boolean> {
        return getCartItemCount(userId).map { it == 0 }
    }

    /**
     * Get cart summary (for checkout)
     */
    suspend fun getCartSummary(userId: String): CartSummary {
        val items = cartDao.getCartItems(userId).first()

        val subtotal = items.sumOf { it.totalPrice }
        val itemCount = items.size
        val totalQuantity = items.sumOf { it.quantity }

        return CartSummary(
            items = items,
            subtotal = subtotal,
            itemCount = itemCount,
            totalQuantity = totalQuantity
        )
    }
}

/**
 * Data class for cart summary
 */
data class CartSummary(
    val items: List<CartItemEntity>,
    val subtotal: Double,
    val itemCount: Int,
    val totalQuantity: Int
)