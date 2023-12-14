package com.capstone.mangbeli.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capstone.mangbeli.data.local.entity.RemoteKeys
import com.capstone.mangbeli.data.local.entity.VendorEntity

@Database(entities = [VendorEntity::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class VendorDatabase : RoomDatabase() {
    abstract fun vendorDao(): VendorDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: VendorDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): VendorDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VendorDatabase::class.java, "vendor_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}