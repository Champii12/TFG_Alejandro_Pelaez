// app/src/main/java/com/example/goalfit/home/HomeViewModel.kt
package com.example.goalfit.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalfit.core.AppDatabase
import com.example.goalfit.core.entity.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class HomeViewModel(
    application: Application,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    /** 1) Flow de Rutinas con sus ejercicios (habitualmente vacío al inicio). */
    val rutinas =
        db.rutinaDao()
            .getRutinasConEjerciciosFlow(auth.currentUser!!.uid)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList<RutinaWithEjercicios>())

    /** 2) Flow de Progresos por usuario. */
    val progresos =
        db.progresoDao()
            .getAllFlow(auth.currentUser!!.uid)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList<ProgresoEntity>())

    /** Estado del menú lateral. */
    private val _menuExpanded = MutableStateFlow(false)
    val menuExpanded = _menuExpanded.asStateFlow()

    init {
        // Arrancamos la sincronización al crear el VM
        syncFromFirestore()
    }

    /** Sincroniza rutinas, ejercicios y progresos desde Firestore hacia Room. */
    private fun syncFromFirestore() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            // 1) Descargar todas las rutinas de Firestore
            val rutinasSnap = firestore
                .collection("usuarios").document(uid)
                .collection("rutinas")
                .get()
                .await()

            val rutinasFs = rutinasSnap.documents.mapNotNull { doc ->
                val nombre = doc.getString("nombreRutina") ?: return@mapNotNull null
                val fecha = doc.getDate("fechaCreacion") ?: return@mapNotNull null
                RutinaEntity(
                    firestoreId   = doc.id,
                    usuarioID     = uid,
                    nombreRutina  = nombre,
                    fechaCreacion = fecha
                )
            }

            // 2) Insertar todas las rutinas en Room
            withContext(Dispatchers.IO) {
                db.rutinaDao().insertAll(rutinasFs)
            }

            // 3) Para cada rutina, sincronizar sus ejercicios
            rutinasFs.forEach { rutinaFs ->
                // Obtener su id local
                val rutinaLocal = withContext(Dispatchers.IO) {
                    db.rutinaDao().getByFirestoreId(rutinaFs.firestoreId)
                } ?: return@forEach

                // Descargar subcolección ejercicios
                val ejerciciosSnap = firestore
                    .collection("usuarios").document(uid)
                    .collection("rutinas")
                    .document(rutinaFs.firestoreId)
                    .collection("ejercicios")
                    .get()
                    .await()

                // Mapear a las entidades Room y relaciones
                val crossRefs = mutableListOf<RutinaEjercicioEntity>()
                ejerciciosSnap.documents.forEach { eDoc ->
                    // Creamos el EjercicioEntity local (si ya existiera, Room lo REPLACE)
                    val nombreEj = eDoc.getString("nombre") ?: ""
                    val categoriaEj = eDoc.getString("categoria")
                    val descEj = eDoc.getString("descripcion")
                    val ejercicioEntity = EjercicioEntity(
                        nombre      = nombreEj,
                        categoria   = categoriaEj,
                        descripcion = descEj,
                        gifUrl      = eDoc.getString("gifUrl")
                    )

                    // Insertar ejercicio en Room
                    val ejercicioLocalId = withContext(Dispatchers.IO) {
                        db.ejercicioDao().insertEjercicio(ejercicioEntity)
                    }.toInt()

                    // Construir cross-ref
                    val series = (eDoc.getLong("series") ?: 0L).toInt()
                    val reps   = (eDoc.getLong("repeticiones") ?: 0L).toInt()
                    val dur    = eDoc.getDouble("duracion")

                    crossRefs += RutinaEjercicioEntity(
                        idRutina     = rutinaLocal.idRutina,
                        idEjercicio  = ejercicioLocalId,
                        series       = series,
                        repeticiones = reps,
                        duracion     = dur
                    )
                }

                // Insertar todas las relaciones en batch
                if (crossRefs.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        db.rutinaDao().addEjerciciosToRutina(crossRefs)
                    }
                }
            }

            // 4) Finalmente, sincronizar progresos
            val progSnap = firestore
                .collection("usuarios").document(uid)
                .collection("progreso")
                .get()
                .await()

            val progresosLocal = progSnap.documents.mapNotNull { doc ->
                // El ID de documento es el firestoreId de la rutina
                val rutinaLocal = db.rutinaDao().getByFirestoreId(doc.id) ?: return@mapNotNull null
                ProgresoEntity(
                    usuarioID             = uid,
                    idRutina              = rutinaLocal.idRutina,
                    fecha                 = doc.getDate("fecha") ?: Date(),
                    duracionTotal         = doc.getDouble("duracionTotal") ?: 0.0,
                    ejerciciosCompletados = (doc.get("ejerciciosCompletados") as? List<*>)?.joinToString(",")
                        ?: ""
                )
            }

            if (progresosLocal.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    db.progresoDao().insertAll(progresosLocal)
                }
            }
        }
    }

    fun toggleMenu() {
        _menuExpanded.value = !_menuExpanded.value
    }

    fun closeMenu() {
        _menuExpanded.value = false
    }

    /** Cuenta cuántas rutinas completaste este mes. */
    fun getRutinasCompletadasMes(progresos: List<ProgresoEntity>): Int {
        return progresos.count {
            val cal = Calendar.getInstance().apply { time = it.fecha }
            val now = Calendar.getInstance()
            cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
        }
    }


    /** Elimina la rutina tanto de Firestore como de Room */
    fun deleteRutina(rutinaId: Int) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            // 1) Recuperar la entidad local
            val rutinaLocal = withContext(Dispatchers.IO) {
                db.rutinaDao().getById(rutinaId)
            } ?: return@launch

            // 2) Borrar en Firestore
            firestore
                .collection("usuarios").document(uid)
                .collection("rutinas")
                .document(rutinaLocal.firestoreId)
                .delete()
                .await()

            // 3) Borrar en Room
            withContext(Dispatchers.IO) {
                db.rutinaDao().deleteRutina(rutinaLocal)
            }
        }
    }
}
