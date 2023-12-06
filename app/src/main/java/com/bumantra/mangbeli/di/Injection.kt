package com.bumantra.mangbeli.di

import android.content.Context
import com.bumantra.mangbeli.data.local.pref.UserPref
import com.bumantra.mangbeli.data.local.pref.dataStore
import com.bumantra.mangbeli.data.local.room.UserDao
import com.bumantra.mangbeli.data.local.room.UserDatabase
import com.bumantra.mangbeli.data.remote.network.ApiConfig
import com.bumantra.mangbeli.data.repository.LocationRepository
import com.bumantra.mangbeli.data.repository.MangRepository
object Injection {
    fun provideRepository(context: Context): MangRepository {
        val pref = UserPref.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return MangRepository.getInstance(pref, apiService)
    }
    fun provideLocationRepository(context: Context): LocationRepository {
        val locationDatabase = UserDatabase.getDatabase(context)
        return LocationRepository.getInstance(locationDatabase)
    }
}