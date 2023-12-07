package com.capstone.mangbeli.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.capstone.mangbeli.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertLocation(location: UserEntity)

    @Query("SELECT * FROM user ORDER BY id DESC LIMIT 1")
    suspend fun getLastLocation(): UserEntity?
}

