package com.example.madhumargaagriculture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.madhumargaagriculture.data.database.AppDatabase
import com.example.madhumargaagriculture.data.entity.HiveEntity
import com.example.madhumargaagriculture.data.repository.HiveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HiveViewModel(application: Application)
    : AndroidViewModel(application) {

    private val repository: HiveRepository

    val allHives: Flow<List<HiveEntity>>

    init {

        val dao = AppDatabase
            .getDatabase(application)
            .hiveDao()

        repository = HiveRepository(dao)

        allHives = repository.allHives
    }

    fun insertHive(hive: HiveEntity) =
        viewModelScope.launch {
            repository.insertHive(hive)
        }

    fun updateHive(hive: HiveEntity) =
        viewModelScope.launch {
            repository.updateHive(hive)
        }

    fun deleteHive(hive: HiveEntity) =
        viewModelScope.launch {
            repository.deleteHive(hive)
        }
}
