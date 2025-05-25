package com.example.goalfit.core.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RutinaDetailViewModelFactory(
    private val app: Application,
    private val rutinaId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RutinaDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RutinaDetailViewModel(app, rutinaId) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
