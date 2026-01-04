package com.example.puydufouexperience.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.puy_du_fou_experience.databinding.FragmentDetalleEspectaculosBinding
import com.example.puy_du_fou_experience.manager.FavoritosManager
import com.example.puy_du_fou_experience.notificacion.ActivarDesactivarNotificaciones
import com.example.puy_du_fou_experience.notificacion.NotificationHelper
import com.example.puy_du_fou_experience.notificacion.ProgramacionNotificacion

class DetalleEspectaculosFragment : Fragment() {
    private var _binding: FragmentDetalleEspectaculosBinding? = null
    private val binding get() = _binding!!

    private var pendienteNombre: String? = null
    private var pendienteHorarios: String? = null

    private lateinit var notifPrefs: ActivarDesactivarNotificaciones

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("DETALLE_FRAGMENT", "Resultado permiso: $isGranted")

        if (isGranted) {
            Toast.makeText(requireContext(), "Permiso concedido", Toast.LENGTH_SHORT).show()

            if (pendienteNombre != null && pendienteHorarios != null) {
                crearRecordatorio(pendienteNombre!!, pendienteHorarios!!)
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Sin permiso no puedes recibir notificaciones",
                Toast.LENGTH_LONG
            ).show()
        }

        pendienteNombre = null
        pendienteHorarios = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleEspectaculosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crear canal de notificación
        NotificationHelper.crearCanalNotificacion(requireContext())
        Log.d("DETALLE_FRAGMENT", "Canal de notificación creado")

        // Inicializar preferencias de notificaciones
        notifPrefs = ActivarDesactivarNotificaciones(requireContext())  // ← AÑADIR

        val nombre = arguments?.getString("nombre") ?: ""
        val imagen = arguments?.getInt("imagen") ?: 0
        val horarios = arguments?.getString("horarios") ?: ""
        val duracion = arguments?.getString("duracion") ?: ""
        val zona = arguments?.getString("zona") ?: ""
        val descripcion = arguments?.getString("descripcion") ?: ""
        val precio = arguments?.getDouble("precio") ?: 0.0
        val restriccionEdad = arguments?.getString("restriccionEdad") ?: ""

        val favManager = FavoritosManager(requireContext())

        binding.apply {
            tvNombreDetalle.text = nombre
            imgDetalle.setImageResource(imagen)
            tvHorariosDetalle.text = "Horario: $horarios"
            tvDuracionDetalle.text = "Duración: $duracion"
            tvZonaDetalle.text = "Zona: $zona"
            tvPrecioDetalle.text = "Precio: $precio€"
            tvEdadDetalle.text = "Restricción de edad a menores de $restriccionEdad"
            tvDescripcionDetalle.text = descripcion
            toggleFav.isChecked = favManager.esFavorito(nombre)

            toggleFav.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    favManager.agregarFavorito(nombre)
                    Toast.makeText(requireContext(), "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    favManager.quitarFavorito(nombre)
                    Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }
            }

