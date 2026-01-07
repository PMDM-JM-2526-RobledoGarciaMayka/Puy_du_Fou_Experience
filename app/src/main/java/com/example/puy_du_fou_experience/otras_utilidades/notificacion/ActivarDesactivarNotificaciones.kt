package com.example.puy_du_fou_experience.otras_utilidades.notificacion

import android.content.Context
import android.content.SharedPreferences


class ActivarDesactivarNotificaciones(context: Context) {

    // SharedPreferences para almacenar el estado de las notificaciones
    private val prefs: SharedPreferences =
        context.getSharedPreferences("notificaciones_prefs", Context.MODE_PRIVATE)


    companion object {
        private const val KEY_NOTIFICACIONES_HABILITADAS = "notificaciones_habilitadas"
    }

    //Verifica si las notificaciones están activadas.
    //Por defecto, las notificaciones están habilitadas (true) si no se ha configurado previamente.
    fun activadas(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICACIONES_HABILITADAS, true)
    }

    //Establece el estado de activación de las notificaciones.
    //Guarda la preferencia del usuario para futuros accesos.
    fun setActivadas(habilitadas: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICACIONES_HABILITADAS, habilitadas).apply()
    }
}