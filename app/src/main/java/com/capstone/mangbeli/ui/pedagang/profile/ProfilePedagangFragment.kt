package com.capstone.mangbeli.ui.pedagang.profile

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.FragmentProfilePedagangBinding
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.model.VendorProfile
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.home.TokenViewModel
import com.capstone.mangbeli.ui.home.TokenViewModelFactory
import com.capstone.mangbeli.utils.ButtonUtils
import com.capstone.mangbeli.utils.EditTextUtils
import com.capstone.mangbeli.utils.LocationHelper
import com.capstone.mangbeli.utils.Result
import com.capstone.mangbeli.utils.loadImage
import com.capstone.mangbeli.utils.reduceFileImage
import com.capstone.mangbeli.utils.setVisibility
import com.capstone.mangbeli.utils.uriToFile
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ProfilePedagangFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentProfilePedagangBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var originalData: VendorProfile? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileVendorViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val tokenViewModel by viewModels<TokenViewModel> {
        TokenViewModelFactory.getInstance(requireActivity())
    }
    private var currentImageUri: Uri? = null
    private var isEdited = false
    private var isTracking = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilePedagangBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        setVisibility(binding.googleMapProfile, false)
        createLocationRequest()
        createLocationCallback()
        initMap()
        initProfileUser()
        initProfile()
        setSlider()
        onImageClicked()
        onSubmit()
        viewModel.getToken()

        viewModel.userResponse.observe(viewLifecycleOwner) { user ->
            ViewModelFactory.refreshInstance()
            TokenViewModelFactory.refreshInstance()
            val expirationDateString = user.expired.toString()
            val expiredAkesToken = user.expiredToken.toString()
            if (expiredAkesToken != " "){
                reefreshToken(expiredAkesToken)}
            else{
                Log.e("SplashScreen", "onCreate: $expiredAkesToken")
            }
            if (expirationDateString == " ") {
                Log.e("SplashScreen", "onCreate: $expirationDateString")
            } else {
                checkTokenExpiration(expirationDateString)
            }
            Log.e("SplashScreen", "onCreate: ${user}")
        }
        val isLocationEnabled = getStatusFromSharedPreferences()
        binding.switchLocation.isChecked = isLocationEnabled
        onChangeEditText()
        return root
    }
    private fun getStatusFromSharedPreferences(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences(getString(R.string.pref_key_location), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(getString(R.string.pref_key_location), false)
    }
    private fun onImageClicked() {
        binding.imgProfile.setOnClickListener { startGallery() }
    }
    private fun initProfileUser() {
        viewModel.getUserProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    setVisibility(binding.profileProgressBar, true)
                    binding.profileProgressBar.visibility = View.VISIBLE
                    Log.d("ProfileFragment", "initProfile: Loading")
                }

                is Result.Success -> {
                    setVisibility(binding.profileProgressBar, false)
                    ViewModelFactory.refreshInstance()
                    val userData = result.data
                    Log.d("ProfileFragment", "Get Data: $userData")
                    with(binding) {
                        Log.d("Profile", "initProfile: ${imgProfile.drawable}")
                        if (imgProfile.drawable == null) {
                            imgProfile.setImageResource(R.drawable.ic_account_circle_24)
                        } else {
                            imgProfile.loadImage(userData.imageUrl.toString())
                        }
                        edtName.setText(userData.name)
                        edtNoHp.setText(userData.noHp)
                    }


                }

                is Result.Error -> {
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

    private fun initProfile() {
        viewModel.getVendorProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    setVisibility(binding.profileProgressBar, true)
                    Log.d("sapi", "Cek Ombak: $result")
                }

                is Result.Success -> {
                    setVisibility(binding.profileProgressBar, false)
                    ViewModelFactory.refreshInstance()
                    val vendors = result.data
                    vendors?.let {
                        binding.edtNameVendor.setText(it.nameVendor)
                        binding.edtListProduct.setText(it.products?.joinToString(", "))
                        binding.minimumSlider.value = it.minPrice?.toFloat() ?: 500f
                        binding.maksimumSlider.value = it.maxPrice?.toFloat() ?: 500f
                        Log.d("kucing", "Cek Ombak: $it")
                    }
                    Log.d("sapi", "Cek Ombak: $vendors")
                }

                is Result.Error -> {
                    setVisibility(binding.profileProgressBar, false)
                    Log.d("sapi", "onCreateView: ${result.error}")
                }
            }
        }
    }

    private fun saveLocationStatus(isLocationEnabled: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences(getString(R.string.pref_key_location), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(getString(R.string.pref_key_location), isLocationEnabled)
        editor.apply()
    }
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
            onUploadImage()
        } else {
            Log.d("AddStory", "Tidak ada gambar yang dipilih")

        }
    }

    private fun onUploadImage() {
        currentImageUri?.let { uri ->
            val image = uriToFile(uri, requireContext()).reduceFileImage()
            val requestImg = image.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                image.name,
                requestImg
            )
            Log.d("CekImageUpload", "onUploadImage: $multipartBody")
            viewModel.uploadImage(multipartBody).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        Log.d("ProfileFragment", "uploadImage: Loading")
                        setVisibility(binding.profileProgressBar, true)
                    }

                    is Result.Success -> {
                        setVisibility(binding.profileProgressBar, false)
                        val userData = result.data.message
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle("Update Image Profile berhasil")
                            setMessage(userData)
                            setPositiveButton("Lanjut") { _, _ ->
                                initProfile()
                            }
                            create()
                            show()
                        }

                    }


                    is Result.Error -> {
                        setVisibility(binding.profileProgressBar, false)
                        Toast.makeText(
                            requireContext(),
                            "Error ${result.error} : Cek internet anda!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ProfileFragment", "uploadImage: ${result.error}")
                    }
                }
            }
        }

    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image", "showImage: $it")
            binding.imgProfile.setImageURI(it)
        }
    }


    private fun onLocationEnable(latitude: Double, longitude: Double) {
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            Log.d("CekSwitch", "onLocationEnable: $isChecked")
            saveLocationStatus(isChecked)
            if (isChecked) {
                // User menyalakan lokasi
                setVisibility(binding.cardView, true)
                setVisibility(binding.googleMapProfile, true)
                updateLocation(latitude, longitude)
                startLocationUpdates()
                updateMapLocation(latitude, longitude)
            } else {
                // User mematikan lokasi
                setVisibility(binding.cardView, false)
                setVisibility(binding.googleMapProfile, false)
                deleteLocation()

            }
        }
    }

    private fun onSubmit() {
        originalData = VendorProfile(
            nameVendor = binding.edtNameVendor.text.toString(),
            products = binding.edtListProduct.text?.split(", ")?.map { it.trim() },
            minPrice = binding.minimumSlider.value.toInt(),
            maxPrice = binding.maksimumSlider.value.toInt()
        )
        binding.btnSave.setOnClickListener {
            val name = binding.edtName.text.toString()
            val vendorName = binding.edtNameVendor.text.toString()
            val noHp = binding.edtNoHp.text.toString()
            val inputString = binding.edtListProduct.text
            val products = inputString?.split(", ")?.map { it.trim() }
            val updateVendor = VendorProfile(
                nameVendor = vendorName,
                products = products,
                minPrice = binding.minimumSlider.value.toInt(),
                maxPrice = binding.maksimumSlider.value.toInt()
            )
            val updateUser = UserProfile(
                name = name,
                noHp = noHp
            )
            viewModel.updateUserProfile(updateUser)
                .observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            Log.d("ProfileFragment", "initProfile: Loading")
                        }

                        is Result.Success -> {
                            Log.d("ProfileFragment", "initProfile: Success")
                        }

                        is Result.Error -> {

                            Log.d("DataDiriActivity", "onCreate: ${result.error}")
                        }
                    }
                }

            viewModel.updateVendorProfile(updateVendor)
                .observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            Log.d("ProfileFragment", "initProfile: Loading")
                        }

                        is Result.Success -> {
                            val userData = result.data.message
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle("Update Profile berhasil")
                                setMessage("$userData")
                                setPositiveButton("Lanjut") { _, _ ->
                                    initProfileUser()
                                    initProfile()
                                }
                                create()
                                show()
                            }
                        }

                        is Result.Error -> {
                            // Tampilkan pesan kesalahan kepada pengguna

                            Log.d("DataDiriActivity", "onCreate: ${result.error}")
                        }
                    }
                }
            val isDataChanged = originalData != updateVendor

            binding.btnSave.isEnabled = isDataChanged

        }
    }
    fun reefreshToken(expirationDate: String) {
        ViewModelFactory.refreshInstance()
        TokenViewModelFactory.refreshInstance()
        if (expirationDate != " ") {
            val expirationDateFormat =
                SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH)
            val expirationDateParsed = expirationDateFormat.parse(expirationDate)
            val calendar = Calendar.getInstance()
            calendar.time = expirationDateParsed as Date

            val expirationDatePlus7Hours = calendar.time
            val currentTime = Calendar.getInstance().time

            if (currentTime.after(expirationDatePlus7Hours)) {
                tokenViewModel.reefreshToken()
                Log.d("coba", "cek akses token: $currentTime, $expirationDatePlus7Hours")
            } else {
                Log.d("coba", "cek akses token: $currentTime, $expirationDatePlus7Hours")
            }
        } else {
            Log.d("coba", "cek akses token: $expirationDate")
        }
    }

    fun logoutRefreshToken() {
        ViewModelFactory.refreshInstance()
        TokenViewModelFactory.refreshInstance()
        tokenViewModel.logoutRefreshToken()
    }
    fun checkTokenExpiration(expirationDate: String) {
        if (expirationDate != " ") {
            val expirationDateFormat =
                SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
            val expirationDateParsed = expirationDateFormat.parse(expirationDate)
            // Ganti dengan tanggal kadaluarsa yang benar
            val calendar = Calendar.getInstance()
            calendar.time = expirationDateParsed as Date

            // Tambahkan 7 jam ke tanggal kedaluwarsa
            calendar.add(Calendar.HOUR_OF_DAY, -7)

            val expirationDatePlus7Hours = calendar.time
            val currentTime = Calendar.getInstance().time

            if (currentTime.after(expirationDatePlus7Hours)) {
                Log.d("coba", "cek refreshToken: $currentTime, $expirationDatePlus7Hours")
                logoutRefreshToken()  // Panggil fungsi logout pada viewModel
            } else {
                Log.d("coba", "cek refreshToken: $currentTime, $expirationDatePlus7Hours")
            }
        } else {
            Log.d("coba", "cek refreshToken: $expirationDate")
        }
    }
    private fun setSlider() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        binding.minimumSlider.valueFrom = 1000f
        binding.minimumSlider.valueTo = 100000f
        binding.minimumSlider.stepSize = 500f
        binding.minimumSlider.value = 1000f

        binding.maksimumSlider.valueFrom = 1000f
        binding.maksimumSlider.valueTo = 100000f
        binding.maksimumSlider.stepSize = 500f
        binding.maksimumSlider.value = 5000f

        binding.minimumSlider.addOnChangeListener { slider, value, fromUser ->
//            if (value > binding.maksimumSlider.value) {
//                binding.minimumSlider.value = binding.maksimumSlider.value
//            } else {
                val formattedValue = currencyFormat.format(value.toDouble())
                binding.minimumTextView.text = getString(R.string.minimum_sapi) + formattedValue
//            }
        }

        binding.maksimumSlider.addOnChangeListener { slider, value, fromUser ->
            if (value < binding.minimumSlider.value) {
                binding.maksimumSlider.value = binding.minimumSlider.value
            } else {
                val formattedValue = currencyFormat.format(value.toDouble())
                binding.maksimumTextView.text = getString(R.string.maksimum) + formattedValue
            }
        }
    }
    private fun deleteLocation() {
        viewModel.deleteLocation().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Log.d("ProfileFragment", "deleteLocation: $result")
                }

                is Result.Success -> {
                    val response = result.data.message
                    Log.d("ProfileFragment", "deleteLocation: $response")
                    Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show()
                }

                is Result.Error -> {
                    Log.d("ProfileFragment", "deleteLocation: ${result.error}")
                }
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            onLocationEnable(it.first, it.second)
        }
        createLocationRequest()
        createLocationCallback()

    }

    private fun updateMapLocation(latitude: Double, longitude: Double) {
        val currentLocation = LatLng(latitude, longitude)
        mMap.addMarker(
            MarkerOptions().position(currentLocation).title(binding.edtName.text.toString())
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

    private fun getUserLocation() {
        LocationHelper.getLastKnownLocation(
            fusedLocationClient,
            { location ->
                viewModel.updateCurrentLocation(location.latitude, location.longitude)
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
        if (!isAdded || view == null) {
            val ayam = viewModel.updateLocation(latitude, longitude)
            Log.d("ProfileLocation", "updateLocation: $ayam $latitude $longitude")
            return
        }
        viewModel.updateLocation(latitude, longitude)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        setVisibility(binding.profileProgressBar, true)
                    }

                    is Result.Success -> {
                        setVisibility(binding.profileProgressBar, false)
                        val response = result.data.message
                        Log.d("ProfileLocation", "location: $response")
                        Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show()
                    }

                    is Result.Error -> {
                        setVisibility(binding.profileProgressBar, false)
                        Log.d("LocationError", "startUpload: ${result.error}")
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }

            }
    }
    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK ->
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                RESULT_CANCELED ->
                    Toast.makeText(
                        requireContext(),
                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.SECONDS.toMillis(30)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireContext())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getUserLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(requireContext(), sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult: " + location.latitude + ", " + location.longitude)
                    updateLocation(location.latitude, location.longitude)
                }
            }
        }
    }
    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e(TAG, "Error : " + exception.message)
        }
    }
    private fun onChangeEditText() {
        EditTextUtils.setupTextWatcher(
            binding.edtName,
            binding.edtNameVendor,
            binding.edtNoHp,
            binding.edtListProduct
        ) {
            isEdited = true
            ButtonUtils.enableButtonIfEdited(binding.btnCancel, isEdited)
            ButtonUtils.enableButtonIfEdited(binding.btnSave, isEdited)
        }

        ButtonUtils.enableButtonIfEdited(binding.btnCancel, isEdited)
        ButtonUtils.enableButtonIfEdited(binding.btnSave, isEdited)

        binding.btnCancel.setOnClickListener {
            binding.edtName.text = null
            binding.edtNameVendor.text = null
            binding.edtNoHp.text = null
            binding.edtListProduct.text = null
            isEdited = false
            ButtonUtils.enableButtonIfEdited(binding.btnCancel, isEdited)
            ButtonUtils.enableButtonIfEdited(binding.btnSave, isEdited)
        }
    }
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    override fun onResume() {
        super.onResume()
        if (binding.switchLocation.isChecked) {
            startLocationUpdates()
        }
    }
    override fun onPause() {
        super.onPause()
        if (binding.switchLocation.isChecked) {
            startLocationUpdates()
        }
    }
    private fun onPermissionDenied() {
        Toast.makeText(
            requireContext(), "Location permission denied", Toast.LENGTH_SHORT
        ).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (binding.switchLocation.isChecked) {
            startLocationUpdates()
        }
    }
    companion object {
        private const val TAG = "profileFragment"
    }
}