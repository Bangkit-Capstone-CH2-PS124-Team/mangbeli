package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("dataUser")
	val dataUser: DataUser? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataUser(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("no_hp")
	val noHp: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("favorite")
	val favorite: List<String?>? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
