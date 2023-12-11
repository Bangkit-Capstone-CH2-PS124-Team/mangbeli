package com.capstone.mangbeli.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.mangbeli.data.remote.response.LoginResponse
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.model.User

class LoginViewModel (private val repository: MangRepository) : ViewModel() {
    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse> = _response
    suspend fun login(email: String, password: String) {
        repository.login(
            email,
            password
        )
    }
}