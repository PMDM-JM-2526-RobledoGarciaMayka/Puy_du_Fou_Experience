package com.example.puy_du_fou_experience.otras_utilidades.notificacion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast


object ProgramacionNotificacion {

    //Programa una notificación para un espectáculo en un momento específico.
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

        //Genera un código único basado en el título para poder identificar y cancelar alarmas específicas
        val requestCode = tituloEspectaculo.hashCode()

        //PendingIntent que envuelve el intent para uso con AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //Obtiene el servicio de alarmas del sistema
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            //Permiso para utilizar alarmas exactas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val puedeUsarAlarmasExactas = alarmManager.canScheduleExactAlarms()

                // Si no tiene permiso, redirige al usuario a la configuración del sistema
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

            //Programar la alarma exacta
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

            //Excepción si faltan permisos
        } catch (e: SecurityException) {
            Toast.makeText(
                context,
                "Error: No se puede programar la alarma",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()

        }
    }
}