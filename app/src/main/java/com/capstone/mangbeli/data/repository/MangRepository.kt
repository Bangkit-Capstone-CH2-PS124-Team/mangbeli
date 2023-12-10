package com.capstone.mangbeli.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.mangbeli.data.local.pref.UserPref
import com.capstone.mangbeli.data.remote.network.ApiService
import com.capstone.mangbeli.data.remote.response.DataUser
import com.capstone.mangbeli.data.remote.response.ErrorResponse
import com.capstone.mangbeli.data.remote.response.ImageUploadResponse
import com.capstone.mangbeli.data.remote.response.LoginResult
import com.capstone.mangbeli.data.remote.response.RegisterResponse
import com.capstone.mangbeli.model.LocationUpdate
import com.capstone.mangbeli.model.User
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.HttpException

class MangRepository(
    private val userPref: UserPref,
    private val apiService: ApiService
) {

    private suspend fun saveToken(token: String, email: String, role: String) {
        userPref.saveToken(token, email, role)
        Log.e("TokenError", "$token + $email")
    }

    fun getSession(): Flow<User> {
        return userPref.getSession()
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        confPassword: String,
        role: String
    ): RegisterResponse {
        return apiService.register(
            name,
            email,
            password,
            confPassword,
            role
        )
    }

    suspend fun login(email: String, password: String): LoginResult {
        try {
            val loginResponse = apiService.login(email, password)
            val loginResult = loginResponse.loginResult

            if (loginResult != null) {
                saveToken(loginResult.token, email, loginResult.role)
                return loginResult
            } else {
                userPref.logout()
                throw Exception(loginResponse.message)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun logout() {
        userPref.logout()
    }

    fun getUserProfile(): LiveData<Result<DataUser>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getUserProfile().dataUser
            if (response != null) {
                Log.d("MangRepository", "getUserProfile: $response")
                emit(Result.Success(response))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun updateLocation(latitude: Double, longitude: Double): LiveData<Result<ErrorResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val locationUpdate = LocationUpdate(latitude, longitude)
                val response = apiService.updateLocation(locationUpdate)
                Log.d("MangRepository", "getUserProfile: $response")
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun updateUserProfile(userData: UserProfile): LiveData<Result<ErrorResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.updateUserProfile(userData)
            Log.d("MangRepository", "getUserProfile: $response")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadImage(imageFile: MultipartBody.Part): LiveData<Result<ImageUploadResponse>> =
        liveData {
            Log.d("Repo", "uploadImage: $imageFile")
            emit(Result.Loading)
            try {
                val response = apiService.uploadImage(imageFile)
                emit(Result.Success(response))
            } catch (e: Exception) {
                Log.d("Repo", "uploadImage: ${e.message}")
                emit(Result.Error(e.message.toString()))
            }
        }

    companion object {
        @Volatile
        private var instance: MangRepository? = null
        fun getInstance(
            userPref: UserPref,
            apiService: ApiService
        ): MangRepository =
            instance ?: synchronized(this) {
                instance ?: MangRepository(userPref, apiService)
            }.also { instance = it }
    }

}