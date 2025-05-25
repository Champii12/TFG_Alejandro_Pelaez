// EjercicioDao.kt
package com.example.goalfit.core.dao

import androidx.room.*
import com.example.goalfit.core.entity.EjercicioEntity

@Dao
interface EjercicioDao {
    /** Inserta uno o varios ejercicios individuales */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEjercicio(ejercicio: EjercicioEntity): Long

    /** Inserta una lista completa de ejercicios en batch */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ejercicios: List<EjercicioEntity>)

    @Query("SELECT * FROM ejercicios")
    suspend fun getAll(): List<EjercicioEntity>

    @Query("SELECT * FROM ejercicios WHERE idEjercicio = :id")
    suspend fun getById(id: Long): EjercicioEntity?

    @Query("""
    SELECT * 
      FROM ejercicios 
     WHERE LOWER(nombre) = LOWER(:nombre) 
     LIMIT 1
  """)
    suspend fun findByName(nombre: String): EjercicioEntity?

}
