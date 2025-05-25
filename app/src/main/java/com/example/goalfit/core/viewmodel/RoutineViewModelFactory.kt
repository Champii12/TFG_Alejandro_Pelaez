package com.example.goalfit.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.goalfit.core.data.RoutineRepository

class RoutineViewModelFactory(
    private val repository: RoutineRepository
) : ViewModelProvider.Factory {

    // 1) Implementación “vieja”
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    // 2) Nueva sobrecarga para Compose
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T = create(modelClass)
}
