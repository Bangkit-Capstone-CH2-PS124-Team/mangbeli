package com.capstone.mangbeli.model

data class UserProfile(
    val name: String? = null,
    val role : String? = null,
    val noHp: String? = null,
    val favorite: List<String>? = null
)