package com.capstone.mangbeli.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.capstone.mangbeli.data.remote.response.LoginResponse
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.model.FCMToken
import com.capstone.mangbeli.model.User
import kotlinx.coroutines.launch

class HomeViewModel (private val repository: MangRepository) : ViewModel() {
    private var _searchquery = MutableLiveData<String>()
    val searchquery: LiveData<String> = _searchquery

    private var _filterBy = MutableLiveData<String>()
    val filterBy: LiveData<String> = _filterBy


    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse> = _response
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    private val _user: MutableLiveData<User> = MutableLiveData()
    val userResponse: LiveData<User> = _user

    fun getSession(): LiveData<User> {
        viewModelScope.launch {
            repository.getSession().collect { values ->
                _user.postValue(values)
            }
        }
        Log.e("SplashScreen2", "refreshToken: $userResponse")
        return userResponse

    }

     fun logoutRefreshToken() {
        viewModelScope.launch {
            repository.logoutRefreshToken(userResponse.value?.refreshToken.toString())
        }
    }
    fun getToken() = viewModelScope.launch {
        repository.getSession().collect { values ->
            _user.postValue(values)
        }
    }
    fun updateFCMToken(fcm: FCMToken) = repository.updateFCMToken(fcm)
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
    fun getAllVendors(location: Int = 1, search: String? = "", filter: String? ="") = repository.getAllVendors(location, search = search, filter = filter).cachedIn(viewModelScope)
    fun setSearchQuery(new: String) {
        _searchquery.value = new
    }
    fun setFilterBy(new: String) {
        _filterBy.value = new
    }


}