package com.capstone.mangbeli.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "vendor")
data class VendorEntity(
    @PrimaryKey
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("noHp")
    val noHp: String? = null,

    @field:SerializedName("distance")
    val distance: String? = null,

    @field:SerializedName("imageUrl")
    val imageUrl: String? = null,

    @field:SerializedName("nameVendor")
    val nameVendor: String? = null,

    @field:SerializedName("minPrice")
    val minPrice: Int? = null,

    @field:SerializedName("latitude")
    val latitude: Double?,

    @field:SerializedName("longitude")
    val longitude: Double?,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("vendorId")
    val vendorId: String? = null,

    @field:SerializedName("maxPrice")
    val maxPrice: Int? = null,

    @field:SerializedName("products")
    val products: String? = null,
)
