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
    //SharedPreferences para persistir datos de usuario
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        //Recuperar el usuario guardado anteriormente (si existe)
        val usuarioGuardado = sharedPreferences.getString(USUARIO, "")
        //Pre-rellenar el campo de usuario con el valor guardado
        binding.etUsuario.setText(usuarioGuardado)


        //Configurar el listener del botón de acceder
        binding.bttnAcceder.setOnClickListener {
            //Obtener los valores introducidos por el usuario
            val usuario = binding.etUsuario.text.toString()
            val contraseña = binding.etContrasenia.text.toString()

            //Validar las credenciales y realizar el login
            when {
                usuario.isEmpty() || contraseña.isEmpty() -> {
                    //Si algún campo está vacío, mostrar un mensaje
                    Toast.makeText(
                        this,
                        "Es necesario rellenar todos los campos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                usuario == "demo" && contraseña == "demo" -> {
                    //Guardar el usuario en SharedPreferences para futuras sesiones
                    sharedPreferences.edit()
                        .putString(USUARIO, usuario)
                        .apply()

                    //Crear intent para navegar a la actividad principal (menú)
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                    //Cerrar la actividad de login para que no se pueda volver con botón atrás
                    finish()
                }
                else -> {
                    //Si las credenciales son incorrectas, mostrar un mensaje
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

