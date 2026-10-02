package com.scorpions.agriscan.data.repository

import com.scorpions.agriscan.data.api.*
import com.scorpions.agriscan.data.local.UserPreferences
import com.scorpions.agriscan.utils.ApiResult
import java.net.SocketTimeoutException

class AuthRepository(
    private val api: ApiService,
    private val prefs: UserPreferences
) {

    val isLoggedIn = prefs.isLoggedIn
    val authToken = prefs.authToken
    val userName = prefs.userName

    suspend fun register(
        phone: String,
        password: String,
        fullName: String,
        district: String,
        language: String = "urdu"
    ): ApiResult<UserOut> {
        return try {
            val response = api.register(RegisterRequest(phone, password, fullName, district, language))
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                val msg = response.errorBody()?.string() ?: "Registration failed"
                ApiResult.Error(msg, response.code())
            }
        } catch (e: SocketTimeoutException) {
            ApiResult.Timeout
        } catch (e: java.io.IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun login(phone: String, password: String): ApiResult<TokenResponse> {
        return try {
            val response = api.login(LoginRequest(phone, password))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!
                prefs.setAuthToken(token.access_token)
                prefs.setUser(phone, phone)
                ApiResult.Success(token)
            } else {
                val msg = response.errorBody()?.string() ?: "Login failed"
                ApiResult.Error(msg, response.code())
            }
        } catch (e: SocketTimeoutException) {
            ApiResult.Timeout
        } catch (e: java.io.IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun logout() {
        prefs.clearAuth()
    }

    suspend fun getToken(): String? = prefs.getTokenSync()
}
