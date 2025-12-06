package com.quickbite.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)