package com.capstone.mangbeli.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
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
                Log.d("PermissionDebug", "PermissionsResult: $permissions")
                when {
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                        // Precise location access granted.
                        // Sekarang kita dapat memeriksa izin ACCESS_BACKGROUND_LOCATION
                        checkBackgroundPermission(fragment, onNeedBackgroundPermission, onSuccess)
                    }

                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                        // Only approximate location access granted.
                        onSuccess.invoke()
                    }
                    else -> {
                        Log.d("PermissionDebug", "Permission denied")
                        onPermissionDenied.invoke()
                    }
                }
            }

        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (arePermissionsGranted(fragment.requireContext(), permissions)) {
            // All required permissions already granted
            Log.d("PermissionDebug", "All required permissions already granted")
            onSuccess.invoke()
        } else {
            Log.d("PermissionDebug", "Requesting permissions: $permissions")
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun checkBackgroundPermission(
        fragment: Fragment,
        onNeedBackgroundPermission: () -> Unit,
        onSuccess: () -> Unit
    ) {
        // Memeriksa izin ACCESS_BACKGROUND_LOCATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permintaan izin ACCESS_BACKGROUND_LOCATION jika belum diberikan
            onNeedBackgroundPermission.invoke()
        } else {
            // Jika izin ACCESS_BACKGROUND_LOCATION sudah diberikan atau perangkat tidak menjalankan Android 10 (Q) atau versi di atasnya,
            // maka kita dapat melanjutkan ke onSuccess
            onSuccess.invoke()
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