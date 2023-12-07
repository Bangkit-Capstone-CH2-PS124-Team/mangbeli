package com.capstone.mangbeli.data.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themes: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingsPref private constructor(private val dataStore: DataStore<Preferences>) {


    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    companion object {
        private val THEME_KEY = booleanPreferencesKey("theme_setting")

        @Volatile
        private var INSTANCE: SettingsPref? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingsPref {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingsPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}