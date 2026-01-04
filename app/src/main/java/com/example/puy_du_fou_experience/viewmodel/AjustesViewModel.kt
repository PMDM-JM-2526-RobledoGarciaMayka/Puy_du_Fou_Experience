package com.example.puy_du_fou_experience.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AjustesViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("ajustes", Context.MODE_PRIVATE)

    private val _idiomaSeleccionado = MutableLiveData<String>()
    val idiomaSeleccionado: LiveData<String> = _idiomaSeleccionado

    private val _notificacionesActivadas = MutableLiveData<Boolean>()
    val notificacionesActivadas: LiveData<Boolean> = _notificacionesActivadas

    private val _temaSeleccionado = MutableLiveData<String>()
    val temaSeleccionado: LiveData<String> = _temaSeleccionado

    init {
        cargarAjustes()
    }

    private fun cargarAjustes() {
        val idioma = sharedPreferences.getString("idioma", "es") ?: "es"
        val notificaciones = sharedPreferences.getBoolean("notificaciones", true)
        val tema = sharedPreferences.getString("tema", "claro") ?: "claro"

        _idiomaSeleccionado.value = idioma
        _notificacionesActivadas.value = notificaciones
        _temaSeleccionado.value = tema
    }

    fun guardarAjustes(idioma: String, notificaciones: Boolean, tema: String) {
        sharedPreferences.edit().apply {
            putString("idioma", idioma)
            putBoolean("notificaciones", notificaciones)
            putString("tema", tema)
            apply()
        }

        _idiomaSeleccionado.value = idioma
        _notificacionesActivadas.value = notificaciones
        _temaSeleccionado.value = tema
    }

    // Métodos para obtener configuraciones guardadas
    fun obtenerIdiomaGuardado(): String {
        return sharedPreferences.getString("idioma", "es") ?: "es"
    }

    fun obtenerTemaGuardado(): String {
        return sharedPreferences.getString("tema", "claro") ?: "claro"
    }
}