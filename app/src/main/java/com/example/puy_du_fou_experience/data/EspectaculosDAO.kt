package com.example.puy_du_fou_experience.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.puy_du_fou_experience.model.Espectaculo


@Dao
interface EspectaculosDAO {

    //Inserta una lista de espectáculos en la base de datos.
    @Insert
    suspend fun insert(espectaculo: List<Espectaculo>)

    //Obtiene todos los espectáculos de la base de datos ordenados por ID ascendente.
    @Query("SELECT * FROM espectaculo ORDER BY id ASC")
    suspend fun getAllEspectaculos(): List<Espectaculo>

    //Busca un espectáculo específico por su título.
    @Query("SELECT * FROM espectaculo WHERE titulo = :titulo")
    suspend fun getEspectaculoporTitulo(titulo: String): Espectaculo

    //Busca un espectáculo específico por su ID.
    @Query("SELECT * FROM espectaculo WHERE id = :id")
    suspend fun getEspectaculoporId(id: Int): Espectaculo
}