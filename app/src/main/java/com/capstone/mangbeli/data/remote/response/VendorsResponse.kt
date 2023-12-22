package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class VendorsResponse(

	@field:SerializedName("listVendors")
	val listVendors: List<ListVendorsItem> = emptyList(),

	@field:SerializedName("profileResult")
	val dataProfile: ListVendorsItem? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("currentPage")
	val currentPage: Int? = null
)

data class ListVendorsItem(

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
	val products: List<String?>? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null
)
