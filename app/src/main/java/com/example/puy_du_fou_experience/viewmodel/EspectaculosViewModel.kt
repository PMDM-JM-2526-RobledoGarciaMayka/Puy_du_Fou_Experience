package com.example.puy_du_fou_experience.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.puy_du_fou_experience.data.EspectaculosDataBase
import com.example.puy_du_fou_experience.model.Espectaculo
import com.example.puy_du_fou_experience.repository.EspectaculosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EspectaculosViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: EspectaculosRepository

    private val _listaEspectaculos = MutableLiveData<List<Espectaculo>>()
    val listaEspectaculos: LiveData<List<Espectaculo>> get() = _listaEspectaculos

    init {
        val database = EspectaculosDataBase.getDatabase(application)
        val espectaculoDao = database.EspectaculosDAO()
        repository = EspectaculosRepository(espectaculoDao)
        cargarEspectaculos()
    }

    fun cargarEspectaculos() {
        viewModelScope.launch (Dispatchers.IO) {
            val espectaculos = repository.getAllEspectaculos()
            withContext(Dispatchers.Main) {
                _listaEspectaculos.value = espectaculos

            }
        }
    }

    fun buscarEspectaculoporTitulo(titulo: String) : LiveData<Espectaculo?> {
        val espectaculoLiveData = MutableLiveData<Espectaculo?>()
        viewModelScope.launch(Dispatchers.IO) {
            val espectaculo = repository.getEspectaculoporTitulo(titulo)
            withContext(Dispatchers.Main) {
                espectaculoLiveData.value = espectaculo
            }
        }
        return espectaculoLiveData
    }

}


