package com.example.madhumargaagriculture.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "harvests")
data class HarvestEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val hiveId: Int,
    val quantity: Double,
    val date: String,
    val honeyType: String
)
