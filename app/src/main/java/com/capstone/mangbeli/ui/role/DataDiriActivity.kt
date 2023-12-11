package com.capstone.mangbeli.ui.role

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.capstone.mangbeli.databinding.ActivityDataDiriBinding
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.home.HomeActivity
import com.capstone.mangbeli.ui.profile.ProfileViewModel
import com.capstone.mangbeli.utils.Result

class DataDiriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataDiriBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataDiriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val selectedFoodNames = intent.getStringArrayListExtra("SELECTED_FOOD_NAMES")
        Log.d("DataDiriActivity", "selectedFoodNames: $selectedFoodNames")
        val foodNamesString = selectedFoodNames?.joinToString(", ") ?: ""
        val foodNamesList = foodNamesString.split(", ")
        Log.d("DataDiriActivity", "foodNamesList: $foodNamesList")
        binding.roleEditText.setText("user")
        binding.favEditText.setText(foodNamesString)
        binding.roleEditText.isEnabled = false
        binding.roleEditText.isFocusable = false
        binding.roleEditText.isClickable = false
        binding.startButton.setOnClickListener {
            val updateUser = UserProfile(
                name = null,
                role = "user",
                noHp = binding.edtNoHp.text.toString(),
                favorite = foodNamesList
            )
            profileViewModel.updateUserProfile(updateUser).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        // Tampilkan indikasi loading kepada pengguna
                        // Misalnya, tampilkan ProgressBar atau indikator lainnya
                    }

                    is Result.Success -> {
                        val userData = result.data.message
                        AlertDialog.Builder(this@DataDiriActivity).apply {
                            setTitle("Update Profile Berhasil")
                            setMessage("$userData")
                            setPositiveButton("Lanjut") { _, _ ->
                                val mainIntent = Intent(this@DataDiriActivity, HomeActivity::class.java)
                                startActivity(mainIntent)
                                finish()
                            }
                        }.show() // Panggil show() untuk menampilkan dialog yang dibuat

                    }
                    is Result.Error -> {
                        // Tampilkan pesan kesalahan kepada pengguna
                        Toast.makeText(
                            this@DataDiriActivity,
                            "Error ${result.error}: Cek koneksi internet anda!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("DataDiriActivity", "onCreate: ${result.error}")
                    }
                }
            }
        }


    }
}