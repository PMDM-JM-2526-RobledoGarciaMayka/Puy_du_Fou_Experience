package com.example.puy_du_fou_experience.notificacion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar
import android.util.Log

object ProgramacionNotificacion {

    fun programarNotificacion(
        context: Context,
        tituloEspectaculo: String,
        horario: String,
        tiempoEnMillis: Long
    ) {
        Log.d("SCHEDULER", "=== PROGRAMANDO NOTIFICACIÓN ===")
        Log.d("SCHEDULER", "Título: $tituloEspectaculo")
        Log.d("SCHEDULER", "Horario: $horario")
        Log.d("SCHEDULER", "Tiempo en millis: $tiempoEnMillis")

        val intent = Intent(context, Notificacion::class.java).apply {
            putExtra("titulo", tituloEspectaculo)
            putExtra("horario", horario)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tituloEspectaculo.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tiempoEnMillis,
                    pendingIntent
                )
                Log.d("SCHEDULER", "Alarma programada con setExactAndAllowWhileIdle")
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    tiempoEnMillis,
                    pendingIntent
                )
                Log.d("SCHEDULER", "Alarma programada con setExact")
            }

            val calendar = Calendar.getInstance().apply { timeInMillis = tiempoEnMillis }
            Log.d("SCHEDULER", "Programado para: ${calendar.time}")
        } catch (e: Exception) {
            Log.e("SCHEDULER", "Error al programar alarma: ${e.message}")
            e.printStackTrace()
        }
    }

    fun programarNotificacionPrueba(context: Context, segundos: Int = 5) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.SECOND, segundos)
        }

        Log.d("SCHEDULER", "=== NOTIFICACIÓN DE PRUEBA ===")
        Log.d("SCHEDULER", "Se enviará en $segundos segundos")

        programarNotificacion(
            context,
            "Prueba de Notificación",
            "Ahora",
            calendar.timeInMillis
        )
    }
}