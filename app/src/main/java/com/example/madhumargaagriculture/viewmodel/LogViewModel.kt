package com.example.madhumargaagriculture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madhumargaagriculture.data.entity.InspectionLog
import com.example.madhumargaagriculture.data.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LogViewModel(private val repository: LogRepository) : ViewModel() {

    val logs: Flow<List<InspectionLog>> = repository.getLogs()

    fun insertLog(log: InspectionLog) {
        viewModelScope.launch {
            repository.insertLog(log)
        }
    }

    fun updateLog(log: InspectionLog) {
        viewModelScope.launch {
            repository.updateLog(log)
        }
    }

    fun deleteLog(log: InspectionLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}
