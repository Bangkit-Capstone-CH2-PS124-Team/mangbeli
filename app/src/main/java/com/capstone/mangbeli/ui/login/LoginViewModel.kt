package com.capstone.mangbeli.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.capstone.mangbeli.data.remote.network.ApiConfig
import com.capstone.mangbeli.data.repository.MangRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: MangRepository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(
        email,
        password
    )

    init {
        ApiConfig.setRefreshTokenCallback { refreshToken, expired ->
                viewModelScope.launch {
                    repository.saveRefreshToken(refreshToken, expired)
                }
            Log.d("Repo", "refreshToken: $refreshToken")

        }
    }
}