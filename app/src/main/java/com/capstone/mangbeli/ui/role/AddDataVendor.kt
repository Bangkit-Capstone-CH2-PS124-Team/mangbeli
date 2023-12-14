package com.capstone.mangbeli.ui.role

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.ActivityAddDataVendorBinding
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.model.VendorProfile
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.home.HomeActivity
import com.capstone.mangbeli.utils.Result
import java.text.NumberFormat
import java.util.Locale

class AddDataVendor : AppCompatActivity() {

    private lateinit var binding: ActivityAddDataVendorBinding
    private val viewModel by viewModels<AddRoleViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataVendorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSlider()
        setAction()
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
            if (value > binding.maksimumSlider.value) {
                binding.minimumSlider.value = binding.maksimumSlider.value
            } else {
                val formattedValue = currencyFormat.format(value.toDouble())
                binding.minimumTextView.text = getString(R.string.minimum_sapi) + formattedValue
            }
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

    private fun setAction() {
        binding.btnNext.setOnClickListener {
            val nama = binding.nameVendorEditText.text.toString()
            val inputString = binding.productEditText.text
            val noHp = binding.edtNoHp.text.toString()
            val products = inputString?.split(", ")?.map { it.trim() }
            val updateVendor = VendorProfile(
                nameVendor = nama,
                products = products,
                minPrice = binding.minimumSlider.value.toInt(),
                maxPrice =binding.maksimumSlider.value.toInt()
            )
            val updateUser = UserProfile(
                noHp = noHp
            )
            viewModel.updateUserProfile(updateUser)
                .observe(this@AddDataVendor) { result ->
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

            viewModel.updateVendorProfile(updateVendor).observe(this@AddDataVendor) { result ->
                when (result) {
                    is Result.Loading -> {
                        Log.d("ProfileFragment", "initProfile: Loading")
                    }

                    is Result.Success -> {
                        val mainIntent = Intent(this@AddDataVendor, HomeActivity::class.java)
                        startActivity(mainIntent)
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@AddDataVendor,
                            "Error ${result.error}: Cek koneksi internet anda!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("DataDiriActivity", "onCreate: ${result.error}")
                    }
                }
            }
            Toast.makeText(this@AddDataVendor, "Data berhasil disimpan $updateVendor", Toast.LENGTH_SHORT).show()
        }
    }
}