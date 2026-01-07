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
        //Aplica el tema y el idioma guardados antes de cargar la interfaz
        aplicarConfiguracionesGuardadas()

        super.onCreate(savedInstanceState)

        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Si la Activity se crea por primera vez,
        //se carga por defecto el fragment de espectáculos
        if (savedInstanceState == null) {
            replaceFragment(EspectaculosFragment(), false)
        }

        //Listener del BottomNavigationView
        binding.bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                //Navegación a Espectáculos
                R.id.navegacion_espectaculos -> replaceFragment(EspectaculosFragment())
                //Navegación a Mapa
                R.id.navegacion_mapa -> replaceFragment(MapaFragment())
                //Navegación a Favoritos
                R.id.navegacion_favoritos -> replaceFragment(FavoritosFragment())
                //Navegación a Ajustes
                R.id.navegacion_ajustes -> replaceFragment(AjustesFragment())
            }
            true
        }
    }

    //Reemplaza el fragment actual por el recibido como parámetro
    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null) //Permite volver atrás
        }

        transaction.commit()
    }

    //Aplica las configuraciones guardadas por el usuario:
    private fun aplicarConfiguracionesGuardadas() {
        //Acceso a SharedPreferences donde se guardan los ajustes
        val sharedPreferences = getSharedPreferences("ajustes", Context.MODE_PRIVATE)

        //Aplicar tema guardado
        val tema = sharedPreferences.getString("tema", "claro") ?: "claro"
        when (tema) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        //Aplicar idioma guardado
        val idioma = sharedPreferences.getString("idioma", "es") ?: "es"

        //Se crea el locale con el idioma guardado
        val locale = Locale(idioma)
        Locale.setDefault(locale)

        //Se actualiza la configuración de la aplicación
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}