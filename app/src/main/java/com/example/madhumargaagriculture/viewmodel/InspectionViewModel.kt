package com.example.madhumargaagriculture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.madhumargaagriculture.data.database.AppDatabase
import com.example.madhumargaagriculture.data.entity.InspectionEntity
import com.example.madhumargaagriculture.data.repository.InspectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InspectionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InspectionRepository
    val allInspections: Flow<List<InspectionEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).inspectionDao()
        repository = InspectionRepository(dao)
        allInspections = repository.allInspections
    }

    fun insertInspection(inspection: InspectionEntity) = viewModelScope.launch {
        repository.insertInspection(inspection)
    }
}
