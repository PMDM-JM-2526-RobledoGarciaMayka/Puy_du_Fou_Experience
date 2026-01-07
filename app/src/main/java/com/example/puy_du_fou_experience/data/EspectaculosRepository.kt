package com.example.puy_du_fou_experience.data

import com.example.puy_du_fou_experience.model.Espectaculo

class EspectaculosRepository (private val espectaculosDAO: EspectaculosDAO){

    //Inserta una lista de espectáculos en la base de datos.
    suspend fun insert(espectaculo: List<Espectaculo>) {
        espectaculosDAO.insert(espectaculo)
    }

    //Recupera todos los espectáculos almacenados en la base de datos.
    suspend fun getAllEspectaculos(): List<Espectaculo> {
        return espectaculosDAO.getAllEspectaculos()
    }

    //Busca y recupera un espectáculo específico por su título.
    suspend fun getEspectaculoporTitulo(titulo: String): Espectaculo {
        return espectaculosDAO.getEspectaculoporTitulo(titulo)
    }
}