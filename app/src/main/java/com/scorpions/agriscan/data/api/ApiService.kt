package com.scorpions.agriscan.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/")
    suspend fun ping(): Response<Map<String, String>>

    @GET("/health")
    suspend fun health(): Response<HealthResponse>

    @Multipart
    @POST("/predict/disease")
    suspend fun predictDisease(
        @Part file: MultipartBody.Part
    ): Response<DiseaseResponse>

    @Multipart
    @POST("/predict/hybrid")
    suspend fun predictHybrid(
        @Part file: MultipartBody.Part
    ): Response<HybridResponse>

    @Multipart
    @POST("/predict/qwen")
    suspend fun predictQwen(
        @Part file: MultipartBody.Part
    ): Response<QwenResponse>

    @POST("/api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<UserOut>

    @POST("/api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>

    @Multipart
    @POST("/api/v1/diagnose-crop")
    suspend fun diagnoseCrop(
        @Part file: MultipartBody.Part
    ): Response<CropDiagnosticResponse>
}
