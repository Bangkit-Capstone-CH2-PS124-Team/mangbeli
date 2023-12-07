package com.capstone.mangbeli.ui.signup

import androidx.lifecycle.ViewModel
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.data.remote.response.RegisterResponse

class SignUpViewModel(private val repository: MangRepository) : ViewModel() {
    suspend fun register(name: String, email: String, password: String, confPassword: String, role: String): RegisterResponse {
        return repository.register(
            name,
            email,
            password,
            confPassword,
            role
        )
    }
}