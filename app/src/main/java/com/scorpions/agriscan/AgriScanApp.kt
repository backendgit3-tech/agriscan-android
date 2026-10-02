package com.scorpions.agriscan

import android.app.Application
import com.scorpions.agriscan.data.local.AppDatabase
import com.scorpions.agriscan.data.local.UserPreferences
import com.scorpions.agriscan.data.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AgriScanApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val userPreferences: UserPreferences by lazy { UserPreferences(this) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
        appScope.launch {
            val savedUrl = userPreferences.apiBaseUrl.first()
            val baseUrl = savedUrl ?: BuildConfig.API_BASE_URL
            RetrofitClient.init(baseUrl) {
                runBlocking { userPreferences.getTokenSync() }
            }
        }
    }

    companion object {
        lateinit var instance: AgriScanApp
            private set
    }
}
