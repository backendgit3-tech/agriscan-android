package com.scorpions.agriscan.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "agriscan_prefs")

class UserPreferences(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    val language: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "en" }
    val authToken: Flow<String?> = context.dataStore.data.map { it[Keys.AUTH_TOKEN] }
    val apiBaseUrl: Flow<String?> = context.dataStore.data.map { it[Keys.API_BASE_URL] }
    val userName: Flow<String?> = context.dataStore.data.map { it[Keys.USER_NAME] }
    val userPhone: Flow<String?> = context.dataStore.data.map { it[Keys.USER_PHONE] }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[Keys.IS_LOGGED_IN] ?: false }
    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { it[Keys.HAS_COMPLETED_ONBOARDING] ?: false }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    }

    suspend fun setAuthToken(token: String?) {
        context.dataStore.edit {
            if (token != null) it[Keys.AUTH_TOKEN] = token
            else it.remove(Keys.AUTH_TOKEN)
        }
    }

    suspend fun setApiBaseUrl(url: String?) {
        context.dataStore.edit {
            if (url != null) it[Keys.API_BASE_URL] = url
            else it.remove(Keys.API_BASE_URL)
        }
    }

    suspend fun setUser(name: String, phone: String) {
        context.dataStore.edit {
            it[Keys.USER_NAME] = name
            it[Keys.USER_PHONE] = phone
            it[Keys.IS_LOGGED_IN] = true
        }
    }

    suspend fun setHasCompletedOnboarding(completed: Boolean) {
        context.dataStore.edit { it[Keys.HAS_COMPLETED_ONBOARDING] = completed }
    }

    suspend fun clearAuth() {
        context.dataStore.edit {
            it.remove(Keys.AUTH_TOKEN)
            it.remove(Keys.USER_NAME)
            it.remove(Keys.USER_PHONE)
            it[Keys.IS_LOGGED_IN] = false
        }
    }

    suspend fun getTokenSync(): String? = context.dataStore.data.first()[Keys.AUTH_TOKEN]
    suspend fun getLanguageSync(): String = context.dataStore.data.first()[Keys.LANGUAGE] ?: "en"
}
