package com.example.puy_du_fou_experience.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.adapter.EspectaculosAdapter
import com.example.puy_du_fou_experience.databinding.FragmentEspectaculosBinding
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

        //Crear el adaptador con un lambda que maneja el click en cada espectáculo
        espectaculosAdapter = EspectaculosAdapter { espectaculo ->
            //Crear una instancia del fragmento de detalle
            val detalleFragment = DetalleEspectaculosFragment().apply {
                //Crear un Bundle con los datos del espectáculo seleccionado
                arguments = Bundle().apply {
                    putInt("ID_ESPECTACULO", espectaculo.id)
                    putString("nombre", espectaculo.titulo)
                    putString("horarios", espectaculo.horarios)
                }
            }

            //Navegar al fragmento de detalle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detalleFragment)
                .addToBackStack(null)
                .commit()
        }

        //Configurar el RecyclerView
        binding.rvEspectaculos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@EspectaculosFragment.espectaculosAdapter
        }

        //Observar los cambios en la lista de espectáculos desde el ViewModel
        espectaculosViewModel.listaEspectaculos.observe(viewLifecycleOwner) { lista ->
            //Cuando la lista cambia, actualizar el adaptador con los nuevos datos
            espectaculosAdapter.submitList(lista)
        }
        //Cargar la lista inicial de espectáculos
        espectaculosViewModel.cargarEspectaculos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}