package com.bumantra.mangbeli.ui.signup

import androidx.lifecycle.ViewModel
import com.bumantra.mangbeli.data.repository.MangRepository
import com.bumantra.mangbeli.data.response.RegisterResponse

class SignUpViewModel(private val repository: MangRepository) : ViewModel() {
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return repository.register(
            name,
            email,
            password
        )
    }
}