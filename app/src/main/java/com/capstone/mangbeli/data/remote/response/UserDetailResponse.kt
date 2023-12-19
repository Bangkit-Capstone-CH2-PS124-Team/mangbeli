package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserDetailResponse(

	@field:SerializedName("dataUser")
	val dataUser: DetailUser,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DetailUser(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("noHp")
	val noHp: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("favorite")
	val favorite: List<String?>? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
