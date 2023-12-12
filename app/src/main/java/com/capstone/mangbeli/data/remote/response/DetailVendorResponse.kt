package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailVendorResponse(

	@field:SerializedName("dataVendor")
	val dataVendor: DataVendor? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

