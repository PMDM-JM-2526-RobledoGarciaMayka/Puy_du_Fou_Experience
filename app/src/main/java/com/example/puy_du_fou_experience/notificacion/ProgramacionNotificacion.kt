package com.example.puy_du_fou_experience.notificacion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.util.Calendar

object ProgramacionNotificacion {

    fun programarNotificacion(
        context: Context,
        tituloEspectaculo: String,
        horario: String,
        tiempoEnMillis: Long
    ) {
        Log.d("PROGRAMACION", "========================================")
        Log.d("PROGRAMACION", "=== PROGRAMANDO NOTIFICACIÓN ===")
        Log.d("PROGRAMACION", "========================================")
        Log.d("PROGRAMACION", "Título: '$tituloEspectaculo'")
        Log.d("PROGRAMACION", "Horario: '$horario'")
        Log.d("PROGRAMACION", "Tiempo en millis: $tiempoEnMillis")

        val calendar = Calendar.getInstance().apply { timeInMillis = tiempoEnMillis }
        Log.d("PROGRAMACION", "Fecha completa: ${calendar.time}")

        val intent = Intent(context, Notificacion::class.java).apply {
            putExtra("titulo", tituloEspectaculo)
            putExtra("horario", horario)
        }

        val requestCode = tituloEspectaculo.hashCode()
        Log.d("PROGRAMACION", "Request Code: $requestCode")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            // VERIFICAR PERMISO DE ALARMAS EXACTAS EN ANDROID 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val puedeUsarAlarmasExactas = alarmManager.canScheduleExactAlarms()
                Log.d("PROGRAMACION", "¿Puede usar alarmas exactas? $puedeUsarAlarmasExactas")

                if (!puedeUsarAlarmasExactas) {
                    Log.e("PROGRAMACION", "NO TIENE PERMISO PARA ALARMAS EXACTAS")
                    Log.e("PROGRAMACION", "Redirigiendo a configuración...")

                    // Redirigir a la configuración para activar el permiso
                    val intentConfig = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
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

            // PROGRAMAR ALARMA
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tiempoEnMillis,
                    pendingIntent
                )
                Log.d("PROGRAMACION", "Alarma programada con setExactAndAllowWhileIdle")
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    tiempoEnMillis,
                    pendingIntent
                )
                Log.d("PROGRAMACION", "Alarma programada con setExact")
            }

            Log.d("PROGRAMACION", "========================================")

        } catch (e: SecurityException) {
            Log.e("PROGRAMACION", "SecurityException: ${e.message}")
            Log.e("PROGRAMACION", "Falta permiso SCHEDULE_EXACT_ALARM en el Manifest")
            e.printStackTrace()

            Toast.makeText(
                context,
                "Error: No se puede programar la alarma",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Log.e("PROGRAMACION", "Error al programar alarma: ${e.message}")
            e.printStackTrace()
        }
    }
}