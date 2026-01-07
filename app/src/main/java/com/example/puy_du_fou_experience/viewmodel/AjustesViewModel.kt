package com.example.puy_du_fou_experience.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AjustesViewModel(application: Application) : AndroidViewModel(application) {

    //Acceso a SharedPreferences usando el contexto de la aplicación
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("ajustes", Context.MODE_PRIVATE)

    //Idioma
    private val _idiomaSeleccionado = MutableLiveData<String>()
    val idiomaSeleccionado: LiveData<String> = _idiomaSeleccionado

    //Notificaciones
    private val _notificacionesActivadas = MutableLiveData<Boolean>()
    val notificacionesActivadas: LiveData<Boolean> = _notificacionesActivadas

    //Tema
    private val _temaSeleccionado = MutableLiveData<String>()
    val temaSeleccionado: LiveData<String> = _temaSeleccionado

    init {
        cargarAjustes()
    }

    //Carga los ajustes almacenados en SharedPreference
    private fun cargarAjustes() {
        //Recupera los valores guardados o usa valores por defecto
        val idioma = sharedPreferences.getString("idioma", "es") ?: "es"
        val notificaciones = sharedPreferences.getBoolean("notificaciones", true)
        val tema = sharedPreferences.getString("tema", "claro") ?: "claro"

        //Actualiza los LiveData
        _idiomaSeleccionado.value = idioma
        _notificacionesActivadas.value = notificaciones
        _temaSeleccionado.value = tema
    }

    //Guarda los ajustes seleccionados por el usuario
    //y actualiza los LiveData para reflejar los cambios.
    fun guardarAjustes(idioma: String, notificaciones: Boolean, tema: String) {
        //Guarda los valores en SharedPreferences
        sharedPreferences.edit().apply {
            putString("idioma", idioma)
            putBoolean("notificaciones", notificaciones)
            putString("tema", tema)
            apply()
        }

        //Actualiza los LiveData
        _idiomaSeleccionado.value = idioma
        _notificacionesActivadas.value = notificaciones
        _temaSeleccionado.value = tema
    }

    //Devuelve el idioma guardado en SharedPreferences.
    fun obtenerIdiomaGuardado(): String {
        return sharedPreferences.getString("idioma", "es") ?: "es"
    }

    //Devuelve el tema guardado en SharedPreferences.
    fun obtenerTemaGuardado(): String {
        return sharedPreferences.getString("tema", "claro") ?: "claro"
    }
}