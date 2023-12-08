package com.capstone.mangbeli.data.repository

import com.capstone.mangbeli.data.local.entity.UserEntity
import com.capstone.mangbeli.data.local.room.UserDatabase

class LocationRepository(private val database: UserDatabase) {
    private val userLocationDao = database.userDao()
    suspend fun insertLocation(location: UserEntity) {
        userLocationDao.insertLocation(location)
    }

    fun getLastLocation(): UserEntity {
        return userLocationDao.getLastLocation()
    }

    companion object {
        @Volatile
        private var instance: LocationRepository? = null
        fun getInstance(
            database: UserDatabase
        ): LocationRepository =
            instance ?: synchronized(this) {
                instance ?: LocationRepository(database)
            }.also { instance = it }
    }
}