package com.capstone.mangbeli.model

data class VendorProfile(
    val nameVendor: String?,
    val noHp: String?,
    val products: List<String>?,
    val minPrice: Int?,
    val maxPrice: Int?
)