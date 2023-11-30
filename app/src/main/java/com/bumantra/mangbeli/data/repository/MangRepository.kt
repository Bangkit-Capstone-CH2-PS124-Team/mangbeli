package com.bumantra.mangbeli.data.repository

import com.bumantra.mangbeli.data.local.pref.UserPref
import com.bumantra.mangbeli.data.remote.network.ApiService
import com.bumantra.mangbeli.data.remote.response.LoginResult
import com.bumantra.mangbeli.data.remote.response.RegisterResponse

class MangRepository(
    private val userPref: UserPref,
    private val apiService: ApiService
) {

    private suspend fun saveToken(token: String) {
        userPref.saveToken(token)
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(
                name,
                email,
                password
            )
    }

    suspend fun login(email: String, password: String): LoginResult {
        try {
            val loginResponse = apiService.login(email, password)
            val loginResult = loginResponse.loginResult

            if (loginResult != null) {
                saveToken(loginResult.token)
                return loginResult
            } else {
                userPref.logout()
                throw Exception(loginResponse.message)
            }
        } catch (e: Exception) {
            throw e
        }
    }
//    suspend fun logout() {
//        userPref.logout()
//    }
    companion object {
        @Volatile
        private var instance: MangRepository? = null
        fun getInstance(
            userPref: UserPref,
            apiService: ApiService
        ): MangRepository =
            instance ?: synchronized(this) {
                instance ?: MangRepository(userPref,apiService)
            }.also { instance = it }
    }

}