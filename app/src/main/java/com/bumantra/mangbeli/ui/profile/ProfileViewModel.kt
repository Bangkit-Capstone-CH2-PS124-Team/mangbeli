package com.bumantra.mangbeli.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumantra.mangbeli.data.repository.MangRepository

class ProfileViewModel(private val repository: MangRepository): ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Rifando"
    }
    val text: LiveData<String> = _text

    private val _currentLocation = MutableLiveData<Pair<Float, Float>>()
    val currentLocation: LiveData<Pair<Float, Float>> get()  = _currentLocation

    fun updateCurrentLocation(lat: Float, log: Float) {
        _currentLocation.value = Pair(lat, log)
    }
}