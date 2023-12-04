package com.bumantra.mangbeli.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumantra.mangbeli.R
import com.bumantra.mangbeli.databinding.FragmentProfileBinding
import com.bumantra.mangbeli.ui.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ProfileFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var currentLat: Float? = null
    private var currentLog: Float? = null
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvNameUser
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.google_map_profile) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getUserLocation()

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        profileViewModel.currentLocation.observe(viewLifecycleOwner) {
            updateMapLocation(it.first, it.second)
        }

    }

    private fun updateMapLocation(latitude: Float, longitude: Float) {
        val currentLocation = LatLng(latitude.toDouble(), longitude.toDouble())
        mMap.addMarker(
            MarkerOptions()
                .position(currentLocation)
                .title(binding.tvNameUser.text.toString())
        )
        Log.d("Profile", "onMapReady: $currentLat, $currentLog")
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getUserLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getUserLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getUserLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLat = location.latitude.toFloat()
                    currentLog = location.longitude.toFloat()
                    Log.d("Profile Fuse", "getUserLocation: $currentLat, $currentLog")
                    currentLat?.let { lat ->
                        currentLog?.let { log ->
                            profileViewModel.updateCurrentLocation(lat, log)
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location tidak ada, yuk dicoba lagi",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}