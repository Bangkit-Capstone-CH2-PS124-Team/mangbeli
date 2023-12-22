package com.capstone.mangbeli.model

data class UserDummy (
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String,
    val favorite: List<String>
)