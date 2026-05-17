package com.example.madhumargaagriculture.data.repository

import com.example.madhumargaagriculture.data.dao.HiveDao
import com.example.madhumargaagriculture.data.entity.HiveEntity

class HiveRepository(
    private val hiveDao: HiveDao
) {

    val allHives = hiveDao.getAllHives()

    suspend fun insertHive(hive: HiveEntity) {
        hiveDao.insertHive(hive)
    }

    suspend fun updateHive(hive: HiveEntity) {
        hiveDao.updateHive(hive)
    }

    suspend fun deleteHive(hive: HiveEntity) {
        hiveDao.deleteHive(hive)
    }
}
