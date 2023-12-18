package com.capstone.mangbeli.ui.maps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.FragmentMapsBinding
import com.capstone.mangbeli.ui.MenuActivity
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.utils.LocationHelper
import com.capstone.mangbeli.utils.Result
import com.capstone.mangbeli.utils.VectorToBitmap
import com.capstone.mangbeli.utils.loadImage
import com.capstone.mangbeli.utils.setVisibility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val geofenceRadius = 100.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>


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
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        hiddenBottomSheet()

        return root
    }

    private fun hiddenBottomSheet() {
        bottomSheetBehavior.apply {
            Log.d("ApplyHiddenBottomSheet", "hiddenBottomSheet: ${this.state}")
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                peekHeight = 0
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Log.d("HiddenBottomSheet", "hiddenBottomSheet: ${this.state}")
            }


        }
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
        if (!isLocationSwitchEnabled()) {
            Log.d("MapsFragment", "onMapReady: Location switch is disabled, showing AlertDialog")
            showLocationSwitchAlertDialog()
        }
        getUserLocation()
    }


    private fun getUserLocation() {
        setVisibility(binding.mapProgressBar, true)
        LocationHelper.getLastKnownLocation(
            fusedLocationClient,
            { location ->
                mapsViewModel.updateCurrentLocation(location.latitude, location.longitude)
                setVisibility(binding.mapProgressBar, false)
            },
            {
                setVisibility(binding.mapProgressBar, false)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_not_available),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun addUserLocation(lat: Double, log: Double) {
        val userLocation = LatLng(lat, log)
        mMap.addCircle(
            CircleOptions()
                .center(userLocation)
                .radius(geofenceRadius)
                .fillColor(0x2200FF00)
                .strokeColor(Color.CYAN)
                .strokeWidth(3f)
        )
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addManyMarker(userLocation: Pair<Double, Double>) {
        val iconConverter = VectorToBitmap()
        val maxDistance = 1000.0
//        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(requireContext()))
        mMap.setOnMapClickListener {
            hiddenBottomSheet()
        }
        mapsViewModel.getMapsVendors().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    setVisibility(binding.mapProgressBar, true)
                }

                is Result.Success -> {
                    setVisibility(binding.mapProgressBar, false)
                    result.data.forEach { data ->

                        val latLng = LatLng(data.latitude, data.longitude)
                        val distance = calculateDistance(
                            userLocation.first,
                            userLocation.second,
                            data.latitude,
                            data.longitude
                        )

                        if (distance <= maxDistance) {
                            mMap.addMarker(
                                MarkerOptions().position(latLng).title(data.name)
                                    .snippet(data.distance)
                                    .icon(
                                        iconConverter.vectorToBitmap(
                                            R.drawable.ic_food_cart,
                                            resources
                                        )
                                    )
                            )?.tag = data.vendorId
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                        } else {
                            mMap.addMarker(
                                MarkerOptions().position(latLng).title(data.name)
                                    .snippet(data.distance)
                                    .icon(
                                        iconConverter.vectorToBitmap(
                                            R.drawable.ic_food_cart,
                                            resources
                                        )
                                    )
                            )?.tag = data.vendorId
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                        }
                        mMap.setOnMarkerClickListener {
                            val id = it.tag as? String
                            if (id != null) {
                                mapsViewModel.getDetailVendor(id)
                                    .observe(viewLifecycleOwner) { result ->
                                        when (result) {
                                            is Result.Loading -> {
                                                setVisibility(binding.mapProgressBar, true)
                                            }

                                            is Result.Success -> {
                                                val bottomSheetData = result.data
                                                setVisibility(binding.mapProgressBar, false)
                                                with(binding) {
                                                    titleMapsContent.text =
                                                        bottomSheetData.nameVendor
                                                    nameMapsContent.text = it.title
                                                    descMapsContent.text =
                                                        bottomSheetData.products?.joinToString(", ")
                                                    distanceMapsContent.text = it.snippet
                                                    minPriceMapsContent.text = "Rp. ${bottomSheetData.minPrice}"
                                                    maxPriceMapsContent.text = "Rp. ${bottomSheetData.maxPrice}"
                                                    if (data.imageUrl != null) {
                                                        imageMapsContent.loadImage(data.imageUrl)
                                                    }
                                                    if (bottomSheetData.noHp != null) {
                                                        setVisibility(binding.actionCall, true)
                                                        binding.actionCall.setOnClickListener {
                                                            intentWhatsapp(bottomSheetData.noHp)
                                                        }
                                                    } else {
                                                        setVisibility(binding.actionCall, false)
                                                    }
                                                }
                                            }

                                            is Result.Error -> {
                                                if (result.error == "Missing access token") {
                                                    startActivity(Intent(requireContext(), MenuActivity::class.java))
                                                    requireActivity().finish()
                                                }
                                                if (result.error == "Invalid access token") {
                                                    startActivity(Intent(requireContext(), MenuActivity::class.java))
                                                    requireActivity().finish()
                                                }
                                                Log.d("Check", "addManyMarker: ${result.error}")
                                                Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    }
                            }
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            false
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

    private fun intentWhatsapp(noHp: String) {
        val message = "Hello, this is a WhatsApp message."
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$noHp&text=$message")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where WhatsApp is not installed on the device
            Toast.makeText(requireContext(), "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLocationSwitchAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.enable_location_title))
            .setMessage(getString(R.string.enable_location_message))
            .setPositiveButton(getString(R.string.enable_location_positive_button)) { _, _ ->
                // Buka ProfileFragment untuk mengaktifkan switch location
                findNavController().navigate(R.id.navigation_profile)
            }
            .setNegativeButton(getString(R.string.enable_location_negative_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun isLocationSwitchEnabled(): Boolean {
        val sharedPreferences =
            requireContext().getSharedPreferences(
                getString(R.string.pref_key_location),
                Context.MODE_PRIVATE
            )
        val isEnabled = sharedPreferences.getBoolean(getString(R.string.pref_key_location), false)
        Log.d("MapsFragment", "isLocationSwitchEnabled: $isEnabled")
        return isEnabled
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