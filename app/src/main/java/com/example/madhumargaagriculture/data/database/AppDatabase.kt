package com.example.madhumargaagriculture.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.madhumargaagriculture.data.dao.HarvestDao
import com.example.madhumargaagriculture.data.dao.HiveDao
import com.example.madhumargaagriculture.data.dao.InspectionDao
import com.example.madhumargaagriculture.data.dao.LogDao
import com.example.madhumargaagriculture.data.entity.HarvestEntity
import com.example.madhumargaagriculture.data.entity.HiveEntity
import com.example.madhumargaagriculture.data.entity.InspectionEntity
import com.example.madhumargaagriculture.data.entity.InspectionLog

@Database(
    entities = [
        HiveEntity::class,
        InspectionEntity::class,
        HarvestEntity::class,
        InspectionLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hiveDao(): HiveDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun harvestDao(): HarvestDao
    abstract fun logDao(): LogDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "madhu_marga_database"
                )
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
