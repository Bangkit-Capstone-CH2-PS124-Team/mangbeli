package com.bumantra.mangbeli.di

import com.bumantra.mangbeli.data.remote.network.ApiConfig
import com.bumantra.mangbeli.data.repository.MangRepository
object Injection {
    fun provideRepository(): MangRepository {
        val apiService = ApiConfig.getApiService()
        return MangRepository.getInstance(apiService)
    }
}