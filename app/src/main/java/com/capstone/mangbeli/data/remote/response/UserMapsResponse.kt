package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserMapsResponse(

	@field:SerializedName("listUsers")
	val listUsers: List<ListUsersItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListUsersItem(

	@field:SerializedName("distance")
	val distance: String? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("latitude")
	val latitude: Double,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("noHp")
	val noHp: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("favorite")
	val favorite: List<String?>? = null,

	@field:SerializedName("longitude")
	val longitude: Double
)
