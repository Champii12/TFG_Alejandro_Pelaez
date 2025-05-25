package com.example.goalfit.core.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalfit.core.data.RoutineRepository
import com.example.goalfit.core.entity.EjercicioEntity
import com.example.goalfit.screen.crearRutina.UiEjercicio
import kotlinx.coroutines.launch
import java.util.Date

class RoutineViewModel(
    private val repo: RoutineRepository
) : ViewModel() {

    /**
     * Crea la rutina y luego añade todos los ejercicios de la lista.
     * onComplete() se invoca al terminar.
     */
    fun crearRutinaConEjercicios(
        nombreRutina: String,
        ejerciciosInput: List<UiEjercicio>,
        fechaCreacion: Date,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            Log.d("VM", "→ crearRutinaConEjercicios nombre=$nombreRutina, ejercicios=${ejerciciosInput.size}")
            // 1) Crea la rutina y obtén el ID de Firestore
            val fsId = repo.createRoutine(nombreRutina, fechaCreacion)

            // 2) Por cada UiEjercicio, construye el entity y llama al repo
            ejerciciosInput.forEach { ui ->
                Log.d("VM", "   • procesando UiEjercicio: $ui")
                val series       = ui.series.toIntOrNull() ?: 0
                val repeticiones = ui.repeticiones.toIntOrNull() ?: 0

                val ejercicioEntity = EjercicioEntity(
                    nombre      = ui.nombre,
                    categoria   = ui.categoria.ifBlank { null },
                    descripcion = ui.descripcion.ifBlank { null },
                    gifUrl      = null
                )

                // 3) Llamamos al método que inserta ejercicio + relación
                repo.addEjercicioConRelacion(
                    rutinaFirestoreId = fsId,
                    ejercicioEntity   = ejercicioEntity,
                    series            = series,
                    repeticiones      = repeticiones,
                    duracion          = null
                )
            }

            // 4) Callback para cerrar la pantalla
            onComplete()
        }
    }

    /**
     * Sólo crea la rutina (sin ejercicios).
     * onResult(fsId) te devuelve el ID de Firestore.
     */
    fun crearRutina(
        nombre: String,
        fecha: Date,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            val fsId = repo.createRoutine(nombre, fecha)
            onResult(fsId)
        }
    }

    /**
     * Añade un único ejercicio a una rutina ya existente.
     * Ahora reutiliza el mismo método de addEjercicioConRelacion;
     * si ya existe el ejercicio local, no lo duplicará.
     */
    fun agregarEjercicio(
        rutinaId: String,
        ejercicio: EjercicioEntity,
        series: Int,
        repeticiones: Int,
        duracion: Double?
    ) {
        viewModelScope.launch {
            repo.addEjercicioConRelacion(
                rutinaFirestoreId = rutinaId,
                ejercicioEntity   = ejercicio,
                series            = series,
                repeticiones      = repeticiones,
                duracion          = duracion
            )
        }
    }

    /**
     * Registra un progreso: convierte los IDs Int a Long y lo envía al repo.
     */
    fun registrarProgreso(
        rutinaId: String,
        durTotal: Double,
        ejerciciosIds: List<Int>
    ) {
        viewModelScope.launch {
            val ejerciciosLong = ejerciciosIds.map { it.toLong() }
            repo.logProgress(rutinaId, durTotal, ejerciciosLong)
        }
    }
}
