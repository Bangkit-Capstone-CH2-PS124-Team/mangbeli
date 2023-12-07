package com.capstone.mangbeli.ui.maps

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
import androidx.lifecycle.ViewModelProvider
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.FragmentMapsBinding
import com.capstone.mangbeli.model.VendorsData.vendors
import com.capstone.mangbeli.ui.profile.ProfileViewModel
import com.capstone.mangbeli.ui.profile.ProfileViewModelFactory
import com.capstone.mangbeli.utils.LocationHelper
import com.capstone.mangbeli.utils.VectorToBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val geofenceRadius = 100.0
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Float = 0F
    private var currentLog: Float = 0F
    private val profileViewModel by viewModels<ProfileViewModel> {
        ProfileViewModelFactory.getInstance(requireActivity())
    }

    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapsViewModel =
            ViewModelProvider(this)[MapsViewModel::class.java]

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (Build.VERSION.SDK_INT >= 33) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        initMap()

        return root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true


        profileViewModel.currentLocation.observe(viewLifecycleOwner) {
            currentLat = it.first
            currentLog = it.second
            addUserLocation(currentLat, currentLog)
            addManyMarker(it)
        }

//        addManyMarker()

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
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    private fun getUserLocation() {
        LocationHelper.getLastKnownLocation(
            fusedLocationClient,
            { location ->
                currentLat = location.latitude.toFloat()
                currentLog = location.longitude.toFloat()

                currentLat.let { lat ->
                    currentLog.let { log ->
                        profileViewModel.updateCurrentLocation(lat, log)
                        Log.d("Maps", "getUserLocation: $lat,$log")
                    }
                }
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

    private fun onPermissionDenied() {
        Toast.makeText(
            requireContext(),
            "Location permission denied",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun addUserLocation(lat: Float, log: Float) {
        val userdummyLocation = LatLng(lat.toDouble(), log.toDouble())
        mMap.addMarker(
            MarkerOptions()
                .position(userdummyLocation)
        )
        mMap.addCircle(
            CircleOptions()
                .center(userdummyLocation)
                .radius(geofenceRadius)
                .fillColor(0x2200FF00)
                .strokeColor(Color.CYAN)
                .strokeWidth(3f)
        )

        mMap.setOnMyLocationClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userdummyLocation, 18f))
        }
    }

    private fun addManyMarker(userLocation: Pair<Float, Float>) {
        val iconConverter = VectorToBitmap()
        val maxDistance = 1000.0

        vendors.forEach { data ->
            val latLng = LatLng(data.latitude, data.longitude)
            val distance = calculateDistance(
                userLocation.first.toDouble(),
                userLocation.second.toDouble(),
                data.latitude,
                data.longitude
            )

            if (distance <= maxDistance) {
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(data.vendorName).snippet(data.name)
                        .icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart, resources))
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                addGeofence(data.vendorName, latLng)
            } else {
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(data.vendorName).snippet(data.name)
                        .icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart, resources))
                )
            }
        }

        /*
        vendors.forEach { data ->
            val latLng = LatLng(data.latitude, data.longitude)
            mMap.addMarker(
                MarkerOptions().position(latLng).title(data.vendorName).snippet(data.name)
                    .icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart, resources))
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            addGeofence(data.id, latLng)
        }
         */
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}