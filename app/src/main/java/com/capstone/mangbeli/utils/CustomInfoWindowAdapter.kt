package com.capstone.mangbeli.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capstone.mangbeli.R
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker


class CustomInfoWindowAdapter(private val context: Context) : InfoWindowAdapter {

    private val mWindow: View? = LayoutInflater.from(context).inflate(R.layout.item_vendor, null)

    @SuppressLint("UseCompatLoadingForDrawables")
    fun windowText(marker: Marker, view: View) {
        val title = marker.title
        val snippet = marker.snippet
        val tvTitle = view.findViewById<TextView>(R.id.tv_item_maps_name_vendor)
        val tvSnippet = view.findViewById<TextView>(R.id.tv_item_maps_name)
        val imgView = view.findViewById(R.id.img_maps_vendor) as ImageView

        if (!title.equals("")) {
            tvTitle.text = title
        }
        if (!snippet.equals("")) {
            tvSnippet.text = snippet
        }

        val imageUrl = marker.tag as? String
        if (imageUrl != null) {
            imageUrl.let {
                imgView.loadImage(imageUrl)
            }
        } else {
            imgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo_mangbeli))
        }
        Log.d("GAMBAR BOS", "windowText: $imageUrl")
        imageUrl?.let {
            // Example using Glide library
            Glide.with(context)
                .load(it)
                .into(imgView)
        }

    }
    override fun getInfoContents(marker: Marker): View? {
        if (mWindow != null) {
            windowText(marker, mWindow)
        }
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        if (mWindow != null) {
            windowText(marker, mWindow)
        }
        return mWindow
    }
}