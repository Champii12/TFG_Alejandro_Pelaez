package com.example.goalfit.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.goalfit.core.entity.RutinaEjercicioEntity
import com.example.goalfit.core.entity.RutinaEntity
import com.example.goalfit.core.entity.RutinaWithEjercicios

@Dao
interface RutinaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutina(rutina: RutinaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEjerciciosToRutina(refs: List<RutinaEjercicioEntity>)

    @Transaction
    @Query("SELECT * FROM rutinas WHERE usuarioID = :uid")
    suspend fun getRutinasConEjercicios(uid: String): List<RutinaWithEjercicios>

}