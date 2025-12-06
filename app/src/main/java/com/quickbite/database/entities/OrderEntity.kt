package com.quickbite.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val userId: String,
    val branchId: String,
    val branchName: String,
    val orderType: String, // "Dine-In", "Take-Out", "Drive-Thru"
    val tableNumber: String = "",
    val pickupTime: String = "",
    val status: String, // "Pending", "Preparing", "Ready", "Completed", "Cancelled"
    val paymentMethod: String,
    val subtotal: Double,
    val discount: Double,
    val total: Double,
    val promoCode: String = "",
    val specialInstructions: String = "",
    val estimatedTime: Int = 15, // minutes
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)