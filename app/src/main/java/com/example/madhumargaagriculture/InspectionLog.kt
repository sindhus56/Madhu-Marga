package com.example.madhumargaagriculture

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspection_logs")
data class InspectionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val logText: String,
    val timestamp: Long
)
