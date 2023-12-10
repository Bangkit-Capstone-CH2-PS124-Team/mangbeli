package com.capstone.mangbeli.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mangbeli.data.local.entity.UserEntity
import com.capstone.mangbeli.data.repository.LocationRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>> get() = _currentLocation

    fun updateCurrentLocation(lat: Double, log: Double) {
        _currentLocation.value = Pair(lat, log)
        val locationEntity = UserEntity(latitude = lat, longitude = log)
        viewModelScope.launch {
            locationRepository.insertLocation(locationEntity)
        }
    }
}