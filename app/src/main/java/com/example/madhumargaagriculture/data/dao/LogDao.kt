package com.example.madhumargaagriculture.data.dao

import androidx.room.*
import com.example.madhumargaagriculture.data.entity.InspectionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Insert
    suspend fun insert(log: InspectionLog)

    @Query("SELECT * FROM inspection_logs ORDER BY timestamp DESC")
    fun getLogs(): Flow<List<InspectionLog>>

    @Update
    suspend fun updateLog(log: InspectionLog)

    @Delete
    suspend fun deleteLog(log: InspectionLog)
}
