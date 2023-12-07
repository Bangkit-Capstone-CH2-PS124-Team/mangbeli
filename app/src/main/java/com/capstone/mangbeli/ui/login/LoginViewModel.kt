package com.capstone.mangbeli.ui.login

import androidx.lifecycle.ViewModel
import com.capstone.mangbeli.data.repository.MangRepository

class LoginViewModel (private val repository: MangRepository) : ViewModel() {
    suspend fun login(email: String, password: String) {
        repository.login(
            email,
            password
        )
    }
}