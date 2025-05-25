// RunRutinaViewModel.kt
package com.example.goalfit.core.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.goalfit.core.AppDatabase
import com.example.goalfit.core.data.RoutineRepository
import com.example.goalfit.core.entity.EjercicioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

data class EjercicioRun(
    val id: Long,
    val nombre: String,
    val duracionSegundos: Int,
    val gifUrl: String?,
    val series: Int?,
    val repeticiones: Int?
)

class RunRutinaViewModel(
    application: Application,
    private val rutinaId: Int,
    private val repo: RoutineRepository
) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    // Lista de EjercicioRun: duracion en segundos (1min=60s si null)
    private val _ejerciciosConDuracion = MutableStateFlow<List<EjercicioRun>>(emptyList())
    val ejerciciosConDuracion: StateFlow<List<EjercicioRun>> = _ejerciciosConDuracion

    init {
        viewModelScope.launch {
            // 1) Obtén la lista original
            val originales = db.rutinaDao()
                .getEjerciciosForRutina(rutinaId)
                .first()
                .map { e ->
                    val rel = db.rutinaDao().getRelacion(rutinaId, e.idEjercicio.toInt())
                    val segundos = (rel?.duracion?.times(60))?.toInt() ?: 60
                    EjercicioRun(
                        id               = e.idEjercicio,
                        nombre           = e.nombre,
                        duracionSegundos = segundos,
                        gifUrl           = e.gifUrl,
                        series           = rel?.series,
                        repeticiones     = rel?.repeticiones
                    )
                }

            // 4) Exponer la lista completa al UI
            _ejerciciosConDuracion.value = originales
        }
    }

    /** Llama a repo.logProgress con la lista de IDs */
    fun registrarProgreso(onComplete: () -> Unit) {
        viewModelScope.launch {
            val fsId = db.rutinaDao().getById(rutinaId)?.firestoreId
                ?: return@launch
            repo.logProgress(
                rutinaFirestoreId = fsId,
                duracionTotal     = ejerciciosConDuracion.value.sumOf { it.duracionSegundos.toDouble() / 60.0 },
                ejerciciosIds    = ejerciciosConDuracion.value.map { it.id }
            )
            onComplete()
        }
    }
}

class RunRutinaViewModelFactory(
    private val app: Application,
    private val rutinaId: Int,
    private val repo: RoutineRepository
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunRutinaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RunRutinaViewModel(app, rutinaId, repo) as T
        }
        throw IllegalArgumentException("Unknown VM")
    }
}
