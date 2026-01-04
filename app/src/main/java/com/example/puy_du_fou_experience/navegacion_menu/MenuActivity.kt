package com.example.puy_du_fou_experience.navegacion_menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.databinding.MenuActivityBinding
import com.example.puy_du_fou_experience.fragments.AjustesFragment
import com.example.puy_du_fou_experience.fragments.EspectaculosFragment
import com.example.puy_du_fou_experience.fragments.MapaFragment
import com.example.puydufouexperience.fragments.FavoritosFragment

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: MenuActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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
            .commit()
    }


}