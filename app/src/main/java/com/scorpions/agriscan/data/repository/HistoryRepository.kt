package com.scorpions.agriscan.data.repository

import com.scorpions.agriscan.data.local.AppDatabase
import com.scorpions.agriscan.data.local.PredictionEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val db: AppDatabase) {

    val allPredictions: Flow<List<PredictionEntity>> = db.predictionDao().getAll()

    suspend fun getById(id: Long): PredictionEntity? = db.predictionDao().getById(id)

    suspend fun deleteById(id: Long) = db.predictionDao().deleteById(id)

    suspend fun deleteAll() = db.predictionDao().deleteAll()

    suspend fun count(): Int = db.predictionDao().count()
}
