package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileVendorResponse(

	@field:SerializedName("dataVendor")
	val dataVendor: DataVendor? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataVendor(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("nameVendor")
	val nameVendor: String? = null,

	@field:SerializedName("minPrice")
	val minPrice: Int? = null,

	@field:SerializedName("vendorId")
	val vendorId: String? = null,

	@field:SerializedName("maxPrice")
	val maxPrice: Int? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("products")
	val products: List<String>? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
