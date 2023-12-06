package com.bumantra.mangbeli.ui.maps

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumantra.mangbeli.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("VisibleForTests")
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, "onReceive: $errorMessage" )
                sendNotification(context, errorMessage)
                return
            }

            val geofencingTransition = geofencingEvent.geofenceTransition

            if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                val geofenceTransitionString =
                    when (geofencingTransition) {
                        Geofence.GEOFENCE_TRANSITION_ENTER -> "sedang mendekat nih!"
                        Geofence.GEOFENCE_TRANSITION_DWELL -> "sedang dalam area kamu!"
                        else -> "Invalid bos"
                    }

                val triggerGeofences = geofencingEvent.triggeringGeofences
                triggerGeofences.forEach { geofence ->
                    val vendorId = geofence.requestId
                    val geofenceTransitionDetails = "$vendorId $geofenceTransitionString"
                    Log.d(TAG, "onReceive: $geofenceTransitionDetails")
                    sendNotification(context, geofenceTransitionDetails)
                }
            } else {
                val errorMessage = "Invalid : $geofencingTransition"
                Log.e(TAG, "onReceive: $errorMessage" )
                sendNotification(context, errorMessage)
            }
        }
    }

    private fun sendNotification(context: Context, geofenceTransitionDetails: String) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(geofenceTransitionDetails)
            .setContentText("Hehehe siapa yang laper nih?")
            .setSmallIcon(R.drawable.baseline_notifications_active_24)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val TAG = "GeofenceBroadcast"
        const val ACTION_GEOFENCE_EVENT = "GeofenceEvent"
        private const val CHANNEL_ID = "1"
        private const val CHANNEL_NAME = "MangBeli Channel"
        private const val NOTIFICATION_ID = 1
    }
}