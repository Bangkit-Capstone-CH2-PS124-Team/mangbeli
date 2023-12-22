package com.capstone.mangbeli.utils

import com.capstone.mangbeli.data.local.entity.VendorEntity
import com.capstone.mangbeli.data.remote.response.ListVendorsItem

fun ListVendorsItem.toVendorEntity(): VendorEntity {
    return VendorEntity(
        userId = userId!!,
        noHp,
        distance,
        imageUrl,
        nameVendor,
        minPrice,
        latitude,
        longitude,
        name,
        vendorId,
        maxPrice,
        products = products?.joinToString(", ")
    )
}