package com.capstone.mangbeli.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.model.SendNotif

class DetailViewModel(private val repository: MangRepository) : ViewModel() {
    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>> get() = _currentLocation

    fun getDetailVendor(id:String) = repository.getDetailVendor(id)

    fun sendNotif(fcm: SendNotif) = repository.sendNotif(fcm)

    fun updateLocation(latitude: Double, longitude: Double) =
        repository.updateLocation(latitude, longitude)
    fun deleteLocation() =
        repository.deleteLocation()
}