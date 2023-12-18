package com.capstone.mangbeli.model

data class User(
    val token: String,
    val email: String? = null,
    val role : String? = null,
    val expired : String? = null,
    val expiredToken: String? = null,
    val refreshToken: String? = null
)
