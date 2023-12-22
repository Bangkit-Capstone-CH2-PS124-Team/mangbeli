package com.capstone.mangbeli.model

data class VendorProfile(
    val vendorId: String? = null,
    val userId: String? = null,
    val nameVendor: String? = null,
    val products: List<String>? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null

)