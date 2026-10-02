package com.scorpions.agriscan.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scorpions.agriscan.data.repository.AuthRepository
import com.scorpions.agriscan.utils.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoginMode: Boolean = true,
    val phone: String = "",
    val password: String = "",
    val fullName: String = "",
    val district: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updatePhone(value: String) { _uiState.value = _uiState.value.copy(phone = value) }
    fun updatePassword(value: String) { _uiState.value = _uiState.value.copy(password = value) }
    fun updateFullName(value: String) { _uiState.value = _uiState.value.copy(fullName = value) }
    fun updateDistrict(value: String) { _uiState.value = _uiState.value.copy(district = value) }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null,
            successMessage = null
        )
    }

    fun submit() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

            if (state.isLoginMode) {
                when (val result = authRepository.login(state.phone, state.password)) {
                    is ApiResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            successMessage = "Login successful!"
                        )
                    }
                    is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = result.message
                    )
                    is ApiResult.NetworkError -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = "Cannot reach server"
                    )
                    is ApiResult.ServerError -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = "Server error"
                    )
                    is ApiResult.Timeout -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = "Request timed out"
                    )
                }
            } else {
                when (val result = authRepository.register(
                    phone = state.phone,
                    password = state.password,
                    fullName = state.fullName,
                    district = state.district
                )) {
                    is ApiResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoginMode = true,
                            successMessage = "Registration successful! Please log in."
                        )
                    }
                    is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = result.message
                    )
                    is ApiResult.NetworkError -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = "Cannot reach server"
                    )
                    is ApiResult.ServerError -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = "Server error"
                    )
                    is ApiResult.Timeout -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = "Request timed out"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
        }
    }
}
