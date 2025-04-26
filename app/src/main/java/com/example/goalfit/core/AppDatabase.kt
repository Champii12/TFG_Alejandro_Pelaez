package com.example.goalfit.core

import android.content.Context
import androidx.room.*
import com.example.goalfit.core.dao.*
import com.example.goalfit.core.entity.*
import com.example.goalfit.core.data.Converters

@Database(
    entities = [
        UserEntity::class,
        EjercicioEntity::class,
        RutinaEntity::class,
        RutinaEjercicioEntity::class,
        ProgresoEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun progresoDao(): ProgresoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goalfit-db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}
