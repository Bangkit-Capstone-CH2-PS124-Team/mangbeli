package com.capstone.mangbeli.data.repository

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.mangbeli.data.local.entity.TokenEntity
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.capstone.mangbeli.data.VendorRemoteMediator
import com.capstone.mangbeli.data.local.entity.VendorEntity
import com.capstone.mangbeli.data.local.pref.UserPref
import com.capstone.mangbeli.data.local.room.UserDatabase
import com.capstone.mangbeli.data.remote.network.ApiConfig
import com.capstone.mangbeli.data.local.room.VendorDatabase
import com.capstone.mangbeli.data.remote.network.ApiService
import com.capstone.mangbeli.data.remote.response.DataUser
import com.capstone.mangbeli.data.remote.response.DataVendor
import com.capstone.mangbeli.data.remote.response.ErrorResponse
import com.capstone.mangbeli.data.remote.response.ImageUploadResponse
import com.capstone.mangbeli.data.remote.response.LoginResult
import com.capstone.mangbeli.data.remote.response.RegisterResponse
import com.capstone.mangbeli.model.LocationUpdate
import com.capstone.mangbeli.model.User
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.model.VendorProfile
import com.capstone.mangbeli.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.Response
import retrofit2.HttpException

class MangRepository(
    private val vendorDatabase: VendorDatabase,
    private val userPref: UserPref,
    private val apiService: ApiService
) {


    suspend fun saveToken(token: String, email: String, role: String) {
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
        return ApiConfig.getApiService().register(
            name,
            email,
            password,
            confPassword
        )
    }

    fun getAllVendors(location: Int = 1, isLocationNotEnable: Int = 1, search: String? = "", filter: String? = ""): Flow<PagingData<VendorEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                jumpThreshold = 1
            ),
            remoteMediator = VendorRemoteMediator(apiService, vendorDatabase, location, isLocationNotEnable, search, filter),
            pagingSourceFactory = {
                vendorDatabase.vendorDao().getAllVendor()
            }
        ).flow

    }

    suspend fun getMapsVendors(): List<VendorEntity> = vendorDatabase.vendorDao().getMapsAllVendor()

    fun login(email: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password).loginResult
            if (response != null) {
                Log.d("MangRepository", "getLoginResponse: $response")
                if (response.email != null) {
                    saveToken(response.token, response.email, response.role)
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

    suspend fun saveRefreshToken(refreshToken: String, Expired: String) {
        userPref.saveRefreshToken(refreshToken, Expired)
        Log.e("sabi", refreshToken)
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

    fun getDetailVendor(id: String): LiveData<Result<DataVendor>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailVendor(id).dataVendor
            if (response != null) {
                Log.d("Repo", "getDetailVendor: $response")
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
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

    fun logoutRefreshToken(refreshToken: String): LiveData<Result<ErrorResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.logout("refreshToken=$refreshToken")
                Log.d("MangRepository", "getUserProfile: $response")
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    suspend fun callRefreshToken(refreshToken: String): LiveData<Result<ErrorResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.refreshToken("refreshToken=$refreshToken")
                Log.d("MangRepository", "callRefreshToken: $response")
                // Lakukan sesuatu dengan response yang diterima
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
                Log.d("MangRepository", "callRefreshToken: ${e.message}")
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
            vendorDatabase: VendorDatabase,
            userPref: UserPref,
            apiService: ApiService
        ): MangRepository =
            instance ?: synchronized(this) {
                instance ?: MangRepository(vendorDatabase,userPref, apiService)
            }.also { instance = it }

        fun refreshInstance() {
            instance = null
        }
    }

}