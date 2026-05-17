package com.example.madhumargaagriculture.data.dao

import androidx.room.*
import com.example.madhumargaagriculture.data.entity.InspectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {

    @Insert
    suspend fun insertInspection(inspection: InspectionEntity)

    @Query("SELECT * FROM inspections")
    fun getAllInspections(): Flow<List<InspectionEntity>>
}
