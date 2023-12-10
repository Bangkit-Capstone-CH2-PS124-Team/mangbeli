package com.capstone.mangbeli.data.remote.network

import com.capstone.mangbeli.data.remote.response.ErrorResponse
import com.capstone.mangbeli.data.remote.response.LoginResponse
import com.capstone.mangbeli.data.remote.response.RegisterResponse
import com.capstone.mangbeli.data.remote.response.UserResponse
import com.capstone.mangbeli.model.LocationUpdate
import com.capstone.mangbeli.model.UserProfile
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confPassword") confPassword: String,
        @Field("role") role: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("/user/profile")
    suspend fun getUserProfile(): UserResponse

    @PATCH("/location")
    suspend fun updateLocation(
        @Body locationUpdate: LocationUpdate
    ): ErrorResponse

    @PATCH("/user/profile")
    suspend fun updateUserProfile(
        @Body userProfile: UserProfile
    ): ErrorResponse
}