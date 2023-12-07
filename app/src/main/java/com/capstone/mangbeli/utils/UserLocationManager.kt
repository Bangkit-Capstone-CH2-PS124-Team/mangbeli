package com.capstone.mangbeli.utils

object UserLocationManager {
    private var currentLat: Float = 0F
    private var currentLog: Float = 0F

    fun setCurrentLocation(lat: Float, log: Float) {
        currentLat = lat
        currentLog = log
    }

    fun getCurrentLocation(): Pair<Float, Float> {
        return Pair(currentLat, currentLog)
    }
}