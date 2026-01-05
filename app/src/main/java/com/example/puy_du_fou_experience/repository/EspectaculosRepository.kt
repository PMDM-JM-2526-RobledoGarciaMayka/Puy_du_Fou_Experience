package com.example.puy_du_fou_experience.repository

import com.example.puy_du_fou_experience.data.EspectaculosDAO
import com.example.puy_du_fou_experience.model.Espectaculo


class EspectaculosRepository (private val espectaculosDAO: EspectaculosDAO){

    suspend fun insert(espectaculo: List<Espectaculo>) {
        espectaculosDAO.insert(espectaculo)
    }

    suspend fun getAllEspectaculos(): List<Espectaculo> {
        return espectaculosDAO.getAllEspectaculos()
    }

    suspend fun getEspectaculoporTitulo(titulo: String): Espectaculo {
        return espectaculosDAO.getEspectaculoporTitulo(titulo)
    }
}