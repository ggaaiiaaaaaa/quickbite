package com.quickbite.database.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val basePrice: Double,
    val quantity: Int,
    val customizations: String, // JSON string
    val totalPrice: Double,
    val userId: String,
    val addedAt: Long = System.currentTimeMillis()
)