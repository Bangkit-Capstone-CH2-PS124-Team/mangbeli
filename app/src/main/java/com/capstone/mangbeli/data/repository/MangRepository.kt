package com.capstone.mangbeli.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.mangbeli.data.local.pref.UserPref
import com.capstone.mangbeli.data.remote.network.ApiService
import com.capstone.mangbeli.data.remote.response.DataUser
import com.capstone.mangbeli.data.remote.response.DataVendor
import com.capstone.mangbeli.data.remote.response.ErrorResponse
import com.capstone.mangbeli.data.remote.response.ImageUploadResponse
import com.capstone.mangbeli.data.remote.response.ListVendorsItem
import com.capstone.mangbeli.data.remote.response.LoginResult
import com.capstone.mangbeli.data.remote.response.RegisterResponse
import com.capstone.mangbeli.data.remote.response.VendorsResponse
import com.capstone.mangbeli.model.LocationUpdate
import com.capstone.mangbeli.model.User
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.model.VendorProfile
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
        Log.e("TokenError", token)
    }

    suspend fun saveRole(role: String) {
        userPref.saveRole(role)
    }
    fun getSession(): Flow<User> {
        return userPref.getSession()
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        confPassword: String
    ): RegisterResponse {
        return apiService.register(
            name,
            email,
            password,
            confPassword
        )
    }
    fun getAllVendor(size: Int = 10, location: Int = 0): LiveData<Result<VendorsResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getVendors(size, location)
                Log.d("Repo", "getAllVendor: $response")
                emit(Result.Success(response))
            } catch (e: HttpException) {
                emit(Result.Error(e.message().toString()))
            }
        }
    fun login(email: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password).loginResult
            if (response != null) {
                Log.d("MangRepository", "getLoginResponse: $response")
                if (response.email != null) {
                saveToken(response.token,response.email,response.role)
                }
                emit(Result.Success(response))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d("Repository", "register user: $errorMessage ")
            emit(Result.Error(errorMessage.toString()))
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


    fun getVendorProfile(): LiveData<Result<DataVendor>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getVendorProfile().dataVendor
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
    fun deleteLocation(): LiveData<Result<ErrorResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.deleteLocation()
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
    fun updateVendorProfile(userData: VendorProfile): LiveData<Result<ErrorResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.updateVendorProfile(userData)
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
        fun refreshInstance() {
            instance = null
        }
    }

}