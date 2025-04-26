package com.example.goalfit.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalfit.core.entity.EjercicioEntity

@Dao
interface EjercicioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg ejercicios: EjercicioEntity)

    @Query("SELECT * FROM ejercicios")
    suspend fun getAll(): List<EjercicioEntity>
}