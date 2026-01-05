package com.example.puy_du_fou_experience.otras_utilidades.notificacion

import android.content.Context
import android.content.SharedPreferences


class ActivarDesactivarNotificaciones(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("notificaciones_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NOTIFICACIONES_HABILITADAS = "notificaciones_habilitadas"
    }

    fun activadas(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICACIONES_HABILITADAS, true)
    }

    fun setActivadas(habilitadas: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICACIONES_HABILITADAS, habilitadas).apply()
    }
}