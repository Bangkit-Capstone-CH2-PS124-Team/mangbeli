package com.capstone.mangbeli.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mangbeli.data.repository.MangRepository

class MapsViewModel(private val repository: MangRepository) : ViewModel() {
    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>> get() = _currentLocation

    fun getMapsVendors() = repository.getMapsVendors()

    fun getMapsUsers() = repository.getMapsUsers()

    fun updateLocation(latitude: Double, longitude: Double) =
        repository.updateLocation(latitude, longitude)

    fun getDetailVendor(id:String) = repository.getDetailVendor(id)
    fun getDetailUser(id:String) = repository.getDetailUser(id)
    fun updateCurrentLocation(lat: Double, log: Double) {
        _currentLocation.value = Pair(lat, log)
    }
}