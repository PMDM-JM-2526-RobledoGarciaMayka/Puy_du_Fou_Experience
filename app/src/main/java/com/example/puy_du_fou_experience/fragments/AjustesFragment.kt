package com.example.puy_du_fou_experience.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.puy_du_fou_experience.databinding.FragmentAjustesBinding
import com.example.puy_du_fou_experience.viewmodel.AjustesViewModel
import java.util.Locale

class AjustesFragment : Fragment() {
    private lateinit var viewModel: AjustesViewModel
    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AjustesViewModel::class.java]

        // Cargar ajustes actuales
        viewModel.idiomaSeleccionado.observe(viewLifecycleOwner) { idioma ->
            when (idioma) {
                "es" -> binding.rgIdioma.check(binding.rbEspaniol.id)
                "en" -> binding.rgIdioma.check(binding.rbIngles.id)
            }
        }

        viewModel.notificacionesActivadas.observe(viewLifecycleOwner) { activadas ->
            binding.switchNotificaciones.isChecked = activadas
        }

        viewModel.temaSeleccionado.observe(viewLifecycleOwner) { tema ->
            when (tema) {
                "claro" -> binding.rgTema.check(binding.rbClaro.id)
                "oscuro" -> binding.rgTema.check(binding.rbOscuro.id)
            }
        }

        // Botón Guardar
        binding.bttnGuardar.setOnClickListener {
            val idioma = when (binding.rgIdioma.checkedRadioButtonId) {
                binding.rbEspaniol.id -> "es"
                binding.rbIngles.id -> "en"
                else -> "es"
            }

            val notificaciones = binding.switchNotificaciones.isChecked

            val tema = when (binding.rgTema.checkedRadioButtonId) {
                binding.rbClaro.id -> "claro"
                binding.rbOscuro.id -> "oscuro"
                else -> "claro"
            }

            viewModel.guardarAjustes(idioma, notificaciones, tema)

            // Aplicar cambios
            aplicarIdioma(idioma)
            aplicarTema(tema)
        }
    }

    private fun aplicarIdioma(codigoIdioma: String) {
        val locale = Locale(codigoIdioma)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        // Reiniciar actividad para aplicar cambios
        activity?.recreate()
    }

    private fun aplicarTema(tema: String) {
        when (tema) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}