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
        createFragment()
    }

    private fun createFragment() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragmentMapa) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        cargarMarcador()
        centrar()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            habilitarUbicacionUsuario()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun centrar() {
        val coordinates = LatLng(39.837338403870035, -4.0945422688603)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 16f),
            1000,
            null
        )
    }

    private fun cargarMarcador() {
        val db = EspectaculosDataBase.getDatabase(requireContext())

        lifecycleScope.launch {
            val espectaculos = db.EspectaculosDAO().getAllEspectaculos()

            espectaculos.forEach { espectaculo ->
                val iconoTipo = when (espectaculo.tipo) {
                    "Teatro" -> R.drawable.imgteatro
                    "Espectáculo" -> R.drawable.imgespectaculo
                    "Exhibición" -> R.drawable.imgexhibicion
                    else -> {
                        R.drawable.espectaculos
                    }
                }
                val bitmap = BitmapFactory.decodeResource(resources, iconoTipo)
                val bitmapEscalado: Bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false)
                val icono: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmapEscalado)
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(espectaculo.latitud, espectaculo.longitud))
                        .title(espectaculo.titulo)
                        .snippet(espectaculo.descripcion)
                        .icon(icono)
                )

                // Guardamos el objeto completo en el marker
                marker?.tag = espectaculo
            }
        }

        configurarClickMarker()
    }

    private fun configurarClickMarker() {
        map.setOnInfoWindowClickListener { marker ->
            val espectaculo = marker.tag as? Espectaculo
            espectaculo?.let { show ->
                val detalleFragment = DetalleEspectaculosFragment().apply {
                    arguments = Bundle().apply {
                        putInt("ID_ESPECTACULO", show.id)
                        putString("nombre", show.titulo)
                        putString("horarios", show.horarios)
                    }
                }

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

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                habilitarUbicacionUsuario()
                Toast.makeText(
                    requireContext(),
                    "Permiso de ubicación concedido",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permiso de ubicación denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun habilitarUbicacionUsuario() {
        if (::map.isInitialized) {
            try {
                map.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

}

