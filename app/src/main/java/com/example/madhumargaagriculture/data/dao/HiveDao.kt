package com.example.madhumargaagriculture.data.dao

import androidx.room.*
import com.example.madhumargaagriculture.data.entity.HiveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HiveDao {

    @Insert
    suspend fun insertHive(hive: HiveEntity)

    @Update
    suspend fun updateHive(hive: HiveEntity)

    @Delete
    suspend fun deleteHive(hive: HiveEntity)

    @Query("SELECT * FROM hives")
    fun getAllHives(): Flow<List<HiveEntity>>

    @Query("SELECT COUNT(*) FROM hives")
    fun getHiveCount(): Flow<Int>
}
