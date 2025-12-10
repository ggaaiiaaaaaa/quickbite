package com.quickbite.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderId: String = "",
    val items: List<String> = emptyList(),
    val customizations: String = "",
    val customerName: String = "",
    val tableNumber: String = "",
    val orderType: String = "", // "table" or "pickup"
    val orderStatus: String = "Received",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
