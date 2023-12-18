package com.capstone.mangbeli.data.repository

import android.util.Log
import com.capstone.mangbeli.data.local.pref.UserPref
import com.capstone.mangbeli.data.remote.network.ApiService
import com.capstone.mangbeli.model.User
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TokenRepository(
    private val userPref: UserPref,
    private val apiService: ApiService,
) {
    suspend fun saveToken(token: String, expiredAksesToken: String) {
        userPref.saveTokenNew(token, expiredAksesToken)
        Log.e("saveTokenNew", token)
    }
    suspend fun callRefreshToken(){
            try {
                val response = apiService.refreshToken()
                val result = response.accessToken
                val calendar = Calendar.getInstance()
                calendar.time = Calendar.getInstance().time

                // Tambahkan 10 menit
                calendar.add(Calendar.MINUTE, 10)

                val currentTimePlus10Minutes = calendar.time
                saveToken(result, currentTimePlus10Minutes.toString())

                Log.d("coba", "ayam: $response, $currentTimePlus10Minutes")
                // Lakukan sesuatu dengan response yang diterima
            } catch (e: Exception) {
                Log.d("coba", "callRefreshToken: ${e.message}")
            }
        }

    fun getSession(): Flow<User> {
        return userPref.getSession()
    }
    suspend fun logoutRefreshToken() {
        userPref.logout()
    }


    companion object {
        @Volatile
        private var instance: TokenRepository? = null
        fun getInstance(
            userPref: UserPref,
            apiService: ApiService
        ): TokenRepository =
            instance ?: synchronized(this) {
                instance ?: TokenRepository(userPref, apiService)
            }.also { instance = it }
        fun refreshInstance() {
            instance = null
        }
    }
}