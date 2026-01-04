package com.example.puy_du_fou_experience.view.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.puy_du_fou_experience.databinding.LoginActivityBinding
import com.example.puy_du_fou_experience.view.navegacion_menu.MenuActivity

class LoginActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "MyPrefsFile"
        const val USUARIO = "usuario"
    }

    private lateinit var binding: LoginActivityBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val usuarioGuardado = sharedPreferences.getString(USUARIO, "")
        binding.etUsuario.setText(usuarioGuardado)

        binding.bttnAcceder.setOnClickListener {
            val usuario = binding.etUsuario.text.toString()
            val contraseña = binding.etContrasenia.text.toString()

            when {
                usuario.isEmpty() || contraseña.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Es necesario rellenar todos los campos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                usuario == "demo" && contraseña == "demo" -> {
                    sharedPreferences.edit()
                        .putString(USUARIO, usuario)
                        .apply()

                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Usuario o contraseña incorrectos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}