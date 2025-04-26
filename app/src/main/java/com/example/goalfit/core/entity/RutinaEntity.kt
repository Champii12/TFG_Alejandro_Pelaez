package com.example.goalfit.core.entity

import androidx.room.*
import androidx.room.ForeignKey
import java.util.Date

@Entity ( tableName = "rutinas",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["usuarioID"],
            childColumns = ["usuarioID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index ("usuarioID")]
)
data class RutinaEntity(
    @PrimaryKey (autoGenerate = true)
    val idRutina: Int  = 0,
    val usuarioID: String,
    val nombreRutina: String,
    val fechaCreacion: Date,
)
