package com.example.puy_du_fou_experience.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.adapter.EspectaculosAdapter
import com.example.puy_du_fou_experience.databinding.FragmentEspectaculosBinding
import com.example.puy_du_fou_experience.model.Espectaculo
import com.example.puy_du_fou_experience.viewmodel.EspectaculosViewModel
import com.example.puydufouexperience.fragments.DetalleEspectaculosFragment


class EspectaculosFragment : Fragment() {
    private lateinit var espectaculosViewModel: EspectaculosViewModel
    private lateinit var espectaculosAdapter: EspectaculosAdapter

    private var _binding: FragmentEspectaculosBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEspectaculosBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        espectaculosViewModel = ViewModelProvider(this).get(EspectaculosViewModel::class.java)
        binding.rvEspectaculos.layoutManager = LinearLayoutManager(requireContext())

        espectaculosAdapter = EspectaculosAdapter { espectaculo ->
            val detalleFragment = DetalleEspectaculosFragment().apply {
                arguments = Bundle().apply {
                    putString("nombre", espectaculo.titulo)
                    putInt("imagen", espectaculo.imagen)
                    putString("horarios", espectaculo.horarios)
                    putString("duracion", espectaculo.duracion)
                    putString("zona", espectaculo.zona)
                    putString("descripcion", espectaculo.descripcion)
                    putDouble("precio", espectaculo.precio)
                    putString("restriccionEdad", espectaculo.resticcionEdad)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detalleFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvEspectaculos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@EspectaculosFragment.espectaculosAdapter
        }

        espectaculosViewModel.listaEspectaculos.observe(viewLifecycleOwner) { lista ->
            espectaculosAdapter.submitList(lista)
        }

        espectaculosViewModel.cargarEspectaculos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}