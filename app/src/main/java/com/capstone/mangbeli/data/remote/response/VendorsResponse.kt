package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class VendorsResponse(

	@field:SerializedName("listVendors")
	val listVendors: List<ListVendorsItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListVendorsItem(

	@field:SerializedName("no_hp")
	val noHp: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("nameVendor")
	val nameVendor: String? = null,

	@field:SerializedName("minPrice")
	val minPrice: Int? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("vendorId")
	val vendorId: String? = null,

	@field:SerializedName("maxPrice")
	val maxPrice: Int? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("products")
	val products: List<String>? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null
)
