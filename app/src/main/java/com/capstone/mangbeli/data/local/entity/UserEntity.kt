package com.capstone.mangbeli.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)val id: Long = 0,
    val latitude: Double,
    val longitude: Double
)