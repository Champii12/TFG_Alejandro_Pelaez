package com.example.goalfit.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalfit.core.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertarUsuario(user: UserEntity)

    @Query("SELECT * FROM users WHERE usuarioID = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE usuarioID = :id")
    fun getByIdFlow(id: String): kotlinx.coroutines.flow.Flow<UserEntity?>

}