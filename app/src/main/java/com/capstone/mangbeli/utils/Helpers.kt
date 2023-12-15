package com.capstone.mangbeli.utils

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.capstone.mangbeli.data.local.entity.TokenEntity
import com.capstone.mangbeli.ui.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


fun checkTokenAvailability(
    viewModel: HomeViewModel,
    token: TokenEntity,
    lifecycleOwner: LifecycleOwner,
    callback: (TokenEntity) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
    val expirationDateString = "14 Dec 2023 14:35:59" // Ganti dengan tanggal kadaluarsa yang benar
    val expirationDate = dateFormat.parse(expirationDateString)

    val currentTime = Calendar.getInstance().time

    if (currentTime.after(expirationDate)) {
        // Waktu hari ini sudah melewati waktu kadaluarsa
        viewModel.logoutRefreshToken() // Panggil fungsi logout pada viewModel
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
            callback(token)
        }
    }
}