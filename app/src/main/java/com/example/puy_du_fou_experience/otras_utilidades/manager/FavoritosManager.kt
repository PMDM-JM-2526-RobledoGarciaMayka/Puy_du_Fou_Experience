package com.example.puy_du_fou_experience.otras_utilidades.manager

import android.content.Context

class FavoritosManager (context: Context) {
    private val prefs = context.getSharedPreferences(
        "favoritos_prefs",
        Context.MODE_PRIVATE
    )

    private val KEY_FAVORITOS = "favoritos"

    fun obtenerFavoritos(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITOS, emptySet()) ?: emptySet()
    }

    fun esFavorito(nombre: String): Boolean {
        return obtenerFavoritos().contains(nombre)
    }

    fun agregarFavorito(titulo: String) {
        val favoritos = obtenerFavoritos().toMutableSet()
        favoritos.add(titulo)
        prefs.edit().putStringSet(KEY_FAVORITOS, favoritos).apply()
    }

    fun quitarFavorito(titulo: String) {
        val favoritos = obtenerFavoritos().toMutableSet()
        favoritos.remove(titulo)
        prefs.edit().putStringSet(KEY_FAVORITOS, favoritos).apply()
    }

    fun toggleFavorito(titulo: String) {
        if (esFavorito(titulo)) {
            quitarFavorito(titulo)
        } else {
            agregarFavorito(titulo)
        }
    }
}