package com.capstone.mangbeli.data.local.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.mangbeli.model.Marker
import com.capstone.mangbeli.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserPref private constructor( private val dataStore: DataStore<Preferences>) {

    suspend fun saveToken(token: String?, email: String, role : String, expiredAksesToken: String) {
        if (token != null) {
            dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = token
                preferences[EMAIL_KEY] = email
                preferences[ROLE_KEY] = role
                preferences[EXPIRED_TOKEN] = expiredAksesToken
            }
            Log.d("USERPREF", "saveToken: ${TOKEN_KEY}")
        } else {
            Log.e("TokenError", "Token is null in the login response")
        }
    }
    suspend fun saveRole(role: String) {
        dataStore.edit { preferences ->
            preferences[ROLE_KEY] = role
        }
    }
    suspend fun saveTokenNew(token: String, expiredAksesToken: String) {
            dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = token
                preferences[EXPIRED_TOKEN] = expiredAksesToken
            Log.d("USERPREF", "saveToken: ${TOKEN_KEY}")
        }
    }
     suspend fun saveRefreshToken(token: String, Expired: String) {
        dataStore.edit { preferences ->
            preferences[RefreshToken_KEY] = token
            preferences[EXPIRED_KEY] = Expired
        }
    }

//    fun getMarker(): Flow<Marker> {
//        return dataStore.data.map { preferences ->
//            Marker(
//                preferences[Marker.MARKER_LATITUDE] ?: 0.0,
//                preferences[Marker.MARKER_LONGITUDE] ?: 0.0,
//            )
//        }
//    }


    fun getSession(): Flow<User> {
        return dataStore.data.map { preferences ->
            Log.d("USERPREF", "saveToken: ${TOKEN_KEY}")
            User(
                preferences[TOKEN_KEY] ?: " ",
                preferences[EMAIL_KEY] ?: "",
                preferences[ROLE_KEY] ?: "",
                preferences[EXPIRED_KEY] ?: " ",
                preferences[EXPIRED_TOKEN] ?: " ",
                preferences[RefreshToken_KEY] ?: " ",

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
        val ROLE_KEY = stringPreferencesKey("role")
        val EXPIRED_KEY = stringPreferencesKey("expired")
        val RefreshToken_KEY = stringPreferencesKey("refreshToken")
        val EXPIRED_TOKEN = stringPreferencesKey("expiredToken")

        fun getInstance(dataStore: DataStore<Preferences>): UserPref {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}