package com.bumantra.mangbeli.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient

object LocationHelper {


    fun requestLocationPermissions(
        fragment: Fragment,
        onSuccess: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        val requestPermissionLauncher =
            fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                ) {
                    onSuccess.invoke()
                } else {
                    onPermissionDenied.invoke()
                }
            }

        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onSuccess.invoke()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
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