package com.capstone.mangbeli.utils

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.util.Log
import com.capstone.mangbeli.R
import com.capstone.mangbeli.ui.home.TokenViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun setMapStyle(mMap: GoogleMap, context: Context) {
    try {
        val success =
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
        if (!success) {
            Log.e("SwapMap", "Style parsing failed.")
        }
    } catch (exception: Resources.NotFoundException) {
        Log.e("SwapMap", "Can't find style. Error: ", exception)
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun checkTokenAvailability(
    viewModel: TokenViewModel
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
    val expirationDateString = "14 Dec 2023 14:35:59" // Ganti dengan tanggal kadaluarsa yang benar
    val expirationDate = dateFormat.parse(expirationDateString)

    val currentTime = Calendar.getInstance().time

    if (currentTime.after(expirationDate)) {
        // Waktu hari ini sudah melewati waktu kadaluarsa
        viewModel.reefreshToken() // Panggil fungsi logout pada viewModel
    } else {
        // Waktu hari ini masih sebelum waktu kadaluarsa
        val milis = expirationDate.time - currentTime.time
        if (milis < 3) {
            Log.d("coba", "refreshToken: $milis")
//            viewModel.refreshToken(token.refreshToken)
//            viewModel.refreshResponse.observe(lifecycleOwner) { response ->
//                response.data?.let { tokenEntity ->
//                    token.accessToken = tokenEntity.accessToken
//                    token.expiredAt = getCurrentUnix() + tokenEntity.expiredAt
//                    viewModel.saveToken(token)
//                    callback(token)
//                }
//            }
        } else {
//            callback(token)
        }
    }
}