package com.example.puy_du_fou_experience.view.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.data.EspectaculosDataBase
import com.example.puy_du_fou_experience.databinding.FragmentMapaBinding
import com.example.puy_du_fou_experience.model.Espectaculo
import com.example.puy_du_fou_experience.viewmodel.EspectaculosViewModel
import com.example.puydufouexperience.fragments.DetalleEspectaculosFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapaFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var _binding: FragmentMapaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EspectaculosViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Inicializar el fragmento del mapa
        createFragment()
    }

    //Crear y obtener la referencia al SupportMapFragment
    private fun createFragment() {
        //Buscar el fragmento de mapa en el layout por su ID
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragmentMapa) as? SupportMapFragment
        //Obtener el mapa de forma asíncrona (callback en onMapReady)
        mapFragment?.getMapAsync(this)
    }

    //Callback que se ejecuta cuando el mapa está listo para usar
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        cargarMarcador()
        centrar()

        //Verificar si se tiene permiso de ubicación
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            //Si ya tiene permiso, habilitar la ubicación del usuario
            habilitarUbicacionUsuario()
        } else {
            //Si no tiene permiso, solicitarlo
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    //Centrar la cámara del mapa en las coordenadas
    private fun centrar() {
        //Coordenadas del parque
        val coordinates = LatLng(39.837338403870035, -4.0945422688603)
        //Animar la cámara hacia estas coordenadas
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 16f),
            1000, //Duración de la animación en milisegundos
            null
        )
    }

    //Cargar los marcadores de espectáculos en el mapa
    private fun cargarMarcador() {
        val db = EspectaculosDataBase.getDatabase(requireContext())

        //Ejecutar consulta en una corrutina
        lifecycleScope.launch {
            //Obtener todos los espectáculos de la base de datos
            val espectaculos = db.EspectaculosDAO().getAllEspectaculos()

            //Crear un marcador para cada espectáculo
            espectaculos.forEach { espectaculo ->
                //Seleccionar icono según el tipo de espectáculo
                val iconoTipo = when (espectaculo.tipo) {
                    "Teatro" -> R.drawable.imgteatro
                    "Espectáculo" -> R.drawable.imgespectaculo
                    "Exhibición" -> R.drawable.imgexhibicion
                    else -> {
                        R.drawable.espectaculos
                    }
                }
                //Decodificar el recurso drawable a Bitmap
                val bitmap = BitmapFactory.decodeResource(resources, iconoTipo)
                //Escalar el bitmap a 80x80 píxeles
                val bitmapEscalado: Bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false)
                //Convertir el bitmap escalado a BitmapDescriptor para usar en el marcador
                val icono: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmapEscalado)
                // Añadir el marcador al mapa
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(espectaculo.latitud, espectaculo.longitud))
                        .title(espectaculo.titulo) //Titulo del espectáculo
                        .snippet(espectaculo.descripcion) //Descripción del espectáculo
                        .icon(icono) //Icono del espectáculo
                )

                // Guardar el objeto espectáculo completo en el tag del marcador
                // para poder recuperarlo al hacer click
                marker?.tag = espectaculo
            }
        }

        //Configurar el listener para clicks en marcadores
        configurarClickMarker()
    }

    //Configurar el comportamiento cuando se hace click en la ventana de información de un marcador
    private fun configurarClickMarker() {
        map.setOnInfoWindowClickListener { marker ->
            // Recuperar el objeto espectáculo guardado en el tag del marcador
            val espectaculo = marker.tag as? Espectaculo
            espectaculo?.let { show ->
                //Crear instancia del fragmento de detalle
                val detalleFragment = DetalleEspectaculosFragment().apply {
                    //Pasar los datos del espectáculo mediante Bundle
                    arguments = Bundle().apply {
                        putInt("ID_ESPECTACULO", show.id)
                        putString("nombre", show.titulo)
                        putString("horarios", show.horarios)
                    }
                }

                // Navegar al fragmento de detalle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detalleFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Solicitar el permiso de ubicación
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                //Permiso concedido: habilitar ubicación del usuario
                habilitarUbicacionUsuario()
                Toast.makeText(
                    requireContext(),
                    "Permiso de ubicación concedido",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //Permiso denegado
                Toast.makeText(
                    requireContext(),
                    "Permiso de ubicación denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    //Habilitar la visualización de la ubicación del usuario en el mapa
    private fun habilitarUbicacionUsuario() {
        //Verificar que el mapa esté inicializado
        if (::map.isInitialized) {
            try {
                //Activar la capa de ubicación (muestra el punto azul del usuario)
                map.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                //Excepción sino hay permisos
                e.printStackTrace()
            }
        }
    }

}

