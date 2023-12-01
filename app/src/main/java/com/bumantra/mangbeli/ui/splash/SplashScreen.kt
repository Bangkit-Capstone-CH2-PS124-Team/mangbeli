package com.bumantra.mangbeli.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.bumantra.mangbeli.databinding.ActivitySplashScreenBinding
import com.bumantra.mangbeli.ui.MenuActivity
import com.bumantra.mangbeli.ui.ViewModelFactory
import com.bumantra.mangbeli.ui.home.HomeActivity
import com.bumantra.mangbeli.ui.home.HomeViewModel


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashDisplayLength = 1000
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getSession().observe(this){ user ->
            if (user.isLogin){
                Handler(Looper.getMainLooper()).postDelayed({
                    val mainIntent = Intent(this@SplashScreen, HomeActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }, splashDisplayLength.toLong())
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    val mainIntent = Intent(this@SplashScreen, MenuActivity::class.java)
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