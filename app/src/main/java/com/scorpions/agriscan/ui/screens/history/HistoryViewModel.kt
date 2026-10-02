package com.scorpions.agriscan.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scorpions.agriscan.data.local.PredictionEntity
import com.scorpions.agriscan.data.repository.HistoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryUiState(
    val predictions: List<PredictionEntity> = emptyList(),
    val isEmpty: Boolean = true,
    val totalCount: Int = 0
)

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _selectedPrediction = MutableStateFlow<PredictionEntity?>(null)
    val selectedPrediction: StateFlow<PredictionEntity?> = _selectedPrediction.asStateFlow()

    init {
        viewModelScope.launch {
            historyRepository.allPredictions.collect { list ->
                _uiState.value = HistoryUiState(
                    predictions = list,
                    isEmpty = list.isEmpty(),
                    totalCount = list.size
                )
            }
        }
    }

    fun selectPrediction(id: Long) {
        viewModelScope.launch {
            _selectedPrediction.value = historyRepository.getById(id)
        }
    }

    fun deletePrediction(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteById(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }
}
