package com.example.puy_du_fou_experience.notificacion

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.navegacion_menu.MenuActivity

class Notificacion : BroadcastReceiver() {

    companion object {
        private const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        // Obtener datos del Intent (opcional)
        val tituloEspectaculo = intent?.getStringExtra("titulo") ?: "Espectáculo"
        val horario = intent?.getStringExtra("horario") ?: ""

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intentAbrir = Intent(context, MenuActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intentAbrir,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setContentTitle("¡$tituloEspectaculo está por comenzar!")
            .setContentText("El espectáculo comienza a las $horario")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notificacion)
    }
}