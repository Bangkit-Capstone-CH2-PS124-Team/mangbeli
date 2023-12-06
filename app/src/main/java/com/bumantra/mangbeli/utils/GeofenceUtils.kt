package com.bumantra.mangbeli.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

object GeofenceUtils {

    @SuppressLint("StaticFieldLeak")
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofencePendingIntent: PendingIntent

    fun initializeGeofencing(context: Context, pendingIntent: PendingIntent) {
        geofencingClient = LocationServices.getGeofencingClient(context)
        geofencePendingIntent = pendingIntent
    }

    @SuppressLint("VisibleForTests", "MissingPermission")
    fun addGeofence(
        vendorId: String,
        latitude: Double,
        longitude: Double,
        radius: Float,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(vendorId)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(5000)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
                        onSuccess.invoke()
                    }
                    addOnFailureListener {
                        onFailure.invoke(it)
                    }
                }
            }
        }
    }
}