package com.capstone.mangbeli.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mangbeli.data.local.entity.VendorEntity
import com.capstone.mangbeli.data.repository.MangRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: MangRepository) : ViewModel() {
    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>> get() = _currentLocation
    private val _vendors = MutableLiveData<List<VendorEntity>>()
    val vendors: LiveData<List<VendorEntity>> get() = _vendors

    init {
        viewModelScope.launch {
            _vendors.value = repository.getMapsVendors()
        }
    }

    fun updateCurrentLocation(lat: Double, log: Double) {
        _currentLocation.value = Pair(lat, log)
    }
}