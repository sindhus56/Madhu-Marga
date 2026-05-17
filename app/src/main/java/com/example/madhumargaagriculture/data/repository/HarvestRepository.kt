package com.example.madhumargaagriculture.data.repository

import com.example.madhumargaagriculture.data.dao.HarvestDao
import com.example.madhumargaagriculture.data.entity.HarvestEntity
import kotlinx.coroutines.flow.Flow

class HarvestRepository(private val harvestDao: HarvestDao) {

    val allHarvests: Flow<List<HarvestEntity>> = harvestDao.getAllHarvests()

    suspend fun insertHarvest(harvest: HarvestEntity) {
        harvestDao.insertHarvest(harvest)
    }
}
