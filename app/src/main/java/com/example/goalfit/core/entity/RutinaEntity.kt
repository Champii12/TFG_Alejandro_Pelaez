package com.example.goalfit.core.entity

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "rutinas",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["usuarioID"],
            childColumns = ["usuarioID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("firestoreId", unique = true)]
)
data class RutinaEntity(
    @PrimaryKey(autoGenerate = true)
    val idRutina: Int = 0,           // INT, no String
    val firestoreId: String,         // para Firestore
    val usuarioID: String,
    val nombreRutina: String,
    val fechaCreacion: Date
)
