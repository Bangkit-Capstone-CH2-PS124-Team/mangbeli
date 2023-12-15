package com.capstone.mangbeli.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "token", indices = [Index(value = ["accessToken"], unique = true)])
data class TokenEntity(
    @PrimaryKey
    var accessToken: String,
    var refreshToken: String
)