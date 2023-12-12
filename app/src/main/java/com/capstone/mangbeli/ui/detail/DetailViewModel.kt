package com.capstone.mangbeli.ui.detail

import androidx.lifecycle.ViewModel
import com.capstone.mangbeli.data.repository.MangRepository

class DetailViewModel(private val repository: MangRepository) : ViewModel() {
    fun getDetailVendor(id:String) = repository.getDetailVendor(id)
}