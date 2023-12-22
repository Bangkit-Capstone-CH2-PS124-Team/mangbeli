package com.capstone.mangbeli.di

import android.content.Context
import android.util.Log
import com.capstone.mangbeli.data.local.pref.UserPref
import com.capstone.mangbeli.data.local.pref.dataStore
import com.capstone.mangbeli.data.local.room.UserDatabase
import com.capstone.mangbeli.data.local.room.VendorDatabase
import com.capstone.mangbeli.data.remote.network.ApiConfig
import com.capstone.mangbeli.data.repository.LocationRepository
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.data.repository.TokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): MangRepository {
        val vendorDatabase = VendorDatabase.getDatabase(context)
        val pref = UserPref.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        Log.d("Cek Injection token", "provideRepository: ${user.token}")
        val apiService = ApiConfig.getApiService(user.token)
        return MangRepository.getInstance(vendorDatabase, pref, apiService)
    }

    fun provideTokenRepository(context: Context): TokenRepository {
        val pref = UserPref.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService2(user.refreshToken.toString())
        Log.d ("Cek Injection token", "provideTokenRepository: ${user.refreshToken}")
        return TokenRepository.getInstance(pref, apiService)
    }
    fun provideLocationRepository(context: Context): LocationRepository {
        val locationDatabase = UserDatabase.getDatabase(context)
        return LocationRepository.getInstance(locationDatabase)
    }
    fun refreshRepository() {
        MangRepository.refreshInstance()
    }
    fun refreshTokenRepository() {
        TokenRepository.refreshInstance()
    }
}