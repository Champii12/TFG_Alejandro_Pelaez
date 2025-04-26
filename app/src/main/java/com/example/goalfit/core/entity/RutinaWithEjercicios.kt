package com.example.goalfit.core.entity

import androidx.room.*

@JvmSuppressWildcards
data class RutinaWithEjercicios(
    @Embedded val rutina: RutinaEntity,
    @Relation(
        parentColumn = "idRutina",
        entityColumn = "idEjercicio",
        associateBy = Junction(
            value = RutinaEjercicioEntity::class,
            parentColumn = "idRutina",
            entityColumn = "idEjercicio"
        )
    )
    val ejercicios: List<EjercicioEntity>
)
