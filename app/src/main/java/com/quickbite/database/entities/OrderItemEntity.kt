package com.quickbite.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val customizations: String, // JSON string
    val price: Double
)