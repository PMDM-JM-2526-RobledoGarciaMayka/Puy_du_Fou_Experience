package com.example.puydufouexperience.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.adapter.EspectaculosAdapter
import com.example.puy_du_fou_experience.databinding.FragmentFavoritosBinding
import com.example.puy_du_fou_experience.viewmodel.FavoritosViewModel

class FavoritosFragment : Fragment() {

    private val viewModel: FavoritosViewModel by viewModels()

    private var _binding: FragmentFavoritosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: EspectaculosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observarFavoritos()
    }

    //Configurar el RecyclerView y su adaptador
    private fun setupRecyclerView() {
        //Crear el adaptador con un lambda que maneja el click en cada espectáculo
        adapter = EspectaculosAdapter { espectaculo ->
            //Crear instancia del fragmento de detalle cuando se hace click
            val detalleFragment = DetalleEspectaculosFragment().apply {
                //Pasar datos del espectáculo seleccionado mediante Bundle
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

        //Configurar el RecyclerView con su layout y adaptador
        binding.rvfavoritos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritosFragment.adapter
        }
    }

    //Observar cambios en la lista de favoritos desde el ViewModel
    private fun observarFavoritos() {
        viewModel.listaFavoritos.observe(viewLifecycleOwner) { favoritos ->
            //Actualizar el adaptador con la nueva lista de favoritos
            adapter.submitList(favoritos)

            //Mostrar/ocultar el RecyclerView según si hay favoritos o no
            if (favoritos.isEmpty()) {
                //Si no hay favoritos, ocultar el RecyclerView
                binding.rvfavoritos.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "No hay favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //Si hay favoritos, mostrar el RecyclerView
                binding.rvfavoritos.visibility = View.VISIBLE
            }
        }
    }

    //Se ejecuta cada vez que el fragmento vuelve a primer plano
    override fun onResume() {
        super.onResume()
        //Recargar la lista de favoritos por si ha habido cambios
        // (si se añadió o quito un favorito desde otro fragmento)(por ejemplo, si se añadió/quitó un favorito desde otro fragmento)
        viewModel.recargarFavoritos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}