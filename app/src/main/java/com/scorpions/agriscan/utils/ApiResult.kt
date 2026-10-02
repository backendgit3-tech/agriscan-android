package com.scorpions.agriscan.utils

import com.scorpions.agriscan.data.api.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object ImageUtils {

    fun fileToMultipart(file: File, fieldName: String = "file"): MultipartBody.Part {
        val requestBody = file.readBytes()
            .toRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData(
            fieldName,
            file.name,
            requestBody
        )
    }

    fun bytesToMultipart(bytes: ByteArray, filename: String = "leaf.jpg"): MultipartBody.Part {
        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData("file", filename, requestBody)
    }
}

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    data object NetworkError : ApiResult<Nothing>()
    data object ServerError : ApiResult<Nothing>()
    data object Timeout : ApiResult<Nothing>()
}

fun confidenceLabel(confidence: Float): String {
    return when {
        confidence >= 90f -> "Very High Confidence"
        confidence >= 70f -> "High Confidence"
        confidence >= 50f -> "Moderate Confidence"
        else -> "Low Confidence"
    }
}

fun confidenceLabelUrdu(confidence: Float): String {
    return when {
        confidence >= 90f -> "بہت زیادہ اعتماد"
        confidence >= 70f -> "زیادہ اعتماد"
        confidence >= 50f -> "معتدل اعتماد"
        else -> "کم اعتماد"
    }
}
