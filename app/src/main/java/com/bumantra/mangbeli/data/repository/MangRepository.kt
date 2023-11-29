package com.bumantra.mangbeli.data.repository

import com.bumantra.mangbeli.data.network.ApiService
import com.bumantra.mangbeli.data.response.RegisterResponse

class MangRepository(
    private val apiService: ApiService
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(
                name,
                email,
                password
            )
    }
    companion object {
        @Volatile
        private var instance: MangRepository? = null
        fun getInstance(
            apiService: ApiService
        ): MangRepository =
            instance ?: synchronized(this) {
                instance ?: MangRepository(apiService)
            }.also { instance = it }
    }

}