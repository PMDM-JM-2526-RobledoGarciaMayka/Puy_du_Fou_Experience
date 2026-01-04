package com.example.puydufouexperience.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private fun setupRecyclerView() {
        adapter = EspectaculosAdapter { espectaculo ->
            val detalleFragment = DetalleEspectaculosFragment().apply {
                arguments = Bundle().apply {
                    putInt("imagen", espectaculo.imagen)
                    putString("nombre", espectaculo.titulo)
                    putString("horarios", espectaculo.horarios)
                    putString("duracion", espectaculo.duracion)
                    putString("zona", espectaculo.zona)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detalleFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvfavoritos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritosFragment.adapter
        }
    }

    private fun observarFavoritos() {
        viewModel.listaFavoritos.observe(viewLifecycleOwner) { favoritos ->
            adapter.submitList(favoritos)

            // Opcional: mostrar mensaje si no hay favoritos
            if (favoritos.isEmpty()) {
                binding.rvfavoritos.visibility = View.GONE
                // binding.tvSinFavoritos.visibility = View.VISIBLE
            } else {
                binding.rvfavoritos.visibility = View.VISIBLE
                // binding.tvSinFavoritos.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar favoritos cuando volvemos al fragment
        viewModel.recargarFavoritos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}