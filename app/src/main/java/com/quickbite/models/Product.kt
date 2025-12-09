package com.quickbite.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val nutritionalInfo: String = "",
    val ingredients: List<String> = emptyList(),
    var isAvailable: Boolean = true,
    var availabilitySchedule: String = "" // e.g., "Breakfast", "Lunch", "Dinner"
) : Parcelable
