package com.example.madhumargaagriculture.data.dao

import androidx.room.*
import com.example.madhumargaagriculture.data.entity.HarvestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HarvestDao {

    @Insert
    suspend fun insertHarvest(harvest: HarvestEntity)

    @Query("SELECT * FROM harvests")
    fun getAllHarvests(): Flow<List<HarvestEntity>>

    @Query("SELECT SUM(quantity) FROM harvests")
    fun getTotalHarvest(): Flow<Double>
}
