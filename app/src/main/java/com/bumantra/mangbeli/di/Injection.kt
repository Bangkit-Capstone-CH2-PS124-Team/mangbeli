package com.bumantra.mangbeli.di

import android.content.Context
import com.bumantra.mangbeli.data.local.pref.UserPref
import com.bumantra.mangbeli.data.local.pref.dataStore
import com.bumantra.mangbeli.data.remote.network.ApiConfig
import com.bumantra.mangbeli.data.repository.MangRepository
object Injection {
    fun provideRepository(context: Context): MangRepository {
        val pref = UserPref.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return MangRepository.getInstance(pref, apiService)
    }
}