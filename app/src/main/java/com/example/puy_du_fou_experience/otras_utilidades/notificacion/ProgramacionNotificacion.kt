package com.example.puy_du_fou_experience.otras_utilidades.notificacion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast

object ProgramacionNotificacion {

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

        val requestCode = tituloEspectaculo.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val puedeUsarAlarmasExactas = alarmManager.canScheduleExactAlarms()

                if (!puedeUsarAlarmasExactas) {
                    val intentConfig = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intentConfig.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intentConfig)

                    Toast.makeText(
                        context,
                        "Activa 'Alarmas y recordatorios' para recibir notificaciones",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }

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

        } catch (e: SecurityException) {
            Toast.makeText(
                context,
                "Error: No se puede programar la alarma",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}