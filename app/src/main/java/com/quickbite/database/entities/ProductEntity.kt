package com.quickbite.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val isSeasonal: Boolean = false,
    val isFreshToday: Boolean = false,
    val calories: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val protein: Int = 0,
    val allergens: String = "",
    val isAvailable: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)