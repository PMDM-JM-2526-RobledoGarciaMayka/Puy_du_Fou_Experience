package com.example.puydufouexperience.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.puy_du_fou_experience.databinding.FragmentDetalleEspectaculosBinding
import com.example.puy_du_fou_experience.manager.FavoritosManager
import com.example.puy_du_fou_experience.notificacion.NotificationSchedule


class DetalleEspectaculosFragment : Fragment() {
    private var _binding: FragmentDetalleEspectaculosBinding? = null
    private val binding get() = _binding!!

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
                } else {
                    favManager.quitarFavorito(nombre)
                }
            }

            // Botón de recordatorio
            btnRecordatorio.setOnClickListener {
                NotificationSchedule.programarNotificacionPrueba(requireContext(), 5)
                crearRecordatorio(nombre, horarios)
            }
        }
    }

    private fun crearRecordatorio(nombreEspectaculo: String, horarios: String) {
        if (nombreEspectaculo.isEmpty() || horarios.isEmpty()) {
            Toast.makeText(requireContext(), "Error: Datos incompletos", Toast.LENGTH_SHORT).show()
            return
        }

        // Extraer el primer horario de la cadena
        // Ejemplo: "14:00, 16:30, 18:00" -> "14:00"
        val primerHorario = horarios
            .replace("Horario:", "")  // Por si viene con el prefijo
            .trim()
            .split(",")
            .firstOrNull()
            ?.trim()

        if (primerHorario.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Error: Horario no válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Calcular tiempo para la notificación
        val tiempoNotificacion = calcularTiempoNotificacion(primerHorario)

        if (tiempoNotificacion > System.currentTimeMillis()) {
            // Programar notificación
            NotificationSchedule.programarNotificacion(
                requireContext(),
                nombreEspectaculo,
                primerHorario,
                tiempoNotificacion
            )

            Toast.makeText(
                requireContext(),
                "Recordatorio creado para las $primerHorario\n(15 minutos antes)",
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
            // Parsear horario (formato esperado: "14:00" o "14:30")
            val partes = horario.split(":")
            if (partes.size != 2) return 0L

            val hora = partes[0].toIntOrNull() ?: return 0L
            val minutos = partes[1].toIntOrNull() ?: return 0L

            // Validar rangos
            if (hora !in 0..23 || minutos !in 0..59) return 0L

            // Crear Calendar para hoy a esa hora
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minutos)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // Restar 15 minutos para notificar ANTES del espectáculo
                add(Calendar.MINUTE, -15)
            }

            // Si la hora ya pasó hoy, programar para mañana
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