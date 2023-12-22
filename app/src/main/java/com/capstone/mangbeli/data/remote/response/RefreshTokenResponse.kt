package com.capstone.mangbeli.data.remote.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("accessToken")
    val accessToken: String,

    @field:SerializedName("errorMessage")
    val errorMessage: String
)
