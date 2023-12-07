package com.capstone.mangbeli.ui.detail

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.ActivityDetailBinding
import com.capstone.mangbeli.utils.VectorToBitmap
import com.bumptech.glide.Glide
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.detail_pedagang)
            setDisplayHomeAsUpEnabled(true)
        }

        // val id = intent.getStringExtra("id") ?: ""

        // Dummy implementation, it should replace when repository or data is done
        name = intent.getStringExtra("name")
        vendorName = intent.getStringExtra("vendorName")
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        val photoUrl = intent.getStringExtra("photoUrl") ?: ""
        val products = intent.getStringArrayListExtra("products")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map_detail) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Dummy implementation, it should replace when repository or data is done
        binding.tvSellerName.text = name
        binding.tvVendorName.text = vendorName
        binding.tvProducts.text = products?.joinToString(", ")
        binding.imgDetailProfile.loadImage(photoUrl)
        binding.fab.setOnClickListener {
            showAlert()
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
                .icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart,  resources))
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