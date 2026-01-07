package com.example.puy_du_fou_experience.otras_utilidades.manager

import android.content.Context


class FavoritosManager (context: Context) {

    // SharedPreferences para almacenar los favoritos de forma persistente
    private val prefs = context.getSharedPreferences(
        "favoritos_prefs",
        Context.MODE_PRIVATE
    )

    private val KEY_FAVORITOS = "favoritos"

    //Obtiene el conjunto de títulos de espectáculos marcados como favoritos.
    fun obtenerFavoritos(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITOS, emptySet()) ?: emptySet()
    }

    //Verifica si un espectáculo está marcado como favorito.
    fun esFavorito(nombre: String): Boolean {
        return obtenerFavoritos().contains(nombre)
    }

    //Agrega un espectáculo a la lista de favoritos.
    //Si ya existe, no se duplica.
    fun agregarFavorito(titulo: String) {
        val favoritos = obtenerFavoritos().toMutableSet()
        favoritos.add(titulo)
        prefs.edit().putStringSet(KEY_FAVORITOS, favoritos).apply()
    }

    //Elimina un espectáculo de la lista de favoritos.
    fun quitarFavorito(titulo: String) {
        val favoritos = obtenerFavoritos().toMutableSet()
        favoritos.remove(titulo)
        prefs.edit().putStringSet(KEY_FAVORITOS, favoritos).apply()
    }

    //Alterna el estado de favorito de un espectáculo.
    fun toggleFavorito(titulo: String) {
        if (esFavorito(titulo)) {
            quitarFavorito(titulo)
        } else {
            agregarFavorito(titulo)
        }
    }
}