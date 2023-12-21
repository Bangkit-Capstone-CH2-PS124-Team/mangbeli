package com.capstone.mangbeli.ui.pedagang.homepedagang

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.FragmentHomePedagangBinding
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.maps.GeofenceBroadcastReceiver
import com.capstone.mangbeli.ui.maps.MapsViewModel
import com.capstone.mangbeli.ui.profile.ProfileViewModelFactory
import com.capstone.mangbeli.utils.LocationHelper
import com.capstone.mangbeli.utils.Result
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class HomePedagangFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomePedagangBinding? = null
    private val geofenceRadius = 100.0
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap1: GoogleMap
    private lateinit var googleMap2: GoogleMap
    private val markers: MutableList<Marker> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel by viewModels<HomeVendorViewModel> {
        ProfileViewModelFactory.getInstance(requireActivity())
    }
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePedagangBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireContext().getSharedPreferences("MarkerPrefs", Context.MODE_PRIVATE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (Build.VERSION.SDK_INT >= 33) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        initMap()
        loadMarkersFromPrefs()
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
                       Log.e("GeofenceMaps", "addGeofence: ${it.message}")

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
                val latLng = LatLng(it.first, it.second)
                googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                addUsersMarker(it)
            }
        } else if (!::googleMap2.isInitialized) {
            googleMap2 = googleMap
            binding.btnMark.setOnClickListener {
                addMarker() // Tambahkan marker saat tombol ditekan
            }

            viewModel.currentLocation.observe(viewLifecycleOwner) {
                val latLng = LatLng(it.first, it.second)
                googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            }
        }


    }

    @SuppressLint("MissingPermission")
    private fun addMarker() {
            // Mendapatkan lokasi saat ini
            getUserLocation()
            // Menambahkan marker saat mendapatkan lokasi saat ini
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    val marker = googleMap2.addMarker(MarkerOptions().position(latLng))
                    marker?.let {
                        markers.add(it)
                    }
                }
            }.addOnFailureListener { exception ->
                // Handle failure to get location
                Log.e("Location", "Error getting location: ${exception.message}")
            }
        }

    private fun saveMarkerToPrefs(marker: Marker) {
        // Simpan informasi marker ke dalam SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("marker_${markers.size}", "${marker.position.latitude},${marker.position.longitude}")
        editor.apply()
    }
    private fun loadMarkersFromPrefs() {
        // Mendapatkan jumlah marker yang tersimpan
        val markerCount = markers.size // Ganti dengan metode untuk mendapatkan jumlah marker yang tersimpan sebelumnya

        // Muat informasi marker dari SharedPreferences
        for (i in 0 until markerCount) {
            val markerInfo = sharedPreferences.getString("marker_$i", null)
            markerInfo?.let {
                val position = it.split(",")
                val lat = position[0].toDouble()
                val lng = position[1].toDouble()
                val latLng = LatLng(lat, lng)
                val marker = googleMap2.addMarker(MarkerOptions().position(latLng))
                marker?.let { it1 -> markers.add(it1) }
            }
        }
    }
    private fun clearMarkers() {
        for (marker in markers) {
            marker.remove() // Hapus semua marker dari peta
        }
        markers.clear() // Bersihkan list marker
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

    private fun addUsersMarker(myLocation: Pair<Double, Double>) {
        val iconConverter = VectorToBitmap()
        val maxDistance = 1000.0
        mapsViewModel.getMapsUsers().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Log.d("AddMarker", "addManyMarker: Loading")
                }

                is Result.Success -> {
                    result.data.forEach { data ->

                        val latLng = LatLng(data.latitude, data.longitude)
                        val distance = calculateDistance(
                            myLocation.first,
                            myLocation.second,
                            data.latitude,
                            data.longitude
                        )
                        val listFavorite = data.favorite?.joinToString(separator = ", ")
                        if (distance <= maxDistance) {
                            googleMap1.addMarker(
                                MarkerOptions().position(latLng).title(data.name)
                                    .snippet(listFavorite).icon(
                                    iconConverter.vectorToBitmap(
                                        R.drawable.ic_detail_user,
                                        resources
                                    )
                                )
                            )
                            addGeofence(data.name.toString(), latLng)
                        } else {
                            googleMap1.addMarker(
                                MarkerOptions().position(latLng).title(data.name)
                                    .snippet(listFavorite).icon(
                                        iconConverter.vectorToBitmap(
                                            R.drawable.ic_detail_user,
                                            resources
                                        )
                                    )
                            )
                        }
                    }
                }

                is Result.Error -> {
                    Log.d("AddMarker", "addManyMarker: ${result.error}")
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun addMyLocation(lat: Double, log: Double) {
        if (!::googleMap2.isInitialized) {
            return
        }
        val myLocation = LatLng(lat, log)
        googleMap2.isMyLocationEnabled = true
        googleMap2.setOnMyLocationClickListener {
            googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f))
        }
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