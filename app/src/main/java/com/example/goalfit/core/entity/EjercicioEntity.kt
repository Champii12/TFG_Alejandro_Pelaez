package com.example.goalfit.core.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicios")
data class EjercicioEntity(
    @PrimaryKey(autoGenerate = true) val idEjercicio: Long = 0,
    val nombre: String,
    val categoria: String? = null,
    val descripcion: String? = null,
    val gifUrl: String? = null
)
