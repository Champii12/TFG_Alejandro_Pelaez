package com.example.goalfit.core.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val usuarioID: String,
    val nombre: String,
    val edad: Int,
    val peso: Double,
    val altura: Double,
    val nivelExperiencia: String,
    val objetivo: String
)