package com.quickbite.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Transaction(
    val transactionId: String = "",
    val customerName: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "",
    @ServerTimestamp
    val date: Date? = null,
    val items: List<OrderItem> = emptyList() // Assuming you have an OrderItem model
) : Parcelable

@Parcelize
data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
) : Parcelable
