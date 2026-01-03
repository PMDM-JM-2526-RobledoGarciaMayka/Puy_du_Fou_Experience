package com.example.puy_du_fou_experience.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.puy_du_fou_experience.model.Espectaculo


@Dao
interface EspectaculosDAO {

    @Insert
    suspend fun insert(espectaculo: List<Espectaculo>)

    @Query("SELECT * FROM espectaculo ORDER BY id ASC")
    suspend fun getAllEspectaculos(): List<Espectaculo>

    @Query("SELECT * FROM espectaculo WHERE titulo = :titulo")
    suspend fun getEspectaculoporTitulo(titulo: String): Espectaculo
}