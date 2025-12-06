package com.quickbite.repository

import com.quickbite.database.AppDatabase
import com.quickbite.database.entities.CartItemEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(private val database: AppDatabase) {

    suspend fun addToCart(item: CartItemEntity) {
        database.cartDao().insertCartItem(item)
    }

    fun getCartItems(userId: String): Flow<List<CartItemEntity>> {
        return database.cartDao().getCartItems(userId)
    }

    suspend fun updateCartItem(item: CartItemEntity) {
        database.cartDao().updateCartItem(item)
    }

    suspend fun removeFromCart(item: CartItemEntity) {
        database.cartDao().deleteCartItem(item)
    }

    suspend fun clearCart(userId: String) {
        database.cartDao().clearCart(userId)
    }

    fun getCartItemCount(userId: String): Flow<Int> {
        return database.cartDao().getCartItemCount(userId)
    }
}