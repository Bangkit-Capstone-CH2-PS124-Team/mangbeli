package com.capstone.mangbeli.model

data class UserProfile(
    val name: String?,
    val role : String? = null,
    val noHp: String?,
    val favorite: List<String>?
)