package com.example.puy_du_fou_experience.view.navegacion_menu

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.databinding.MenuActivityBinding
import com.example.puy_du_fou_experience.view.fragments.AjustesFragment
import com.example.puy_du_fou_experience.view.fragments.EspectaculosFragment
import com.example.puy_du_fou_experience.view.fragments.MapaFragment
import com.example.puydufouexperience.fragments.FavoritosFragment
import java.util.Locale


class MenuActivity : AppCompatActivity() {

    private lateinit var binding: MenuActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        aplicarConfiguracionesGuardadas()

        super.onCreate(savedInstanceState)

        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(EspectaculosFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navegacion_espectaculos -> replaceFragment(EspectaculosFragment())
                R.id.navegacion_mapa -> replaceFragment(MapaFragment())
                R.id.navegacion_favoritos -> replaceFragment(FavoritosFragment())
                R.id.navegacion_ajustes -> replaceFragment(AjustesFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun aplicarConfiguracionesGuardadas() {
        val sharedPreferences = getSharedPreferences("ajustes", Context.MODE_PRIVATE)

        // Aplicar tema guardado
        val tema = sharedPreferences.getString("tema", "claro") ?: "claro"
        when (tema) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        // Aplicar idioma guardado
        val idioma = sharedPreferences.getString("idioma", "es") ?: "es"
        val locale = Locale(idioma)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}