package com.capstone.mangbeli


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.capstone.mangbeli.databinding.SlideLayoutBinding


class ViewPagerAdapter(private val context: Context) : PagerAdapter() {
    private val images = intArrayOf(
        R.drawable.onboarding1,
        R.drawable.onboarding2,
        R.drawable.onboarding3
    )

    private val descriptions = intArrayOf(
        R.string.description1,
        R.string.description2,
        R.string.description3
    )

    override fun getCount(): Int {
        return descriptions.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val binding = SlideLayoutBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        val slideTitleImage = binding.imageView
        val slideDescription = binding.description

        slideTitleImage.setImageResource(images[position])
        slideDescription.setText(descriptions[position])
        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
