package com.bumantra.mangbeli.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumantra.mangbeli.R
import com.bumantra.mangbeli.ViewPagerAdapter
import com.bumantra.mangbeli.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var dots: Array<TextView>
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.btnBack.visibility = View.INVISIBLE
        val isFirstInstall = getSharedPreferences("PREFS", MODE_PRIVATE).getBoolean("isFirstInstall", true)

        // Jika ini adalah instalasi pertama kali, tampilkan halaman ini
        if (isFirstInstall) {
            setupFirstInstall()
        } else {
            proceedToNextActivity()
        }

    }
    private fun setupFirstInstall() {
        binding.btnBack.setOnClickListener {
            if (getitem(0) > 0) {
                binding.viewPager.setCurrentItem(getitem(-1), true)
                binding.btnNext.text = getString(R.string.next)
            }
        }


        binding.btnNext.setOnClickListener {
            if (getitem(0) == 1) {
                binding.btnNext.text = getString(R.string.menu)
            }

            if (getitem(0) < 2) {
                binding.viewPager.setCurrentItem(getitem(1), true)
            } else {
                val i = Intent(this@MainActivity, MenuActivity::class.java)
                startActivity(i)
                finish()
                getSharedPreferences("PREFS", MODE_PRIVATE).edit().putBoolean("isFirstInstall", false).apply()
            }
        }


        binding.btnSkip.setOnClickListener {
            val i = Intent(this@MainActivity, MenuActivity::class.java)
            startActivity(i)
            finish()
            getSharedPreferences("PREFS", MODE_PRIVATE).edit().putBoolean("isFirstInstall", false).apply()
        }

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        setUpindicator(0)
        binding.viewPager.addOnPageChangeListener(viewListener)
    }

    private fun proceedToNextActivity() {
        val i = Intent(this@MainActivity, MenuActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun setUpindicator(position: Int) {
        dots = Array(3) { TextView(this) }
        binding.indicator.removeAllViews()

        for (i in 0 until 3) {
            dots[i] = TextView(this)
            dots[i].text = "• "
            dots[i].textSize = 35f
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.md_theme_dark_onPrimaryContainer))
            binding.indicator.addView(dots[i])
        }

        if (dots.isNotEmpty()) {
            dots[position].setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            setUpindicator(position)
            binding.btnBack.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        }

        override fun onPageScrollStateChanged(state: Int) {
        }

    }

    private fun getitem(i: Int): Int {
        return binding.viewPager.currentItem + i
    }

}