            // Botón de recordatorio
            btnRecordatorio.setOnClickListener {
                Log.d("DETALLE_FRAGMENT", "========================================")
                Log.d("DETALLE_FRAGMENT", "BOTÓN RECORDATORIO PRESIONADO")
                Log.d("DETALLE_FRAGMENT", "========================================")

                // ← VERIFICAR SI LAS NOTIFICACIONES ESTÁN HABILITADAS
                if (!notifPrefs.activadas()) {
                    Toast.makeText(
                        requireContext(),
                        "Las notificaciones están desactivadas.\nActívalas en Ajustes.",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("DETALLE_FRAGMENT", "Notificaciones deshabilitadas en Ajustes")
                    return@setOnClickListener
                }

                verificarPermisoYCrearRecordatorio(nombre, horarios)
            }
        }
    }

    private fun verificarPermisoYCrearRecordatorio(nombreEspectaculo: String, horarios: String) {
        Log.d("DETALLE_FRAGMENT", "--- Verificando permisos ---")
        Log.d("DETALLE_FRAGMENT", "Android version: ${Build.VERSION.SDK_INT}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val tienePermiso = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            Log.d("DETALLE_FRAGMENT", "¿Tiene permiso POST_NOTIFICATIONS? $tienePermiso")

            when {
                tienePermiso -> {
                    Log.d("DETALLE_FRAGMENT", "Permiso concedido, creando recordatorio")
                    crearRecordatorio(nombreEspectaculo, horarios)
                }
                else -> {
                    Log.d("DETALLE_FRAGMENT", "Sin permiso, solicitando...")
                    pendienteNombre = nombreEspectaculo
                    pendienteHorarios = horarios

                    Toast.makeText(
                        requireContext(),
                        "Se necesita permiso para crear recordatorios",
                        Toast.LENGTH_SHORT
                    ).show()

                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d("DETALLE_FRAGMENT", "Android < 13, no necesita permiso")
            crearRecordatorio(nombreEspectaculo, horarios)
        }
    }

    private fun crearRecordatorio(nombreEspectaculo: String, horarios: String) {
        Log.d("RECORDATORIO", "========================================")
        Log.d("RECORDATORIO", "=== INICIO CREAR RECORDATORIO ===")
        Log.d("RECORDATORIO", "========================================")
        Log.d("RECORDATORIO", "Nombre: '$nombreEspectaculo'")
        Log.d("RECORDATORIO", "Horarios raw: '$horarios'")

        if (nombreEspectaculo.isEmpty() || horarios.isEmpty()) {
            Log.e("RECORDATORIO", "ERROR: Datos incompletos")
            Toast.makeText(requireContext(), "Error: Datos incompletos", Toast.LENGTH_SHORT).show()
            return
        }

        val primerHorario = horarios
            .replace("Horario:", "")
            .replace("Horario", "")
            .trim()
            .split(",")
            .firstOrNull()
            ?.trim()

        Log.d("RECORDATORIO", "Primer horario extraído: '$primerHorario'")

        if (primerHorario.isNullOrEmpty()) {
            Log.e("RECORDATORIO", "ERROR: Horario no válido")
            Toast.makeText(requireContext(), "Error: Horario no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val tiempoNotificacion = calcularTiempoNotificacion(primerHorario)
        val tiempoActual = System.currentTimeMillis()
        val diferenciaMinutos = (tiempoNotificacion - tiempoActual) / 1000 / 60

        Log.d("RECORDATORIO", "Tiempo notificación: $tiempoNotificacion")
        Log.d("RECORDATORIO", "Tiempo actual: $tiempoActual")
        Log.d("RECORDATORIO", "Diferencia: $diferenciaMinutos minutos")

        if (tiempoNotificacion > tiempoActual) {
            Log.d("RECORDATORIO", "Tiempo válido, programando...")

            ProgramacionNotificacion.programarNotificacion(
                requireContext(),
                nombreEspectaculo,
                primerHorario,
                tiempoNotificacion
            )

            val calendario = Calendar.getInstance().apply {
                timeInMillis = tiempoNotificacion
            }

            val horaNotif = String.format(
                "%02d:%02d",
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE)
            )

            val diaNotif = calendario.get(Calendar.DAY_OF_MONTH)
            val ahora = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val esManana = diaNotif != ahora

            val mensaje = if (esManana) {
                "Recordatorio creado\nEspectáculo: $primerHorario (mañana)\nTe avisaremos a las: $horaNotif"
            } else {
                "Recordatorio creado\nEspectáculo: $primerHorario (hoy)\nTe avisaremos a las: $horaNotif"
            }

            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()

            Log.d("RECORDATORIO", "Recordatorio programado exitosamente")
            Log.d("RECORDATORIO", "Hora notificación: $horaNotif ${if (esManana) "(mañana)" else "(hoy)"}")
            Log.d("RECORDATORIO", "========================================")
        } else {
            Log.e("RECORDATORIO", "El horario ya pasó")
            Toast.makeText(
                requireContext(),
                "El horario $primerHorario ya ha pasado hoy",
                Toast.LENGTH_LONG
            ).show()
            Log.d("RECORDATORIO", "========================================")
        }
    }

    private fun calcularTiempoNotificacion(horario: String): Long {
        Log.d("CALCULO_TIEMPO", "--- Calculando tiempo ---")
        Log.d("CALCULO_TIEMPO", "Horario recibido: '$horario'")

        try {
            val partes = horario.split(":")
            Log.d("CALCULO_TIEMPO", "Partes separadas: ${partes.size} -> $partes")

            if (partes.size != 2) {
                Log.e("CALCULO_TIEMPO", "Error: formato incorrecto (esperado HH:MM)")
                return 0L
            }

            val hora = partes[0].toIntOrNull()
            val minutos = partes[1].toIntOrNull()

            Log.d("CALCULO_TIEMPO", "Hora parseada: $hora")
            Log.d("CALCULO_TIEMPO", "Minutos parseados: $minutos")

            if (hora == null || minutos == null) {
                Log.e("CALCULO_TIEMPO", "Error: no se pudo parsear a números")
                return 0L
            }

            if (hora !in 0..23 || minutos !in 0..59) {
                Log.e("CALCULO_TIEMPO", "Error: hora/minutos fuera de rango")
                return 0L
            }

            val ahora = Calendar.getInstance()
            Log.d("CALCULO_TIEMPO", "Hora actual: ${ahora.get(Calendar.HOUR_OF_DAY)}:${ahora.get(Calendar.MINUTE)}")

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minutos)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            Log.d("CALCULO_TIEMPO", "Hora del espectáculo: $hora:$minutos")

            calendar.add(Calendar.MINUTE, -15)

            val horaNotif = calendar.get(Calendar.HOUR_OF_DAY)
            val minNotif = calendar.get(Calendar.MINUTE)
            Log.d("CALCULO_TIEMPO", "Hora de notificación (15 min antes): $horaNotif:$minNotif")

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                Log.d("CALCULO_TIEMPO", "Ya pasó, programando para MAÑANA")
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            } else {
                Log.d("CALCULO_TIEMPO", "Programando para HOY")
            }

            Log.d("CALCULO_TIEMPO", "Fecha/hora final: ${calendar.time}")
            Log.d("CALCULO_TIEMPO", "Timestamp: ${calendar.timeInMillis}")

            return calendar.timeInMillis

        } catch (e: Exception) {
            Log.e("CALCULO_TIEMPO", "EXCEPCIÓN: ${e.message}")
            e.printStackTrace()
            return 0L
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

