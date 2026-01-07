package com.example.puy_du_fou_experience.otras_utilidades.notificacion

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.view.navegacion_menu.MenuActivity


class Notificacion : BroadcastReceiver() {

    companion object {
        private const val NOTIFICATION_ID = 1
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        //Extrae el título del espectáculo
        val tituloEspectaculo = intent?.getStringExtra("titulo") ?: "Espectáculo"
        //Extrae el horario del espectáculo
        val horario = intent?.getStringExtra("horario") ?: ""

        //Obtiene el servicio de notificaciones del sistema
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Crea un intent para abrir la aplicación cuando el usuario toque la notificación
        val intentAbrir = Intent(context, MenuActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intentAbrir,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Contenido de la notificación y configuración apropiada
        val notificacion = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setContentTitle("¡$tituloEspectaculo va a comenzar en 15 minutos!")
            .setContentText("El espectáculo comienza a las $horario")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        notificationManager.notify(NOTIFICATION_ID, notificacion)
    }
}

