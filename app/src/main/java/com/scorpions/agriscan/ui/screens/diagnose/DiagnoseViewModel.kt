package com.scorpions.agriscan.ui.screens.diagnose

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scorpions.agriscan.data.api.PredictionResult
import com.scorpions.agriscan.data.api.RetrofitClient
import com.scorpions.agriscan.data.repository.PredictionRepository
import com.scorpions.agriscan.utils.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

enum class AiMode(val label: String, val labelUrdu: String) {
    LOCAL("Local AI", "مقامی AI"),
    HYBRID("Hybrid AI", "ہائبرڈ AI"),
    QWEN("Qwen Vision", "کیون AI")
}

data class DiagnoseUiState(
    val selectedImageUri: Uri? = null,
    val selectedImageFile: File? = null,
    val selectedMode: AiMode = AiMode.HYBRID,
    val isAnalyzing: Boolean = false,
    val result: PredictionResult? = null,
    val errorMessage: String? = null
)

class DiagnoseViewModel(
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnoseUiState())
    val uiState: StateFlow<DiagnoseUiState> = _uiState.asStateFlow()

    fun setImage(uri: Uri, file: File) {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri,
            selectedImageFile = file,
            result = null,
            errorMessage = null
        )
    }

    fun setMode(mode: AiMode) {
        _uiState.value = _uiState.value.copy(selectedMode = mode)
    }

    fun analyze() {
        val file = _uiState.value.selectedImageFile ?: return
        val mode = _uiState.value.selectedMode
        val uri = _uiState.value.selectedImageUri?.toString()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true, errorMessage = null)

            val result: ApiResult<PredictionResult> = when (mode) {
                AiMode.LOCAL -> predictionRepository.predictDisease(file, uri)
                AiMode.HYBRID -> predictionRepository.predictHybrid(file, uri)
                AiMode.QWEN -> predictionRepository.predictQwen(file, uri)
            }

            _uiState.value = when (result) {
                is ApiResult.Success -> _uiState.value.copy(
                    isAnalyzing = false,
                    result = result.data
                )
                is ApiResult.Error -> _uiState.value.copy(
                    isAnalyzing = false,
                    errorMessage = result.message
                )
                is ApiResult.NetworkError -> _uiState.value.copy(
                    isAnalyzing = false,
                    errorMessage = "Cannot reach server. Check your connection."
                )
                is ApiResult.ServerError -> _uiState.value.copy(
                    isAnalyzing = false,
                    errorMessage = "Server error. Try again later."
                )
                is ApiResult.Timeout -> _uiState.value.copy(
                    isAnalyzing = false,
                    errorMessage = "Request timed out. Check server status."
                )
            }
        }
    }

    fun reset() {
        _uiState.value = DiagnoseUiState()
    }
}
