package com.capstone.mangbeli.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.mangbeli.data.repository.TokenRepository
import com.capstone.mangbeli.model.User
import kotlinx.coroutines.launch


class TokenViewModel (private val repository: TokenRepository) : ViewModel() {
    fun reefreshToken() = viewModelScope.launch {
        val response = repository.callRefreshToken()
        Log.d("coba", "refreshToken: $response")
    }
    private val _user: MutableLiveData<User> = MutableLiveData()
    val userResponse: LiveData<User> = _user
    fun logoutRefreshToken() {
        viewModelScope.launch {
            repository.logoutRefreshToken()
        }
    }
    fun getSession(): LiveData<User> {
        viewModelScope.launch {
            repository.getSession().collect { values ->
                _user.postValue(values)
            }
        }
        return userResponse
        Log.d("coba", "refreshToken: $userResponse")
    }

}