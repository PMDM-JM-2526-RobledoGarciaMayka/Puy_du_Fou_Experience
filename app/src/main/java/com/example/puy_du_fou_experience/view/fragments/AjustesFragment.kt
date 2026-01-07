package com.example.puy_du_fou_experience.view.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.puy_du_fou_experience.databinding.FragmentAjustesBinding
import com.example.puy_du_fou_experience.otras_utilidades.notificacion.ActivarDesactivarNotificaciones
import com.example.puy_du_fou_experience.viewmodel.AjustesViewModel
import java.util.Locale


class AjustesFragment : Fragment() {
    private lateinit var viewModel: AjustesViewModel
    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificacionesSharedPrefs: ActivarDesactivarNotificaciones

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicializar gestor de notificaciones y ViewModel
        notificacionesSharedPrefs = ActivarDesactivarNotificaciones(requireContext())
        viewModel = ViewModelProvider(this)[AjustesViewModel::class.java]

        //Configurar el switch de notificaciones
        binding.switchNotificaciones.isChecked = notificacionesSharedPrefs.activadas()

        //Listener para cambios en el switch de notificaciones
        binding.switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            notificacionesSharedPrefs.setActivadas(isChecked)
            val mensaje = if (isChecked) {
                "Notificaciones activadas"
            } else {
                "Notificaciones desactivadas"
            }
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
        }

        //Observar el idioma seleccionado y actualizar el RadioGroup correspondiente
        viewModel.idiomaSeleccionado.observe(viewLifecycleOwner) { idioma ->
            when (idioma) {
                "es" -> binding.rgIdioma.check(binding.rbEspaniol.id)
                "en" -> binding.rgIdioma.check(binding.rbIngles.id)
            }
        }

        //Observar el estado de las notificaciones y actualizar el Switch
        viewModel.notificacionesActivadas.observe(viewLifecycleOwner) { activadas ->
            binding.switchNotificaciones.isChecked = activadas
        }

        //Observar el tema seleccionado y actualizar el RadioGroup correspondiente
        viewModel.temaSeleccionado.observe(viewLifecycleOwner) { tema ->
            when (tema) {
                "claro" -> binding.rgTema.check(binding.rbClaro.id)
                "oscuro" -> binding.rgTema.check(binding.rbOscuro.id)
            }
        }

        //Configurar el botón Guardar para aplicar todos los ajustes
        binding.bttnGuardar.setOnClickListener {
            //Obtener el idioma seleccionado del RadioGroup
            val idioma = when (binding.rgIdioma.checkedRadioButtonId) {
                binding.rbEspaniol.id -> "es"
                binding.rbIngles.id -> "en"
                else -> "es"
            }

            //Obtener el estado de las notificaciones del Switch
            val notificaciones = binding.switchNotificaciones.isChecked

            //Obtener el tema seleccionado del RadioGroup
            val tema = when (binding.rgTema.checkedRadioButtonId) {
                binding.rbClaro.id -> "claro"
                binding.rbOscuro.id -> "oscuro"
                else -> "claro"
            }

            //Guardar todos los ajustes en el ViewModel (y por tanto en SharedPreferences)
            viewModel.guardarAjustes(idioma, notificaciones, tema)

            // Aplicar cambios
            aplicarIdioma(idioma)
            aplicarTema(tema)

            Toast.makeText(requireContext(), "Ajustes guardados", Toast.LENGTH_SHORT).show()
        }
    }

    //Aplica el idioma seleccionado a la aplicación.
    private fun aplicarIdioma(codigoIdioma: String) {
        val locale = Locale(codigoIdioma)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )

        //Reiniciar actividad para aplicar los cambios de idioma en toda la interfaz
        activity?.recreate()
    }

    //Aplica el tema seleccionado a la aplicación.
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