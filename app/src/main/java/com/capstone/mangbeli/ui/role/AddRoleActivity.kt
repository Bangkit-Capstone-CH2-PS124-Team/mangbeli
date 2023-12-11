package com.capstone.mangbeli.ui.role

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.mangbeli.databinding.ActivityAddRoleBinding
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.profile.ProfileViewModel
import com.capstone.mangbeli.utils.Result
import kotlinx.coroutines.launch

class AddRoleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRoleBinding
    private val viewModel by viewModels<AddRoleViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPembeli.setOnClickListener {
            val mainIntent = Intent(this@AddRoleActivity, AddFavFoodActivity::class.java)
            val pairs = arrayOf<Pair<View, String>>(
                Pair.create(binding.btnPembeli, "btnTransition")
            )
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs)
            startActivity(mainIntent, options.toBundle())
        }


        binding.btnPedagang.setOnClickListener {
            val updateUser = UserProfile(
                role = "vendor"
            )
            viewModel.updateUserProfile(updateUser).observe(this@AddRoleActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        Log.d("ProfileFragment", "initProfile: Loading")
                    }

                    is Result.Success -> {
                        lifecycleScope.launch {
                            viewModel.saveRole("vendor")
                        }
                        val mainIntent = Intent(this@AddRoleActivity, AddDataVendor::class.java)
                        startActivity(mainIntent)
                    }

                    is Result.Error -> {
                        // Tampilkan pesan kesalahan kepada pengguna
                        Toast.makeText(
                            this@AddRoleActivity,
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