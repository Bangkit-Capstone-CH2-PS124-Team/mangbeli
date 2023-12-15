package com.capstone.mangbeli.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capstone.mangbeli.data.local.entity.VendorEntity

@Dao
interface VendorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVendor(story: List<VendorEntity>)

    @Query("SELECT * FROM vendor")
    fun getAllVendor(): PagingSource<Int, VendorEntity>

    @Query("SELECT * FROM vendor WHERE latitude IS NOT NULL and longitude IS NOT NULL")
    suspend fun getMapsAllVendor(): List<VendorEntity>

    @Query("DELETE FROM vendor")
    suspend fun deleteAll()
}