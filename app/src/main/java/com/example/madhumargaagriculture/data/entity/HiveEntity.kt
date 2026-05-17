package com.example.madhumargaagriculture.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hives")
data class HiveEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val hiveName: String,
    val location: String,
    val queenStatus: String,
    val hiveStrength: String,
    val notes: String
)
