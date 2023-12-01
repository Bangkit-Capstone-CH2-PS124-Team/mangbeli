package com.bumantra.mangbeli.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumantra.mangbeli.R
import com.bumantra.mangbeli.databinding.ActivityHomeBinding
import com.bumantra.mangbeli.ui.ViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var userRole: String? = null
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val navView: BottomNavigationView = binding.navView

        viewModel.getSession().observe(this) { user ->
            userRole = user.role

            val navController = findNavController(R.id.nav_host_fragment_activity_home)

            // Inflate navGraph
            val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
            navGraph.setStartDestination(R.id.navigation_home)

            // Set startDestination based on userRole
            when (userRole) {
                "user" -> {
                    navGraph.setStartDestination(R.id.navigation_home)
                }
                "vendor" -> {
                    navGraph.setStartDestination(R.id.navigation_home_pedagang)
                }

                else -> {
                    navGraph.setStartDestination(R.id.navigation_home)
                }
            }

            // Set navGraph to navController
            navController.graph = navGraph

            // Setup ActionBar dan NavigationController
            val appBarConfiguration = AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_maps, R.id.navigation_profile, R.id.navigation_settings
            ).build()
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }
}
