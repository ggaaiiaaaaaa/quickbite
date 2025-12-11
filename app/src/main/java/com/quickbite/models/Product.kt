package com.quickbite.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val nutritionalInfo: String = "",
    val ingredients: List<String> = emptyList(),
    var isAvailable: Boolean = true,
    var availabilitySchedule: String = "",
    val isSeasonal: Boolean = false,
    val isFreshToday: Boolean = false,
    val calories: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val protein: Int = 0,
    val allergens: String = ""
) : Parcelable {
    // Helper function to convert String id to Int
    fun getIntId(): Int = id.toIntOrNull() ?: 0
}