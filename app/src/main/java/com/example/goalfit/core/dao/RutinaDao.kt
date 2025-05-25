// RutinaDao.kt
package com.example.goalfit.core.dao

import androidx.room.*
import com.example.goalfit.core.entity.EjercicioEntity
import com.example.goalfit.core.entity.RutinaEntity
import com.example.goalfit.core.entity.RutinaEjercicioEntity
import com.example.goalfit.core.entity.RutinaWithEjercicios
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutina(rutina: RutinaEntity)

    /** Inserta varias rutinas en batch */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rutinas: List<RutinaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEjerciciosToRutina(refs: List<RutinaEjercicioEntity>)

    /** Lectura única */
    @Transaction
    @Query("SELECT * FROM rutinas WHERE usuarioID = :uid")
    suspend fun getRutinasConEjercicios(uid: String): List<RutinaWithEjercicios>

    @Query("SELECT * FROM rutinas WHERE firestoreId = :fsId")
    suspend fun getByFirestoreId(fsId: String): RutinaEntity?

    @Transaction
    @Query("SELECT * FROM rutinas WHERE usuarioID = :uid")
    fun getRutinasConEjerciciosFlow(uid: String): Flow<List<RutinaWithEjercicios>>

    @Transaction
    @Query("""
  SELECT e.* FROM ejercicios AS e
    INNER JOIN rutinas_ejercicios AS re
      ON e.idEjercicio = re.idEjercicio
  WHERE re.idRutina = :rutinaId
""")
    fun getEjerciciosForRutina(rutinaId: Int): Flow<List<EjercicioEntity>>

    @Query("SELECT COUNT(*) FROM rutinas_ejercicios WHERE idRutina = :rutinaId")
    suspend fun countEjerciciosForRutina(rutinaId: Int): Int

    @Query("SELECT * FROM rutinas_ejercicios WHERE idRutina=:rid AND idEjercicio=:eid")
    suspend fun getRelacion(rid: Int, eid: Int): RutinaEjercicioEntity?

    @Query("SELECT * FROM rutinas WHERE idRutina=:rid")
    suspend fun getById(rid: Int): RutinaEntity?

    @Delete
    suspend fun deleteRutina(rutina: RutinaEntity)
}
