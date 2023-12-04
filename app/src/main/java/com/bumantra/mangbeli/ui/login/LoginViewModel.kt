package com.bumantra.mangbeli.ui.login

import androidx.lifecycle.ViewModel
import com.bumantra.mangbeli.data.repository.MangRepository

class LoginViewModel (private val repository: MangRepository) : ViewModel() {
    suspend fun login(email: String, password: String) {
        repository.login(
            email,
            password
        )
    }
}