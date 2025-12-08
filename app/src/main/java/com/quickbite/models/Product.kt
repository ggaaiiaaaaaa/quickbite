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
    val rating: Double = 0.0,
    val ratingsCount: Int = 0,
    val category: String = ""
) : Parcelable
