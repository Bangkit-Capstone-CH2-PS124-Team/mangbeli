package com.capstone.mangbeli.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.mangbeli.data.repository.TokenRepository
import com.capstone.mangbeli.di.Injection

class TokenViewModelFactory(private val tokenRepository: TokenRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TokenViewModel::class.java)) {
            return TokenViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var INSTANCE: TokenViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): TokenViewModelFactory {
            if (INSTANCE == null) {
                synchronized(TokenViewModelFactory::class.java) {
                    INSTANCE = TokenViewModelFactory(Injection.provideTokenRepository(context))
                }
            }
            return INSTANCE as TokenViewModelFactory
        }
        fun refreshInstance() {
            INSTANCE = null
            Injection.refreshRepository()
        }
    }
}