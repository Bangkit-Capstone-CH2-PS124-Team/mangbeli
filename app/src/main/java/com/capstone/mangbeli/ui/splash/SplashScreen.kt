package com.capstone.mangbeli.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.capstone.mangbeli.data.local.pref.SettingsPref
import com.capstone.mangbeli.data.local.pref.dataStore
import com.capstone.mangbeli.databinding.ActivitySplashScreenBinding
import com.capstone.mangbeli.ui.MainActivity
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.home.HomeActivity
import com.capstone.mangbeli.ui.home.HomeViewModel
import com.capstone.mangbeli.ui.home.TokenViewModel
import com.capstone.mangbeli.ui.home.TokenViewModelFactory
import com.capstone.mangbeli.ui.settings.SettingViewModel
import com.capstone.mygithubusers.ui.settings.SettingViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashDisplayLength = 1000
    private var isUserValid: Boolean? = null
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val tokenViewModel by viewModels<TokenViewModel> {
        TokenViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingsPref.getInstance(applicationContext.dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        viewModel.getToken()

        viewModel.userResponse.observe(this@SplashScreen) { user ->
            ViewModelFactory.refreshInstance()
            TokenViewModelFactory.refreshInstance()
            isUserValid = user.refreshToken == " "
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
            cekSession()
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
    private fun  cekSession(){
        isUserValid?.let { isUserValid ->
            Log.e("SplashScreen", "cekSession: $isUserValid")
            if (isUserValid){
                Handler(Looper.getMainLooper()).postDelayed({
                    val mainIntent = Intent(this@SplashScreen, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }, splashDisplayLength.toLong())
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    val mainIntent = Intent(this@SplashScreen, HomeActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }, splashDisplayLength.toLong())
            }
        }
    }
    override fun onDestroy() {
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}