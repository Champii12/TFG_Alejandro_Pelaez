// ProgresoDao.kt
package com.example.goalfit.core.dao

import androidx.room.*
import com.example.goalfit.core.entity.ProgresoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgresoDao {
    /** Inserta un único progreso */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progreso: ProgresoEntity)

    /** Inserta una lista de progresos de una sola vez */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progresos: List<ProgresoEntity>)

    /** Obtiene todos los progresos como lista (para llamadas suspending) */
    @Query("SELECT * FROM progreso WHERE usuarioID = :uid")
    suspend fun getAll(uid: String): List<ProgresoEntity>

    /** Observa los progresos como Flow para Compose/StateFlow */
    @Query("SELECT * FROM progreso WHERE usuarioID = :uid")
    fun getAllFlow(uid: String): Flow<List<ProgresoEntity>>
}
