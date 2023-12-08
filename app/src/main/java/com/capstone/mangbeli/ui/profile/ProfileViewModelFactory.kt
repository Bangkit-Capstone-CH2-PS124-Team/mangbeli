package com.capstone.mangbeli.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.mangbeli.data.repository.LocationRepository
import com.capstone.mangbeli.di.Injection
import com.capstone.mangbeli.ui.pedagang.homepedagang.HomeVendorViewModel

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val locationRepository: LocationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(locationRepository) as T
        }
        if (modelClass.isAssignableFrom(HomeVendorViewModel::class.java)) {
            return HomeVendorViewModel(locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var INSTANCE: ProfileViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ProfileViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ProfileViewModelFactory::class.java) {
                    INSTANCE = ProfileViewModelFactory(Injection.provideLocationRepository(context))
                }
            }
            return INSTANCE as ProfileViewModelFactory
        }
    }
}