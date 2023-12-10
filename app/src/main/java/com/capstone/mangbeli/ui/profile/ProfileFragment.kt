package com.capstone.mangbeli.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.FragmentProfileBinding
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.utils.LocationHelper
import com.capstone.mangbeli.utils.Result.Error
import com.capstone.mangbeli.utils.Result.Loading
import com.capstone.mangbeli.utils.Result.Success
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
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        setVisibility(binding.googleMapProfile, false)

        initMap()
        initProfile()
        onSubmit()

        return root
    }

    private fun onSubmit() {
        binding.btnSave.setOnClickListener {
            val inputString = binding.edtFavorite.text
            val listFavorite = inputString?.split(", ")?.map { it.trim() }
            val updateUser = UserProfile(
                name = binding.edtName.text.toString(),
                no_hp = binding.edtNoHp.text.toString(),
                favorite = listFavorite
            )
            profileViewModel.updateUserProfile(updateUser).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Loading -> {
                        setVisibility(binding.profileProgressBar, true)
                        binding.profileProgressBar.visibility = View.VISIBLE
                        Log.d("ProfileFragment", "initProfile: Loading")
                    }

                    is Success -> {
                        setVisibility(binding.profileProgressBar, false)
                        val userData = result.data.message
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle("Update Profile berhasil")
                            setMessage("$userData")
                            setPositiveButton("Lanjut") { _, _ ->
                                initProfile()
                            }
                            create()
                            show()
                        }

                    }

                    is Error -> {
                        setVisibility(binding.profileProgressBar, false)
                        Toast.makeText(
                            requireContext(),
                            "Error ${result.error} : Cek internet anda!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ProfileFragment", "onCreate: ${result.error}")
                    }
                }
            }
        }
    }

    private fun onLocationEnable(latitude: Double, longitude: Double) {
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // User menyalakan lokasi
                setVisibility(binding.googleMapProfile, true)
                updateLocation(latitude, longitude)
                updateMapLocation(latitude, longitude)
            } else {
                // User mematikan lokasi
                setVisibility(binding.googleMapProfile, false)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        profileViewModel.currentLocation.observe(viewLifecycleOwner) {
            onLocationEnable(it.first, it.second)
        }


    }

    private fun updateMapLocation(latitude: Double, longitude: Double) {
        val currentLocation = LatLng(latitude, longitude)
        mMap.addMarker(
            MarkerOptions().position(currentLocation).title(binding.tvNameUser.text.toString())
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
    }


    private fun initMap() {
        // Initialize map
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map_profile) as SupportMapFragment
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

    private fun initProfile() {
        profileViewModel.getUserProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> {
                    setVisibility(binding.profileProgressBar, true)
                    binding.profileProgressBar.visibility = View.VISIBLE
                    Log.d("ProfileFragment", "initProfile: Loading")
                }

                is Success -> {
                    setVisibility(binding.profileProgressBar, false)
                    val userData = result.data
                    val listFavorite = userData?.favorite?.joinToString(", ")
                    if (userData != null) {
                        Log.d("ProfileFragment", "Get Data: $userData")
                        with(binding) {
                            tvNameUser.text = userData.name
                            tvEmailUser.text = userData.email
                            tvFavoriteUser.text = listFavorite
                            edtName.setText(userData.name)
                            edtNoHp.setText(userData.noHp)
                            edtFavorite.setText(listFavorite)
                        }
                    }

                }

                is Error -> {
                    setVisibility(binding.profileProgressBar, false)
                    Toast.makeText(
                        requireContext(),
                        "Error ${result.error} : Cek internet anda!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("ProfileFragment", "onCreate: ${result.error}")
                }
            }
        }
    }

    private fun getUserLocation() {
        LocationHelper.getLastKnownLocation(
            fusedLocationClient,
            { location ->
                profileViewModel.updateCurrentLocation(location.latitude, location.longitude)
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

    private fun updateLocation(latitude: Double, longitude: Double) {
        profileViewModel.updateLocation(latitude, longitude)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Loading -> {
                        setVisibility(binding.profileProgressBar, true)
                    }

                    is Success -> {
                        setVisibility(binding.profileProgressBar, false)
                        val response = result.data.message
                        Log.d("ProfileLocation", "location: $response")
                        Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show()
                    }

                    is Error -> {
                        setVisibility(binding.profileProgressBar, false)
                        Log.d("LocationError", "startUpload: ${result.error}")
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }

            }
    }

    private fun onPermissionDenied() {
        Toast.makeText(
            requireContext(), "Location permission denied", Toast.LENGTH_SHORT
        ).show()
    }

    private fun setVisibility(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}