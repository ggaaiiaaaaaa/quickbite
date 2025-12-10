package com.quickbite.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InventoryItem(
    val id: String = "",
    val name: String = "",
    var quantity: Int = 0,
    val lowStockThreshold: Int = 0
) : Parcelable
