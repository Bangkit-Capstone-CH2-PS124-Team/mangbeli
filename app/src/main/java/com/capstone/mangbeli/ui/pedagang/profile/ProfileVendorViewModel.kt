package com.capstone.mangbeli.ui.pedagang.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.model.VendorProfile
import okhttp3.MultipartBody

class ProfileVendorViewModel(private val userRepository: MangRepository) : ViewModel() {

    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>> get() = _currentLocation
    fun updateCurrentLocation(lat: Double, log: Double) {
        _currentLocation.value = Pair(lat, log)
    }
    fun getVendorProfile() = userRepository.getVendorProfile()
    fun getUserProfile() = userRepository.getUserProfile()
    fun updateUserProfile(updateUser: UserProfile) = userRepository.updateUserProfile(updateUser)
    fun updateLocation(latitude: Double, longitude: Double) =
        userRepository.updateLocation(latitude, longitude)
    fun deleteLocation() =
        userRepository.deleteLocation()
    fun updateVendorProfile(updateVendor: VendorProfile) = userRepository.updateVendorProfile(updateVendor)
    fun uploadImage(imageFile: MultipartBody.Part) = userRepository.uploadImage(imageFile)
}