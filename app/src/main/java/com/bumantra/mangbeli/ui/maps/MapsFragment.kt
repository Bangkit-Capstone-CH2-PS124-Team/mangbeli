package com.bumantra.mangbeli.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumantra.mangbeli.R
import com.bumantra.mangbeli.databinding.FragmentMapsBinding
import com.bumantra.mangbeli.model.VendorsData.vendors
import com.bumantra.mangbeli.utils.LocationHelper
import com.bumantra.mangbeli.utils.UserLocationManager
import com.bumantra.mangbeli.utils.VectorToBitmap
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

    private val binding get() = _binding!!

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted : Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notification permission rejected", Toast.LENGTH_SHORT).show()

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


        addManyMarker()
        addUserLocation()
        addGeofence()

    }

    @SuppressLint("MissingPermission", "VisibleForTests")
    private fun addGeofence() {
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        val geofence = Geofence.Builder()
            .setRequestId("Rumah")
            .setCircularRegion(
                currentLat.toDouble(),
                currentLog.toDouble(),
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
                        Toast.makeText(requireContext(), "Geofencing added", Toast.LENGTH_SHORT).show()
                    }
                    addOnFailureListener {
                        Toast.makeText(requireContext(), "Geofencing not added, ${it.message}", Toast.LENGTH_SHORT).show()

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
                        UserLocationManager.setCurrentLocation(lat, log)
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

    private fun addUserLocation() {
        val currentLocation = UserLocationManager.getCurrentLocation()
        val lat = currentLocation.first.toDouble()
        val log = currentLocation.second.toDouble()
        val userdummyLocation = LatLng(lat, log)
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

    private fun addManyMarker() {
        val iconConverter = VectorToBitmap()
        vendors.forEach { data ->
            val latLng = LatLng(data.latitude, data.longitude)
            mMap.addMarker(
                MarkerOptions().position(latLng).title(data.vendorName).snippet(data.name)
                    .icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart, resources))
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}