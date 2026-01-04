package com.example.puy_du_fou_experience.notificacion

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object NotificationSchedule {

    @SuppressLint("ScheduleExactAlarm")
    fun programarNotificacion(
        context: Context,
        tituloEspectaculo: String,
        horario: String,
        tiempoEnMillis: Long
    ) {
        val intent = Intent(context, Notificacion::class.java).apply {
            putExtra("titulo", tituloEspectaculo)
            putExtra("horario", horario)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tituloEspectaculo.hashCode(), // ID único por espectáculo
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                tiempoEnMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                tiempoEnMillis,
                pendingIntent
            )
        }
    }

    fun programarNotificacionPrueba(context: Context, segundos: Int = 5) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.SECOND, segundos)
        }

        programarNotificacion(
            context,
            "El Último Cantar",
            "15:00",
            calendar.timeInMillis
        )
    }
}