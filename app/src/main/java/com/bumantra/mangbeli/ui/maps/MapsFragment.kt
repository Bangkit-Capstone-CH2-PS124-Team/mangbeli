package com.bumantra.mangbeli.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumantra.mangbeli.R
import com.bumantra.mangbeli.databinding.FragmentMapsBinding
import com.bumantra.mangbeli.utils.VectorToBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.bumantra.mangbeli.model.VendorsData.vendors
import com.google.android.gms.maps.model.LatLngBounds


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val boundsBuilder = LatLngBounds.Builder()

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

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        addManyMarker()
    }

    private fun addManyMarker() {
        val iconConverter = VectorToBitmap()
        vendors.forEach { data ->
            val latLng = LatLng(data.latitude, data.longitude)
            mMap.addMarker(
                MarkerOptions().position(latLng).title(data.vendorName).snippet(data.name).icon(iconConverter.vectorToBitmap(R.drawable.ic_food_cart,  resources))
            )
            boundsBuilder.include(latLng)
        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}