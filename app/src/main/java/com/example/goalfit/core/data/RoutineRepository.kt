package com.example.goalfit.core.data

import android.util.Log
import com.example.goalfit.core.AppDatabase
import com.example.goalfit.core.entity.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class RoutineRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val localDb: AppDatabase
) {
    private val userDoc = firestore
        .collection("usuarios")
        .document(auth.currentUser!!.uid)

    suspend fun ensureUserLocal() {
        auth.currentUser?.let { fbUser ->
            val existing = localDb.userDao().getById(fbUser.uid)
            if (existing == null) {
                val snapshot = userDoc.get().await()
                val altura           = snapshot.getDouble("altura") ?: 0.0
                val edad             = snapshot.getLong("edad")?.toInt() ?: 0
                val nivelExperiencia = snapshot.getString("nivelExperiencia") ?: ""
                val objetivo         = snapshot.getString("objetivo") ?: ""
                val peso             = snapshot.getDouble("peso") ?: 0.0

                val userEntity = UserEntity(
                    usuarioID        = fbUser.uid,
                    nombre           = fbUser.displayName.orEmpty(),
                    altura           = altura,
                    edad             = edad,
                    nivelExperiencia = nivelExperiencia,
                    objetivo         = objetivo,
                    peso             = peso
                )
                localDb.userDao().insertarUsuario(userEntity)
            }
        }
    }

    /**
     * Crea la rutina en Firestore y luego la inserta en Room.
     * Devuelve el ID generado en Firestore.
     */
    suspend fun createRoutine(nombre: String, fechaCreacion: Date): String {
        ensureUserLocal()
        val ref = userDoc.collection("rutinas")
            .add(mapOf(
                "nombreRutina" to nombre,
                "fechaCreacion" to fechaCreacion
            ))
            .await()

        val rutina = RutinaEntity(
            firestoreId   = ref.id,
            usuarioID     = auth.currentUser!!.uid,
            nombreRutina  = nombre,
            fechaCreacion = fechaCreacion
        )
        localDb.rutinaDao().insertRutina(rutina)
        return ref.id
    }

    /**
     * Añade un ejercicio con relación, evitando duplicados por nombre.
     */
    suspend fun addEjercicioConRelacion(
        rutinaFirestoreId: String,
        ejercicioEntity: EjercicioEntity,
        series: Int,
        repeticiones: Int,
        duracion: Double?
    ) {
        ensureUserLocal()

        val dao = localDb.ejercicioDao()

        // 1) Intentamos encontrar un ejercicio existente (case-insensitive)
        val existente = dao.findByName(ejercicioEntity.nombre)

        // 2) Si existe, actualizamos categoría/descr/gif con REPLACE; si no, insertamos IGNORE para obtener ID
        val ejercicioLocalId = if (existente != null) {
            // Construimos la entidad actualizada
            val actualizado = existente.copy(
                categoria   = ejercicioEntity.categoria   ?: existente.categoria,
                descripcion = ejercicioEntity.descripcion ?: existente.descripcion,
                gifUrl      = ejercicioEntity.gifUrl      ?: existente.gifUrl
            )
            // insertAll con REPLACE para actualizar
            dao.insertAll(listOf(actualizado))
            existente.idEjercicio   // reutilizamos el id original
        } else {
            // insertEjercicio con IGNORE para no duplicar
            dao.insertEjercicio(ejercicioEntity)
        }

        // 3) Guardamos en Firestore los datos del ejercicio en la subcolección
        userDoc.collection("rutinas")
            .document(rutinaFirestoreId)
            .collection("ejercicios")
            .add(mapOf(
                "nombre"       to ejercicioEntity.nombre,
                "categoria"    to ejercicioEntity.categoria,
                "descripcion"  to ejercicioEntity.descripcion,
                "series"       to series,
                "repeticiones" to repeticiones,
                "duracion"     to duracion
            ))
            .await()

        // 4) Luego persistes la relación Rutina–Ejercicio en Room
        val rutinaLocal = localDb.rutinaDao()
            .getByFirestoreId(rutinaFirestoreId)
            ?: throw IllegalStateException("Rutina local no encontrada para $rutinaFirestoreId")

        val crossRef = RutinaEjercicioEntity(
            idRutina     = rutinaLocal.idRutina,
            idEjercicio  = ejercicioLocalId.toInt(),
            series       = series,
            repeticiones = repeticiones,
            duracion     = duracion
        )
        localDb.rutinaDao().addEjerciciosToRutina(listOf(crossRef))

        // 5) Log para verificar
        val count = localDb.rutinaDao()
            .countEjerciciosForRutina(rutinaLocal.idRutina)
        Log.d("Repo", "Ahora hay $count ejercicio(s) para rutina ${rutinaLocal.idRutina}")
    }

    /**
     * Registra el progreso de una rutina (Firestore + Room).
     */
    suspend fun logProgress(
        rutinaFirestoreId: String,
        duracionTotal: Double,
        ejerciciosIds: List<Long>
    ) {
        ensureUserLocal()
        // Firestore
        userDoc.collection("progreso")
            .document(rutinaFirestoreId)
            .set(mapOf(
                "duracionTotal"         to duracionTotal,
                "ejerciciosCompletados" to ejerciciosIds
            ))
            .await()

        // Room
        val rutinaLocal = localDb.rutinaDao()
            .getByFirestoreId(rutinaFirestoreId)
            ?: throw IllegalStateException("Rutina local no encontrada para $rutinaFirestoreId")

        val progreso = ProgresoEntity(
            usuarioID             = auth.currentUser!!.uid,
            idRutina              = rutinaLocal.idRutina,
            fecha                 = Date(),
            duracionTotal         = duracionTotal,
            ejerciciosCompletados = ejerciciosIds.joinToString(",")
        )
        localDb.progresoDao().insert(progreso)
    }

    // (Puedes mantener aquí también addExerciseToRoutine si lo necesitas para otros flujos)
}
