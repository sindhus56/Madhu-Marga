package com.example.madhumargaagriculture.data.repository

import com.example.madhumargaagriculture.data.dao.InspectionDao
import com.example.madhumargaagriculture.data.entity.InspectionEntity
import kotlinx.coroutines.flow.Flow

class InspectionRepository(private val inspectionDao: InspectionDao) {

    val allInspections: Flow<List<InspectionEntity>> = inspectionDao.getAllInspections()

    suspend fun insertInspection(inspection: InspectionEntity) {
        inspectionDao.insertInspection(inspection)
    }
}
