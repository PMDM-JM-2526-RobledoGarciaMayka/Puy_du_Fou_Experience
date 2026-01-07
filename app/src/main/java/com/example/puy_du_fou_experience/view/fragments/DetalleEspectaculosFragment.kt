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
import androidx.lifecycle.lifecycleScope
import com.example.puy_du_fou_experience.data.EspectaculosDataBase
import com.example.puy_du_fou_experience.databinding.FragmentDetalleEspectaculosBinding
import com.example.puy_du_fou_experience.otras_utilidades.manager.FavoritosManager
import com.example.puy_du_fou_experience.otras_utilidades.notificacion.ActivarDesactivarNotificaciones
import com.example.puy_du_fou_experience.otras_utilidades.notificacion.NotificationHelper
import com.example.puy_du_fou_experience.otras_utilidades.notificacion.ProgramacionNotificacion
import kotlinx.coroutines.launch
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
        //Callback que se ejecuta cuando el usuario responde a la solicitud de permiso
        if (isGranted) {
            Toast.makeText(
                requireContext(),
                "Permiso concedido",
                Toast.LENGTH_SHORT
            ).show()

            //Si hay datos pendientes, crear el recordatorio
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

        //Crear el canal de notificaciones
        NotificationHelper.crearCanalNotificacion(requireContext())
        //Inicializar gestor de preferencias de notificaciones
        notifPrefs = ActivarDesactivarNotificaciones(requireContext())

        //Obtener datos del espectáculo desde los argumentos del fragmento
        val nombre = arguments?.getString("nombre") ?: ""
        val imagen = arguments?.getInt("imagen") ?: 0
        val horarios = arguments?.getString("horarios") ?: ""
        val duracion = arguments?.getString("duracion") ?: ""
        val zona = arguments?.getString("zona") ?: ""
        val descripcion = arguments?.getString("descripcion") ?: ""
        val precio = arguments?.getDouble("precio") ?: 0.0
        val restriccionEdad = arguments?.getString("restriccionEdad") ?: ""

        //Inicializar el gestor de favoritos
        val favManager = FavoritosManager(requireContext())


        //Configurar las vistas con los datos obtenidos
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

            //Listener para el toggle de favoritos
            toggleFav.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    //Añadir a favoritos
                    favManager.agregarFavorito(nombre)
                    Toast.makeText(
                        requireContext(),
                        "Añadido a favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //Quitar de favoritos
                    favManager.quitarFavorito(nombre)
                    Toast.makeText(
                        requireContext(),
                        "Eliminado de favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            //Listener para el botón de crear recordatorio
            btnRecordatorio.setOnClickListener {
                //Verificar si las notificaciones están activadas en preferencias
                if (!notifPrefs.activadas()) {
                    Toast.makeText(
                        requireContext(),
                        "Las notificaciones están desactivadas.\nActívalas en Ajustes.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                //Verificar permisos y crear recordatorio
                verificarPermisoYCrearRecordatorio(nombre, horarios)
            }
        }
        val id = arguments?.getInt("ID_ESPECTACULO") ?: return
        val db = EspectaculosDataBase.getDatabase(requireContext())

        //Cargar datos del espectáculo desde la base de datos en una corrutina
        lifecycleScope.launch {
            //Consultar espectáculo por ID
            val espectaculo = db.EspectaculosDAO().getEspectaculoporId(id)
            //Actualizar las vistas con los datos de la base de datos
            binding.apply {
                tvNombreDetalle.text = espectaculo.titulo
                imgDetalle.setImageResource(espectaculo.imagen)
                tvHorariosDetalle.text = "Horario: ${espectaculo.horarios}"
                tvDuracionDetalle.text = "Duración: ${espectaculo.duracion}"
                tvZonaDetalle.text = "Zona: ${espectaculo.zona}"
                tvPrecioDetalle.text = "Precio: ${espectaculo.precio}€"
                tvEdadDetalle.text = "Restricción de edad a menores de ${espectaculo.resticcionEdad}"
                tvDescripcionDetalle.text = espectaculo.descripcion

                //Reinicializar gestor de favoritos
                val favManager = FavoritosManager(requireContext())
                toggleFav.isChecked = favManager.esFavorito(espectaculo.titulo)

                // Reconfigurar listener del toggle de favoritos
                toggleFav.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        favManager.agregarFavorito(espectaculo.titulo)
                        Toast.makeText(requireContext(), "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                    } else {
                        favManager.quitarFavorito(espectaculo.titulo)
                        Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //Verificar si se tiene el permiso de notificaciones y crear recordatorio
    private fun verificarPermisoYCrearRecordatorio(nombreEspectaculo: String, horarios: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Comprobar si ya se tiene el permiso
            val tienePermiso = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            when {
                tienePermiso -> {
                    //Crear recordatorio
                    crearRecordatorio(nombreEspectaculo, horarios)
                }
                else -> {
                    //Guardar datos para usarlos después de obtener el permiso
                    pendienteNombre = nombreEspectaculo
                    pendienteHorarios = horarios

                    Toast.makeText(
                        requireContext(),
                        "Se necesita permiso para crear recordatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                    //Solicitar permiso
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            crearRecordatorio(nombreEspectaculo, horarios)
        }
    }

    //Crear una notificación para el espectáculo
    private fun crearRecordatorio(nombreEspectaculo: String, horarios: String) {
        //Validar que los datos no estén vacíos
        if (nombreEspectaculo.isEmpty() || horarios.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Error: Datos incompletos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        //Extraer el primer horario de la lista de horarios
        val primerHorario = horarios
            .replace("Horario:", "") //Eliminar prefijo
            .replace("Horario", "")
            .trim()
            .split(",") //Separar por comas
            .firstOrNull() //Obtener el primero
            ?.trim()

        //Validar que el horario sea válido
        if (primerHorario.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Error: Horario no válido",
                Toast.LENGTH_SHORT).show()
            return
        }

        //Calcular el tiempo en milisegundos para la notificación (15 min antes)
        val tiempoNotificacion = calcularTiempoNotificacion(primerHorario)
        val tiempoActual = System.currentTimeMillis()

        //Verificar que el horario no haya pasado
        if (tiempoNotificacion > tiempoActual) {
            //Programar la notificación
            ProgramacionNotificacion.programarNotificacion(
                requireContext(),
                nombreEspectaculo,
                primerHorario,
                tiempoNotificacion
            )

            //Crear instancia de Calendar con el tiempo de la notificación
            val calendario = Calendar.getInstance().apply {
                timeInMillis = tiempoNotificacion
            }

            //Determinar si la notificación es para hoy o mañana
            val diaNotif = calendario.get(Calendar.DAY_OF_MONTH)
            val ahora = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val esManana = diaNotif != ahora

            //Crear mensaje personalizado según si es hoy o mañana
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
            //El horario ya pasó
            Toast.makeText(
                requireContext(),
                "El horario $primerHorario ya ha pasado hoy",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //Calcular el tiempo en milisegundos para la notificación (15 min antes del horario)
    private fun calcularTiempoNotificacion(horario: String): Long {
        try {
            //Separar hora y minutos del formato "HH:mm"
            val partes = horario.split(":")
            if (partes.size != 2) return 0L

            //Convertir a enteros
            val hora = partes[0].toIntOrNull()
            val minutos = partes[1].toIntOrNull()

            //Validar que los valores sean válidos
            if (hora == null || minutos == null) return 0L
            if (hora !in 0..23 || minutos !in 0..59) return 0L

            //Crear instancia de Calendar con el horario del espectáculo
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minutos)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                //Restar 15 minutos para notificar antes
                add(Calendar.MINUTE, -15)
            }

            //Si el tiempo calculado ya pasó, programar para mañana
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            //Devolver el tiempo en milisegundos
            return calendar.timeInMillis

        } catch (e: Exception) {
            //En caso de error, imprimir stacktrace y devolver 0
            e.printStackTrace()
            return 0L
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}