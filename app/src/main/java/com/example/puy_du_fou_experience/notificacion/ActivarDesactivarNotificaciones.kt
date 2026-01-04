package com.example.puy_du_fou_experience.notificacion

import android.content.Context

class ActivarDesactivarNotificaciones(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("notificaciones_prefs", Context.MODE_PRIVATE)
    companion object {
        private const val NOTIFICACIONES_ACTIVADAS = "notificaciones_activadas"
    }

    fun activadas(): Boolean {
        return sharedPreferences.getBoolean(NOTIFICACIONES_ACTIVADAS, true)
    }

    fun setActivadas(activadas: Boolean) {
        sharedPreferences.edit().putBoolean(NOTIFICACIONES_ACTIVADAS, false).apply()
    }


}