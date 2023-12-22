package com.capstone.mangbeli.ui.role


import androidx.lifecycle.ViewModel
import com.capstone.mangbeli.data.repository.MangRepository
import com.capstone.mangbeli.model.UserProfile
import com.capstone.mangbeli.model.VendorProfile

class AddRoleViewModel(private val userRepository: MangRepository) : ViewModel() {
    fun updateUserProfile(updateUser: UserProfile) = userRepository.updateUserProfile(updateUser)
    suspend fun saveRole(role: String) = userRepository.saveRole(role)
    fun updateVendorProfile(updateVendor: VendorProfile) = userRepository.updateVendorProfile(updateVendor)
}