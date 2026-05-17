package com.example.madhumargaagriculture.data.repository

import com.example.madhumargaagriculture.data.dao.LogDao
import com.example.madhumargaagriculture.data.entity.InspectionLog
import kotlinx.coroutines.flow.Flow

class LogRepository(private val dao: LogDao) {

    fun getLogs(): Flow<List<InspectionLog>> = dao.getLogs()

    suspend fun insertLog(log: InspectionLog) {
        dao.insert(log)
    }

    suspend fun updateLog(log: InspectionLog) {
        dao.updateLog(log)
    }

    suspend fun deleteLog(log: InspectionLog) {
        dao.deleteLog(log)
    }
}
