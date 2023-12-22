package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class ImageUploadResponse(

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
