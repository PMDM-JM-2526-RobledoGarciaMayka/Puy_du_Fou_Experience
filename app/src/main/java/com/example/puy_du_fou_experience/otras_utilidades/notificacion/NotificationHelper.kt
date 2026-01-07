package com.example.puy_du_fou_experience.otras_utilidades.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


object NotificationHelper {

    const val CHANNEL_ID = "espectaculos_channel"
    private const val CHANNEL_NAME = "Espectáculos"
    private const val CHANNEL_DESC = "Notificaciones de espectáculos"


    //Crea el canal de notificación para los espectáculos.
    fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Configura la importancia del canal como ALTA para asegurar que las notificaciones sean visibles
            val importancia = NotificationManager.IMPORTANCE_HIGH
            //Crea el canal de notificación con su ID, nombre e importancia
            val canal = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importancia).apply {
                description = CHANNEL_DESC
            }

            //Obtiene el servicio de notificaciones y registra el canal
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}