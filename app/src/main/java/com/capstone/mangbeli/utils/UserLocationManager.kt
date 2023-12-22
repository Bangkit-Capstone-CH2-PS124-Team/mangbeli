package com.capstone.mangbeli.utils

object UserLocationManager {
    private var currentLat: Double = 0.0
    private var currentLog: Double = 0.0

    fun setCurrentLocation(lat: Double, log: Double) {
        currentLat = lat
        currentLog = log
    }

    fun getCurrentLocation(): Pair<Double, Double> {
        return Pair(currentLat, currentLog)
    }
}