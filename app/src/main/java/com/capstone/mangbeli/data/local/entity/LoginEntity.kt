package com.capstone.mangbeli.data.local.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "login")
data class LoginEntity(
@field:SerializedName("name")
val name: String? = null,

@field:SerializedName("userId")
val userId: String? = null,

@field:SerializedName("email")
val email: String? = null,

@field:SerializedName("role")
val role: String,

@field:SerializedName("accessToken")
val token: String
)
