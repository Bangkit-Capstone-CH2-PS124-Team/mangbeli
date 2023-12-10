package com.capstone.mangbeli.ui.maps

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.FragmentMapsBinding
import com.capstone.mangbeli.model.VendorsData.vendors
import com.capstone.mangbeli.ui.profile.ProfileViewModelFactory
import com.capstone.mangbeli.utils.LocationHelper
import com.capstone.mangbeli.utils.VectorToBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
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
    private val mapsViewModel by viewModels<MapsViewModel> {
        ProfileViewModelFactory.getInstance(requireActivity())
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        initMap()

        return root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        with(mMap) {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = true
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isCompassEnabled = true
        }

        mapsViewModel.currentLocation.observe(viewLifecycleOwner) {
            addUserLocation(it.first, it.second)
            addManyMarker(it)
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
            }
        )
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
    }


    private fun getUserLocation() {
        LocationHelper.getLastKnownLocation(
            fusedLocationClient,
            { location ->
                mapsViewModel.updateCurrentLocation(location.latitude, location.longitude)
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

    @SuppressLint("MissingPermission")
    private fun addUserLocation(lat: Double, log: Double) {
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
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userdummyLocation, 18f))
        }
    }

    private fun addManyMarker(userLocation: Pair<Double, Double>) {
        val iconConverter = VectorToBitmap()
        val maxDistance = 1000.0

        vendors.forEach { data ->
            val latLng = LatLng(data.latitude, data.longitude)
            val distance = calculateDistance(
                userLocation.first,
                userLocation.second,
                data.latitude,
                data.longitude
            )

            if (distance <= maxDistance) {
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(data.vendorName).snippet(data.name)
                        .icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart, resources))
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            } else {
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(data.vendorName).snippet(data.name)
                        .icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart, resources))
                )
            }
        }
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