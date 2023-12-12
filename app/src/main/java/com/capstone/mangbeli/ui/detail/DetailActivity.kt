package com.capstone.mangbeli.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.ActivityDetailBinding
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.utils.Result.Error
import com.capstone.mangbeli.utils.Result.Loading
import com.capstone.mangbeli.utils.Result.Success
import com.capstone.mangbeli.utils.VectorToBitmap
import com.capstone.mangbeli.utils.setVisibility
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.detail_pedagang)
            setDisplayHomeAsUpEnabled(true)
        }

        val id = intent.getStringExtra("id") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val photoUrl = intent.getStringExtra("photoUrl") ?: ""
        noHp = intent.getStringExtra("noHp") ?: ""
        val currentLatitude = intent.getStringExtra("latitude")?.toDouble() ?: 0.0
        val currentLongitude = intent.getStringExtra("longitude")?.toDouble() ?: 0.0
        latitude = currentLatitude
        longitude = currentLongitude
        Log.d("Detail", "Get Id: $id")


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map_detail) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Dummy implementation, it should replace when repository or data is done
        initDetail(id, name, photoUrl)


    }

    private fun initDetail(id: String, name: String, photoUrl: String) {
        viewModel.getDetailVendor(id).observe(this) { result ->
            when (result) {
                is Loading -> {
                    setVisibility(binding.detailProgressBar, false)

                }

                is Success -> {
                    setVisibility(binding.detailProgressBar, false)
                    val response = result.data
                    with(binding) {
                        tvSellerName.text = name
                        tvVendorName.text = response.nameVendor ?: "Pedagang"
                        tvProducts.text = response.products?.joinToString(", ")
                        if (photoUrl.isEmpty()) {
                            imgDetailProfile.setImageResource(R.drawable.gobaklogo)
                        } else {
                            imgDetailProfile.loadImage(photoUrl)
                        }
                        fab.setOnClickListener {
                            showAlert()
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true

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

    private fun ImageView.loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .into(this)
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
}