package com.scorpions.agriscan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageName: String,
    val disease: String,
    val confidence: Float,
    val treatment: String,
    val mode: String,
    val source: String,
    val qwenOpinion: String?,
    val localImageUri: String?,
    val date: String,
    val time: String,
    val timestamp: Long
)
