package com.capstone.mangbeli.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.mangbeli.data.remote.response.LoginResponse
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.model.User
import kotlinx.coroutines.launch

class HomeViewModel (private val repository: MangRepository) : ViewModel() {
    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }
    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse> = _response
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}