package com.scorpions.agriscan.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scorpions.agriscan.data.api.HealthResponse
import com.scorpions.agriscan.data.api.RetrofitClient
import com.scorpions.agriscan.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val health: HealthResponse? = null,
    val serverOnline: Boolean = false,
    val predictionCount: Int = 0,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.api.health()
                if (response.isSuccessful && response.body() != null) {
                    val health = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        health = health,
                        serverOnline = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        serverOnline = false,
                        errorMessage = "Server returned ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    serverOnline = false,
                    errorMessage = e.message ?: "Cannot reach server"
                )
            }
        }
        viewModelScope.launch {
            val count = historyRepository.count()
            _uiState.value = _uiState.value.copy(predictionCount = count)
        }
    }
}
