package com.scorpions.agriscan.data.repository

import com.scorpions.agriscan.data.api.*
import com.scorpions.agriscan.data.local.AppDatabase
import com.scorpions.agriscan.data.local.PredictionEntity
import com.scorpions.agriscan.utils.ApiResult
import com.scorpions.agriscan.utils.ImageUtils
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*

class PredictionRepository(
    private val api: ApiService,
    private val db: AppDatabase
) {

    val historyFlow: Flow<List<PredictionEntity>> = db.predictionDao().getAll()

    suspend fun predictDisease(imageFile: File, localImageUri: String?): ApiResult<PredictionResult> {
        return try {
            val part = ImageUtils.fileToMultipart(imageFile)
            val response = api.predictDisease(part)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val result = PredictionResult(
                    disease = body.prediction.disease,
                    confidence = body.prediction.confidence,
                    treatment = body.prediction.treatment,
                    mode = "local",
                    source = "Crop Disease Model",
                    qwenOpinion = null,
                    imageUri = localImageUri,
                    error = body.prediction.error
                )
                saveToHistory(result, imageFile.name)
                ApiResult.Success(result)
            } else {
                ApiResult.Error("Server returned ${response.code()}", response.code())
            }
        } catch (e: SocketTimeoutException) {
            ApiResult.Timeout
        } catch (e: java.io.IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun predictHybrid(imageFile: File, localImageUri: String?): ApiResult<PredictionResult> {
        return try {
            val part = ImageUtils.fileToMultipart(imageFile)
            val response = api.predictHybrid(part)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val qwen = body.qwen_opinion
                val result = PredictionResult(
                    disease = if (body.mode == "hybrid" && qwen?.disease != null) qwen.disease else body.local_model.disease,
                    confidence = if (body.mode == "hybrid" && qwen != null) qwen.confidence else body.local_model.confidence,
                    treatment = if (body.mode == "hybrid" && qwen?.treatment != null) qwen.treatment else body.local_model.treatment,
                    mode = body.mode,
                    source = if (body.mode == "hybrid") "Hybrid (Local + Qwen)" else "Local Model",
                    qwenOpinion = qwen?.description,
                    imageUri = localImageUri,
                    error = body.local_model.error
                )
                saveToHistory(result, imageFile.name)
                ApiResult.Success(result)
            } else {
                ApiResult.Error("Server returned ${response.code()}", response.code())
            }
        } catch (e: SocketTimeoutException) {
            ApiResult.Timeout
        } catch (e: java.io.IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun predictQwen(imageFile: File, localImageUri: String?): ApiResult<PredictionResult> {
        return try {
            val part = ImageUtils.fileToMultipart(imageFile)
            val response = api.predictQwen(part)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val result = PredictionResult(
                    disease = body.diagnosis.disease ?: "Unknown",
                    confidence = body.diagnosis.confidence,
                    treatment = body.diagnosis.treatment ?: "",
                    mode = "qwen",
                    source = body.source,
                    qwenOpinion = body.diagnosis.description,
                    imageUri = localImageUri,
                    error = body.diagnosis.error,
                    isLowConfidence = body.status == "fallback_local" || body.diagnosis.confidence < 50f
                )
                saveToHistory(result, imageFile.name)
                ApiResult.Success(result)
            } else {
                ApiResult.Error("Server returned ${response.code()}", response.code())
            }
        } catch (e: SocketTimeoutException) {
            ApiResult.Timeout
        } catch (e: java.io.IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun saveToHistory(result: PredictionResult, imageName: String) {
        val now = Calendar.getInstance()
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFmt = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

        val entity = PredictionEntity(
            imageName = imageName,
            disease = result.disease,
            confidence = result.confidence,
            treatment = result.treatment,
            mode = result.mode,
            source = result.source,
            qwenOpinion = result.qwenOpinion,
            localImageUri = result.imageUri,
            date = dateFmt.format(now.time),
            time = timeFmt.format(now.time),
            timestamp = now.timeInMillis
        )
        db.predictionDao().insert(entity)
    }
}
