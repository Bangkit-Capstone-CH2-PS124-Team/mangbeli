package com.capstone.mangbeli.model

data class Vendor(
    val id: String,
    val vendorName: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String,
    val products: List<String>
)