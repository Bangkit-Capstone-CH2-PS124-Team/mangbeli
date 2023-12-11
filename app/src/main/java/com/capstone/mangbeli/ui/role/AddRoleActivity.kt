package com.capstone.mangbeli.ui.role

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import com.capstone.mangbeli.databinding.ActivityAddRoleBinding

class AddRoleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRoleBinding
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
            val mainIntent = Intent(this@AddRoleActivity, AddFavFoodActivity::class.java)
            startActivity(mainIntent)
        }
    }
}