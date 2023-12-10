package com.capstone.mangbeli.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mangbeli.data.local.entity.UserEntity
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.model.UserProfile

class ProfileViewModel(private val userRepository: MangRepository) : ViewModel() {

    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>> get() = _currentLocation

    fun updateCurrentLocation(lat: Double, log: Double) {
        _currentLocation.value = Pair(lat, log)
    }

    fun getUserProfile() = userRepository.getUserProfile()

    fun updateLocation(latitude: Double, longitude: Double) =
        userRepository.updateLocation(latitude, longitude)

    fun updateUserProfile(updateUser: UserProfile) = userRepository.updateUserProfile(updateUser)
}