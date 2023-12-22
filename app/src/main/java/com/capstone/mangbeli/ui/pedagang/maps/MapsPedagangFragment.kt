package com.capstone.mangbeli.ui.pedagang.maps

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
import com.capstone.mangbeli.databinding.FragmentMapsPedagangBinding
import com.capstone.mangbeli.ui.MenuActivity
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.maps.MapsViewModel
import com.capstone.mangbeli.utils.LocationHelper
import com.capstone.mangbeli.utils.Result
import com.capstone.mangbeli.utils.VectorToBitmap
import com.capstone.mangbeli.utils.isNetworkAvailable
import com.capstone.mangbeli.utils.loadImage
import com.capstone.mangbeli.utils.setVisibility
import com.codebyashish.googledirectionapi.AbstractRouting
import com.codebyashish.googledirectionapi.ErrorHandling
import com.codebyashish.googledirectionapi.RouteDrawing
import com.codebyashish.googledirectionapi.RouteInfoModel
import com.codebyashish.googledirectionapi.RouteListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar

class MapsPedagangFragment : Fragment(), OnMapReadyCallback, RouteListener{
    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsPedagangBinding? = null
    private val geofenceRadius = 100.0
    private var userLocation: LatLng = LatLng(0.0, 0.0)
    private var vendorLocation: LatLng = LatLng(0.0, 0.0)
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var previousPolylines: ArrayList<Polyline> = ArrayList()
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
        _binding = FragmentMapsPedagangBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        initMap()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        hiddenBottomSheet()
        binding.btnRoute.setOnClickListener {
            findRoute(userLocation, vendorLocation)
        }

        return root
    }
    private fun findRoute(start: LatLng?, end: LatLng?) {
        previousPolylines.forEach { it.remove() }
        previousPolylines.clear()
        if (start == null || end == null) {
            view?.let { rootView ->
                Snackbar.make(rootView.findViewById(android.R.id.content), "Unable to get location", Snackbar.LENGTH_LONG).show()
            }
        } else {
            val routing = RouteDrawing.Builder()
                .key(getString(R.string.api_key))
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this as RouteListener)
                .alternativeRoutes(true)
                .waypoints(start, end)
                .build()
            routing.execute()
        }
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
            val latLng = LatLng(it.first, it.second)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
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
                userLocation = LatLng(location.latitude, location.longitude)
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
        if (!isNetworkAvailable(requireContext())) {
            Toast.makeText(
                requireContext(),
                "Internet connection is required",
                Toast.LENGTH_LONG
            )
                .show()
        }
        else {
            mapsViewModel.getMapsUsers().observe(viewLifecycleOwner) { result ->
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
                                                R.drawable.ic_detail_user,
                                                resources
                                            )
                                        )
                                )?.tag = data.userId
                            } else {
                                mMap.addMarker(
                                    MarkerOptions().position(latLng).title(data.name)
                                        .snippet(data.distance)
                                        .icon(
                                            iconConverter.vectorToBitmap(
                                                R.drawable.ic_detail_user,
                                                resources
                                            )
                                        )
                                )?.tag = data.userId
                            }
                            mMap.setOnMarkerClickListener {
                                val id = it.tag as? String
                                if (id != null) {
                                    mapsViewModel.getDetailUser(id)
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
                                                            bottomSheetData.name
                                                        nameMapsContent.text = it.title
                                                        descMapsContent.text =
                                                            bottomSheetData.favorite?.joinToString(", ")
                                                        distanceMapsContent.text = it.snippet
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
                                                    latitude = bottomSheetData.latitude!!
                                                    longitude = bottomSheetData.longitude!!
                                                    vendorLocation = LatLng(latitude, longitude)
                                                    Log.d("Checkayam", "addManyMarker: $vendorLocation")
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
                findNavController().navigate(R.id.navigation_profile_pedagang)
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
    override fun onRouteFailure(e: ErrorHandling?) {
        Log.d("Route", "onRoutingFailure: $e")
    }

    override fun onRouteStart() {
        Log.d("Route", "Started Route")
    }

    override fun onRouteSuccess(list: ArrayList<RouteInfoModel>, indexing: Int) {
        val polylineOptions = PolylineOptions()
        val polylines = ArrayList<Polyline>()

        for (i in 0 until list.size) {
            if (i == indexing) {
                Log.e("TAG", "onRoutingSuccess: routeIndexing $indexing")
                polylineOptions.color(Color.GREEN)
                polylineOptions.width(12f)
                polylineOptions.addAll(list[indexing].points)
                polylineOptions.startCap(RoundCap())
                polylineOptions.endCap(RoundCap())
                val durationText = list[indexing].durationText
                binding.tvArrivalTimesMaps.text = durationText
                setVisibility(binding.linearLayoutArrivalTimeMaps, true)
                val polyline: Polyline = mMap.addPolyline(polylineOptions)
                polylines.add(polyline)
            }
        }
        previousPolylines.forEach { it.remove() }
        previousPolylines.clear()

        previousPolylines.addAll(polylines)
    }
    override fun onRouteCancelled() {
        Log.d("Route", "Cancel Route")
    }
}