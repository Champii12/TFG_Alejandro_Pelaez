package com.example.goalfit.core.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "rutinas_ejercicios",
    primaryKeys = ["idRutina", "idEjercicio"],
    foreignKeys = [
        ForeignKey(
            entity = RutinaEntity::class,
            parentColumns = ["idRutina"],
            childColumns = ["idRutina"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EjercicioEntity::class,
            parentColumns = ["idEjercicio"],
            childColumns = ["idEjercicio"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("idRutina"),
        Index("idEjercicio")]
)
data class RutinaEjercicioEntity(
    val idRutina: Int,
    val idEjercicio: Int,
    val series: Int,
    val repeticiones: Int,
    val duracion: Double? // opcional en seg/minutos
)