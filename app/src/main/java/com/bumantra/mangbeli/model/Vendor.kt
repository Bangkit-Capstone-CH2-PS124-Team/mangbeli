package com.bumantra.mangbeli.model

data class Vendor(
    val id: String,
    val vendorName: String,
    val name: String,
    val distance: String?,
    val photoUrl: String,
    val products: List<String>
)