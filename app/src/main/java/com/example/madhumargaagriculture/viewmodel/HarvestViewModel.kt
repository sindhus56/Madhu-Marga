package com.example.madhumargaagriculture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.madhumargaagriculture.data.database.AppDatabase
import com.example.madhumargaagriculture.data.entity.HarvestEntity
import com.example.madhumargaagriculture.data.repository.HarvestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HarvestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HarvestRepository
    val allHarvests: Flow<List<HarvestEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).harvestDao()
        repository = HarvestRepository(dao)
        allHarvests = repository.allHarvests
    }

    fun insertHarvest(harvest: HarvestEntity) = viewModelScope.launch {
        repository.insertHarvest(harvest)
    }
}
