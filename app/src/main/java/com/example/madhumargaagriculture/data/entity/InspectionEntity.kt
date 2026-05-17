package com.example.madhumargaagriculture.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspections")
data class InspectionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val hiveId: Int,
    val queenSeen: Boolean,
    val pestsSeen: Boolean,
    val honeyFlow: String,
    val colonyLevel: String,
    val notes: String,
    val suggestion: String
)
