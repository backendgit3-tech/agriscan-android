package com.scorpions.agriscan.data.api

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

/**
 * Polymorphic confidence deserializer.
 * The backend returns confidence as:
 *  - String "92.13%" in /predict/disease and /predict/hybrid local_model
 *  - Double 0-100 in /predict/hybrid qwen_opinion and /predict/qwen (success)
 *  - Double 0.0-1.0 in /api/v1/diagnose-crop confidence_score
 * This normalizer always outputs a Float 0-100.
 */
class ConfidenceDeserializer : JsonDeserializer<Float> {
    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ): Float {
        return when {
            json.isJsonPrimitive && json.asJsonPrimitive.isString -> {
                val str = json.asString.trim().removeSuffix("%")
                str.toFloatOrNull() ?: 0f
            }
            json.isJsonPrimitive && json.asJsonPrimitive.isNumber -> {
                val value = json.asFloat
                if (value in 0f..1f) value * 100f else value
            }
            else -> 0f
        }
    }
}

// ── /health ──────────────────────────────────────────────
data class HealthResponse(
    val status: String,
    val model_loaded: Boolean,
    val qwen_enabled: Boolean,
    val storage_enabled: Boolean,
    val storage_type: String
)

// ── /predict/disease ─────────────────────────────────────
data class DiseasePrediction(
    val disease: String,
    @JsonAdapter(ConfidenceDeserializer::class)
    val confidence: Float,
    val treatment: String,
    val error: String? = null
)

data class DiseaseResponse(
    val status: String,
    val filename: String?,
    val prediction: DiseasePrediction
)

// ── /predict/hybrid ──────────────────────────────────────
data class StorageInfo(
    val url: String,
    val filename: String,
    val storage_type: String
)

data class QwenOpinion(
    val disease: String?,
    @JsonAdapter(ConfidenceDeserializer::class)
    val confidence: Float,
    val treatment: String?,
    val description: String?
)

data class HybridResponse(
    val status: String,
    val mode: String,
    val image: StorageInfo,
    val local_model: DiseasePrediction,
    val qwen_opinion: QwenOpinion?
)

// ── /predict/qwen ────────────────────────────────────────
data class QwenDiagnosis(
    val disease: String?,
    @JsonAdapter(ConfidenceDeserializer::class)
    val confidence: Float,
    val treatment: String?,
    val description: String?,
    val error: String? = null
)

data class QwenResponse(
    val status: String,
    val image: StorageInfo,
    val diagnosis: QwenDiagnosis,
    val source: String,
    val qwen_note: String? = null
)

// ── /api/v1/auth ─────────────────────────────────────────
data class RegisterRequest(
    val phone: String,
    val password: String,
    val full_name: String,
    val district: String,
    val language: String = "urdu"
)

data class LoginRequest(
    val phone: String,
    val password: String
)

data class UserOut(
    val id: Int,
    val phone: String,
    val full_name: String,
    val district: String,
    val language: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String
)

// ── /api/v1/diagnose-crop ────────────────────────────────
data class CropDiagnosticResponse(
    val detected_anomaly: String?,
    @JsonAdapter(ConfidenceDeserializer::class)
    val confidence_score: Float,
    val status: String,
    val source: String?,
    val message: String?,
    val recommendation_urdu: String,
    val recommendation_sindhi: String,
    val organic_alternative: String?,
    val image_path: String,
    val crop_type: String?,
    val treatment_en: String?
)

// ── Unified prediction result ────────────────────────────
data class PredictionResult(
    val disease: String,
    val confidence: Float,
    val treatment: String,
    val mode: String,
    val source: String,
    val qwenOpinion: String?,
    val imageUri: String?,
    val isLowConfidence: Boolean = confidence < 50f,
    val error: String? = null
)
