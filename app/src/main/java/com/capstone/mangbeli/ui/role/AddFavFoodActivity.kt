package com.capstone.mangbeli.ui.role

import FoodAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.mangbeli.databinding.ActivityAddFavFoodBinding
import com.capstone.mangbeli.model.FoodData.foods

class AddFavFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFavFoodBinding
    private lateinit var foodAdapter: FoodAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFavFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = binding.rvFavFood
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        foodAdapter = FoodAdapter(foods)
        recyclerView.adapter = foodAdapter
        binding.btnSave.setOnClickListener {
            val mainIntent = Intent(this@AddFavFoodActivity, DataDiriActivity::class.java)
            val pairs = arrayOf<Pair<View, String>>(
                Pair.create(binding.btnSave, "btnTransition")
            )
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs)
            val selectedFoodNames = foodAdapter.getSelectedFoodNames()
            mainIntent.putStringArrayListExtra("SELECTED_FOOD_NAMES", ArrayList(selectedFoodNames))
            startActivity(mainIntent, options.toBundle())

        }
    }
}