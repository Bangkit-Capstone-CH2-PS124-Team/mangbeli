package com.capstone.mangbeli.data.local.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.mangbeli.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserPref private constructor( private val dataStore: DataStore<Preferences>) {

    suspend fun saveToken(token: String?, email: String, role : String) {
        if (token != null) {
            dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = token
                preferences[EMAIL_KEY] = email
                preferences[ROLE_KEY] = role
                preferences[IS_LOGIN_KEY] = true
            }
        } else {
            Log.e("TokenError", "Token is null in the login response")
        }
    }

    fun getSession(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[TOKEN_KEY] ?: " ",
                preferences[EMAIL_KEY] ?: "",
                preferences[ROLE_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )

        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: UserPref? = null
        val TOKEN_KEY = stringPreferencesKey("token")
        val EMAIL_KEY = stringPreferencesKey("email")
        val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        val ROLE_KEY = stringPreferencesKey("role")

        fun getInstance(dataStore: DataStore<Preferences>): UserPref {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}