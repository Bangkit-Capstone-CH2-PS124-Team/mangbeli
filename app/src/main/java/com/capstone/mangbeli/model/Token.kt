package com.capstone.mangbeli.model

data class Token(
    val accessToken: String,
    val refreshToken: String,
    val expiredAt: Long
)
