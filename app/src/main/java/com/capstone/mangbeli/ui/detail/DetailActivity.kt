package com.capstone.mangbeli.ui.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.ActivityDetailBinding
import com.capstone.mangbeli.model.SendNotif
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.utils.Result.Error
import com.capstone.mangbeli.utils.Result.Loading
import com.capstone.mangbeli.utils.Result.Success
import com.capstone.mangbeli.utils.UserLocationManager
import com.capstone.mangbeli.utils.VectorToBitmap
import com.capstone.mangbeli.utils.loadImage
import com.capstone.mangbeli.utils.setVisibility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var mMap: GoogleMap
    private var name: String? = null
    private var vendorName: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var noHp: String = ""
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.detail_pedagang)
            setDisplayHomeAsUpEnabled(true)
        }

        val id = intent.getStringExtra("id") ?: ""
        noHp = intent.getStringExtra("noHp") ?: ""
        val currentLatitude = intent.getDoubleExtra("latitude", 0.0)
        val currentLongitude = intent.getDoubleExtra("longitude", 0.0)
        latitude = currentLatitude
        longitude = currentLongitude
        Log.d("Detail", "Get Id: $id")
        Log.d("DetailLatitude", "Get Lat: $currentLatitude")
        Log.d("DetailLongitude", "Get Long: $currentLongitude")


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map_detail) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        if (!isLocationSwitchEnabled()) {
            getMyLastLocation()
        } else {
            initDetail(id)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initDetail(id: String) {
        viewModel.getDetailVendor(id).observe(this) { result ->
            when (result) {
                is Loading -> {
                    setVisibility(binding.detailProgressBar, false)
                }

                is Success -> {
                    setVisibility(binding.detailProgressBar, false)
                    val response = result.data
                    with(binding) {
                        tvSellerName.text = response.name
                        tvVendorName.text = response.nameVendor ?: "Pedagang"
                        tvProducts.text = response.products?.joinToString(", ")
                        if (response.imageUrl != null) {
                            imgDetailProfile.loadImage(response.imageUrl)
                        } else {
                            imgDetailProfile.setImageResource(R.drawable.logo_mangbeli)
                        }
                        fab.setOnClickListener {
                            val sendNotif = response.userId?.let { it1 ->
                                SendNotif(
                                    it1,
                                    "MangBeli",
                                    "Kamu dapet orderan dari Blabla nih!"
                                )
                            }
                            if (sendNotif != null) {
                                viewModel.sendNotif(sendNotif).observe(this@DetailActivity) {result ->
                                    when (result) {
                                        is Loading -> {
                                            Log.d("SendNotif", "Loading: $result")
                                        }
                                        is Success -> {
                                            Log.d("SendNotif", "Success: ${result.data.message}")
                                        }
                                        is Error -> {
                                            Log.d("SendNotif", "Error: ${result.error}")

                                        }
                                    }
                                }
                            }
                            showAlert()
                        }
                        distanceDetail.text = response.distance
                        minPriceDetail.text = "Rp. ${response.minPrice.toString()}"
                        maxPriceDetail.text = "Rp. ${response.maxPrice.toString()}"
                        if (response.noHp != null) {
                            setVisibility(binding.fabWhatsapp, true)
                        } else {
                            setVisibility(binding.fabWhatsapp, false)
                        }
                        binding.fabWhatsapp.setOnClickListener {
                            intentWhatsapp()
                        }
                    }
                }

                is Error -> {
                    setVisibility(binding.detailProgressBar, false)
                    Toast.makeText(
                        this,
                        "Error : ${result.error} ",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("DetailActivity", "Error: ${result.error}")
                }
            }
        }
    }

    private fun intentWhatsapp() {
        val message = "Hello, this is a WhatsApp message."
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$noHp&text=$message")

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where WhatsApp is not installed on the device
            Toast.makeText(this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getMyLastLocation()

        with(mMap) {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = true
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isCompassEnabled = true
            isMyLocationEnabled = true
        }
        val getCurrentLocation = UserLocationManager.getCurrentLocation()
        val userLocation = LatLng(getCurrentLocation.first, getCurrentLocation.second)
        mMap.setOnMyLocationClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
        }

        val currentLocation = LatLng(latitude, longitude)
        val iconConverter = VectorToBitmap()
        mMap.addMarker(
            MarkerOptions()
                .position(currentLocation)
                .title(vendorName)
                .snippet(name)
                .icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart, resources))
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
    }

    private val requestPermissionLauncher =
        this.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    // No location access granted.

                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    saveLocationStatus(true)
                    viewModel.updateLocation(location.latitude, location.longitude)
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


    private fun showAlert() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_alert_detail))
            setMessage(getString(R.string.desc_alert_detail))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                Toast.makeText(
                    this.context,
                    getString(R.string.toast_alert_detail),
                    Toast.LENGTH_SHORT
                ).show()
            }
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(
                    this.context,
                    getString(R.string.toast_alert_detail_cancel), Toast.LENGTH_SHORT
                ).show()
            }
            create()
            show()
        }
    }

    private fun showLocationAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.enable_location_title))
            .setMessage(getString(R.string.enable_location_message))
            .setPositiveButton(getString(R.string.enable_location_positive_button)) { _, _ ->
            }
            .setNegativeButton(getString(R.string.enable_location_negative_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun saveLocationStatus(isLocationEnabled: Boolean) {
        val sharedPreferences = this.getSharedPreferences(getString(R.string.pref_key_location), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(getString(R.string.pref_key_location), isLocationEnabled)
        editor.apply()
    }
    private fun isLocationSwitchEnabled(): Boolean {
        val sharedPreferences =
            this.getSharedPreferences(
                getString(R.string.pref_key_location),
                Context.MODE_PRIVATE
            )
        val isEnabled = sharedPreferences.getBoolean(getString(R.string.pref_key_location), false)
        Log.d("MapsFragment", "isLocationSwitchEnabled: $isEnabled")
        return isEnabled
    }

}