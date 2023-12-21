package com.capstone.mangbeli.ui.pedagang.homepedagang

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.FragmentHomePedagangBinding
import com.capstone.mangbeli.model.VendorsData
import com.capstone.mangbeli.ui.maps.GeofenceBroadcastReceiver
import com.capstone.mangbeli.ui.profile.ProfileViewModelFactory
import com.capstone.mangbeli.utils.LocationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HomePedagangFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomePedagangBinding? = null
    private val geofenceRadius = 100.0
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap1: GoogleMap
    private lateinit var googleMap2: GoogleMap
    private val viewModel by viewModels<HomeVendorViewModel> {
        ProfileViewModelFactory.getInstance(requireActivity())
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Notification permission granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notification permission rejected",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }



    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomePedagangBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (Build.VERSION.SDK_INT >= 33) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        initMap()

        return root
    }
    @SuppressLint("MissingPermission", "VisibleForTests")
    private fun addGeofence(vendorId: String, latLng: LatLng) {

        Log.d("GeofenceMaps", "addGeofence: ${latLng.latitude}, ${latLng.longitude}")
        val geofence = Geofence.Builder()
            .setRequestId(vendorId)
            .setCircularRegion(
                latLng.latitude,
                latLng.longitude,
                geofenceRadius.toFloat()
            )
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
                        Toast.makeText(requireContext(), "Geofencing added", Toast.LENGTH_SHORT)
                            .show()
                    }
                    addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Geofencing not added, ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        }
    }

    private fun initMap() {
        val mapFragment1 =
            childFragmentManager.findFragmentById(R.id.google_map_pembeli) as SupportMapFragment
        mapFragment1.getMapAsync(this)
        val mapFragment2 =
            childFragmentManager.findFragmentById(R.id.google_map_tandai) as SupportMapFragment
        mapFragment2.getMapAsync(this)

        LocationHelper.requestLocationPermissions(
            this,
            {
               getUserLocation()
            },
            {
                onPermissionDenied()
            },
            {
                requestBackgroundLocationPermission()
            }
        )
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
    }
    override fun onMapReady(googleMap: GoogleMap) {
        if (!::googleMap1.isInitialized) {
            googleMap1 = googleMap
            with(googleMap1) {
                uiSettings.isZoomControlsEnabled = true
                uiSettings.isIndoorLevelPickerEnabled = true
                uiSettings.isMapToolbarEnabled = true
                uiSettings.isCompassEnabled = true
            }

            viewModel.currentLocation.observe(viewLifecycleOwner) {
                addMyLocation(it.first, it.second)
                addUsersMarker(it)
            }
        } else if (!::googleMap2.isInitialized) {
            googleMap2 = googleMap
            binding.btnMark.setOnClickListener {
                viewModel.currentLocation.observe(viewLifecycleOwner) {
                    addMarker(it.first, it.second)
                }
            }

        }



    }

    private fun addMarker(first: Double, second: Double) {
        val latLng = LatLng(first,second)
        val marker = mutableListOf<LatLng>()
        marker.add(latLng)
        marker.forEach { location ->
            googleMap2.addMarker(
                MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
        }
        googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
    }

    private fun getUserLocation() {
        LocationHelper.getLastKnownLocation(
            fusedLocationClient,
            { location ->
                viewModel.updateCurrentLocation(location.latitude, location.longitude)
            },
            {
                Toast.makeText(
                    requireContext(),
                    "Location not available, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun addUsersMarker(myLocation: Pair<Double, Double>){
        val maxDistance = 1000.0

        VendorsData.usersDummy.forEach { data ->
            val latLng = LatLng(data.latitude, data.longitude)
            val distance = calculateDistance(
                myLocation.first,
                myLocation.second,
                data.latitude,
                data.longitude
            )
            val listFavorite = data.favorite.joinToString(separator = ", ")

            if (distance <= maxDistance) {
                googleMap1.addMarker(
                    MarkerOptions().position(latLng).title(data.name).snippet(listFavorite)
                )
                googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                addGeofence(data.name, latLng)
            } else {
                googleMap1.addMarker(
                    MarkerOptions().position(latLng).title(data.name).snippet(listFavorite)
                )
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun addMyLocation(lat: Double, log: Double) {
        val myLocation = LatLng(lat, log)
        googleMap1.addCircle(
            CircleOptions()
                .center(myLocation)
                .radius(geofenceRadius)
                .fillColor(0x2200FF00)
                .strokeColor(Color.CYAN)
                .strokeWidth(3f)
        )
        googleMap1.isMyLocationEnabled = true
        googleMap1.setOnMyLocationClickListener {
            googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f))
        }
    }

    private fun requestBackgroundLocationPermission() {
        AlertDialog.Builder(requireContext())
            .setTitle("Background Location Permission Required")
            .setMessage("This feature requires background location permission. Please enable it in the app settings.")
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle cancellation
            }
            .show()
    }
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
    private fun onPermissionDenied() {
        Toast.makeText(
            requireContext(),
            "Location permission denied",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

}