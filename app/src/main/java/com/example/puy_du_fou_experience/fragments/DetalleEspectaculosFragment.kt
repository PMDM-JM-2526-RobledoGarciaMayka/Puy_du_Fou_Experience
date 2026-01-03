package com.example.puydufouexperience.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.databinding.FragmentDetalleEspectaculosBinding

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

        //val favManager = FavoritosManager(requireContext())

        binding.apply {
            tvNombreDetalle.text = nombre
            imgDetalle.setImageResource(imagen)
            tvHorariosDetalle.text = "Horario: $horarios"
            tvDuracionDetalle.text = "Duración: $duracion"
            tvZonaDetalle.text = zona
            tvPrecioDetalle.text = "Precio: $precio€"
            tvEdadDetalle.text = "Restricción de edad a menores de $restriccionEdad"
            tvDescripcionDetalle.text = descripcion
            //toggleFav.isChecked = favManager.esFavorito(nombre)


            /*toggleFav.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    favManager.agregarFavorito(nombre)
                } else {
                    favManager.quitarFavorito(nombre)
                }
            }*/

            // Botón de recordatorio
            btnRecordatorio.setOnClickListener {
                // TODO: Implementar lógica de recordatorio/notificación
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}