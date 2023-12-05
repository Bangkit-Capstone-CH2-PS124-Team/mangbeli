package com.bumantra.mangbeli.ui.maps

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumantra.mangbeli.R
import com.bumantra.mangbeli.databinding.FragmentMapsBinding
import com.bumantra.mangbeli.model.VendorsData.vendors
import com.bumantra.mangbeli.utils.LocationHelper
import com.bumantra.mangbeli.utils.UserLocationManager
import com.bumantra.mangbeli.utils.VectorToBitmap
import com.google.android.gms.location.FusedLocationProviderClient
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val binding get() = _binding!!

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
    }

    private fun getUserLocation() {
        LocationHelper.getLastKnownLocation(
            fusedLocationClient,
            { location ->
                val currentLat = location.latitude.toFloat()
                val currentLog = location.longitude.toFloat()

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
                .fillColor(0x22FF0000)
                .strokeColor(Color.RED)
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