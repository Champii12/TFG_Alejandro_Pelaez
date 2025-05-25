package com.example.goalfit.core.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalfit.core.AppDatabase
import com.example.goalfit.core.entity.EjercicioEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach       // <<-- AÑADE ESTO
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.onStart

class RutinaDetailViewModel(
    application: Application,
    private val rutinaId: Int
) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    val ejercicios: StateFlow<List<EjercicioEntity>> =
        db.rutinaDao()
            .getEjerciciosForRutina(rutinaId)
            .onStart { Log.d("DetailVM", "¡Se suscriben a ejercicios para rutina $rutinaId!") }
            .onEach  { Log.d("DetailVM", "Ejercicios para rutina $rutinaId → $it") }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )
}