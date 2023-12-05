package com.bumantra.mangbeli.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient

object LocationHelper {

    fun requestLocationPermissions(
        fragment: Fragment,
        onSuccess: () -> Unit,
        onPermissionDenied: () -> Unit,
        onNeedBackgroundPermission: () -> Unit = {}
    ) {
        val requestPermissionLauncher =
            fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                ) {
                    onSuccess.invoke()
                    // Check if background location permission is granted
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                        permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] != true
                    ) {
                        // Background location permission not granted
                        onNeedBackgroundPermission.invoke()
                    } else {
                        // All required permissions granted
                        onSuccess.invoke()
                    }
                } else {
                    onPermissionDenied.invoke()
                }
            }

        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Add background location permission for Android 10 (Q) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        if (arePermissionsGranted(fragment.requireContext(), permissions)) {
            // All required permissions already granted
            onSuccess.invoke()
        } else {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun arePermissionsGranted(context: Context, permissions: List<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(
        fusedLocationClient: FusedLocationProviderClient,
        onSuccess: (location: Location) -> Unit,
        onError: () -> Unit
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                onSuccess.invoke(location)
            } else {
                onError.invoke()
            }
        }
    }
}
