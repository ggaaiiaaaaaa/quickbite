package com.quickbite.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")
data class BranchEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "Kiosk", "Mall", "Drive-Thru"
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val operatingHours: String,
    val coffeeMachineStatus: String, // "available", "busy", "unavailable"
    val donutAvailability: String, // "high", "low", "out"
    val distance: Double = 0.0,
    val isActive: Boolean = true
)