package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class VendorsMapsResponse(

	@field:SerializedName("listVendors")
	val listVendors: List<ListMapsVendorsItem> ,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListMapsVendorsItem(

	@field:SerializedName("no_hp")
	val noHp: String? = null,

	@field:SerializedName("distance")
	val distance: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("nameVendor")
	val nameVendor: String? = null,

	@field:SerializedName("minPrice")
	val minPrice: Int? = null,

	@field:SerializedName("latitude")
	val latitude: Double,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("vendorId")
	val vendorId: String,

	@field:SerializedName("maxPrice")
	val maxPrice: Int? = null,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("products")
	val products: List<String?>? = null,

	@field:SerializedName("longitude")
	val longitude: Double
)
