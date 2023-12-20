package com.capstone.mangbeli.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)val id: Long = 0,
    val latitude: Double,
    val longitude: Double
)
