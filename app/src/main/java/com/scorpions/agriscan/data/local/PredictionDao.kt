package com.scorpions.agriscan.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prediction: PredictionEntity): Long

    @Query("SELECT * FROM predictions ORDER BY timestamp DESC")
    fun getAll(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE id = :id")
    suspend fun getById(id: Long): PredictionEntity?

    @Query("DELETE FROM predictions")
    suspend fun deleteAll()

    @Query("DELETE FROM predictions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM predictions")
    suspend fun count(): Int
}
