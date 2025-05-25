package com.example.goalfit.core.entity

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "progreso",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["usuarioID"],
            childColumns = ["usuarioID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RutinaEntity::class,
            parentColumns = ["idRutina"],
            childColumns = ["idRutina"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioID"), Index("idRutina")]
)
data class ProgresoEntity(
    @PrimaryKey(autoGenerate = true) val idProgreso: Long = 0,
    val usuarioID: String,
    val idRutina: Int,           // ← ahora Int
    val fecha: Date,
    val duracionTotal: Double,
    val ejerciciosCompletados: String
)
