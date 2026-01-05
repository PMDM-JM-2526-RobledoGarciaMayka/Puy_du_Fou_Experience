package com.example.puydufouexperience.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.puy_du_fou_experience.databinding.FragmentDetalleEspectaculosBinding
import com.example.puy_du_fou_experience.otras_utilidades.manager.FavoritosManager
import com.example.puy_du_fou_experience.otras_utilidades.notificacion.ActivarDesactivarNotificaciones
import com.example.puy_du_fou_experience.otras_utilidades.notificacion.NotificationHelper
import com.example.puy_du_fou_experience.otras_utilidades.notificacion.ProgramacionNotificacion
import java.util.Calendar


class DetalleEspectaculosFragment : Fragment() {
    private var _binding: FragmentDetalleEspectaculosBinding? = null
    private val binding get() = _binding!!

    private var pendienteNombre: String? = null
    private var pendienteHorarios: String? = null

    private lateinit var notifPrefs: ActivarDesactivarNotificaciones

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(
                requireContext(),
                "Permiso concedido",
                Toast.LENGTH_SHORT
            ).show()

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

        NotificationHelper.crearCanalNotificacion(requireContext())
        notifPrefs = ActivarDesactivarNotificaciones(requireContext())

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
                    Toast.makeText(
                        requireContext(),
                        "Añadido a favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    favManager.quitarFavorito(nombre)
                    Toast.makeText(
                        requireContext(),
                        "Eliminado de favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnRecordatorio.setOnClickListener {
                if (!notifPrefs.activadas()) {
                    Toast.makeText(
                        requireContext(),
                        "Las notificaciones están desactivadas.\nActívalas en Ajustes.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                verificarPermisoYCrearRecordatorio(nombre, horarios)
            }
        }
    }

    private fun verificarPermisoYCrearRecordatorio(nombreEspectaculo: String, horarios: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val tienePermiso = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            when {
                tienePermiso -> {
                    crearRecordatorio(nombreEspectaculo, horarios)
                }
                else -> {
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
            crearRecordatorio(nombreEspectaculo, horarios)
        }
    }

    private fun crearRecordatorio(nombreEspectaculo: String, horarios: String) {
        if (nombreEspectaculo.isEmpty() || horarios.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Error: Datos incompletos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val primerHorario = horarios
            .replace("Horario:", "")
            .replace("Horario", "")
            .trim()
            .split(",")
            .firstOrNull()
            ?.trim()

        if (primerHorario.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Error: Horario no válido",
                Toast.LENGTH_SHORT).show()
            return
        }

        val tiempoNotificacion = calcularTiempoNotificacion(primerHorario)
        val tiempoActual = System.currentTimeMillis()

        if (tiempoNotificacion > tiempoActual) {
            ProgramacionNotificacion.programarNotificacion(
                requireContext(),
                nombreEspectaculo,
                primerHorario,
                tiempoNotificacion
            )

            val calendario = Calendar.getInstance().apply {
                timeInMillis = tiempoNotificacion
            }


            val diaNotif = calendario.get(Calendar.DAY_OF_MONTH)
            val ahora = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val esManana = diaNotif != ahora

            val mensaje = if (esManana) {
                "Recordatorio creado para las: $primerHorario (mañana)"
            } else {
                "Recordatorio creado para las: $primerHorario (hoy)"
            }

            Toast.makeText(
                requireContext(),
                mensaje,
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                "El horario $primerHorario ya ha pasado hoy",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun calcularTiempoNotificacion(horario: String): Long {
        try {
            val partes = horario.split(":")
            if (partes.size != 2) return 0L

            val hora = partes[0].toIntOrNull()
            val minutos = partes[1].toIntOrNull()

            if (hora == null || minutos == null) return 0L
            if (hora !in 0..23 || minutos !in 0..59) return 0L

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minutos)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.MINUTE, -15)
            }

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            return calendar.timeInMillis

        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}