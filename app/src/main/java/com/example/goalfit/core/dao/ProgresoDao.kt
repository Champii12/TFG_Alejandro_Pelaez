package com.example.goalfit.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalfit.core.entity.ProgresoEntity

@Dao
interface ProgresoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progreso: ProgresoEntity)

    @Query("SELECT * FROM progreso WHERE usuarioID = :uid")
    suspend fun getAll(uid: String): List<ProgresoEntity>
}