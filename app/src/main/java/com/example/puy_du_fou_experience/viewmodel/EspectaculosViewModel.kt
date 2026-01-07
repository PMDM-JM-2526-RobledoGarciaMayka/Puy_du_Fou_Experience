package com.example.puy_du_fou_experience.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.puy_du_fou_experience.data.EspectaculosDataBase
import com.example.puy_du_fou_experience.model.Espectaculo
import com.example.puy_du_fou_experience.data.EspectaculosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EspectaculosViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: EspectaculosRepository

    private val _listaEspectaculos = MutableLiveData<List<Espectaculo>>()
    val listaEspectaculos: LiveData<List<Espectaculo>> get() = _listaEspectaculos

    init {
        //Obtención de la base de datos y del DAO
        val database = EspectaculosDataBase.getDatabase(application)
        val espectaculoDao = database.EspectaculosDAO()
        //Inicialización del repositorio
        repository = EspectaculosRepository(espectaculoDao)
        //Carga inicial de los espectáctilos
        cargarEspectaculos()
    }

    //Obtiene todos los espectáculos desde la base de datos.
    fun cargarEspectaculos() {
        viewModelScope.launch (Dispatchers.IO) {
            //Llamada al repositorio para obtener los datos
            val espectaculos = repository.getAllEspectaculos()
            // Cambio al hilo principal para actualizar el LiveData
            withContext(Dispatchers.Main) {
                _listaEspectaculos.value = espectaculos

            }
        }
    }

    //Busca un espectáculo por su título.
    fun buscarEspectaculoporTitulo(titulo: String) : LiveData<Espectaculo?> {
        //LiveData que contendrá el resultado de la búsqueda
        val espectaculoLiveData = MutableLiveData<Espectaculo?>()
        viewModelScope.launch(Dispatchers.IO) {
            //Consulta del espectáculo por título
            val espectaculo = repository.getEspectaculoporTitulo(titulo)
            //Se publica el resultado en el hilo principal
            withContext(Dispatchers.Main) {
                espectaculoLiveData.value = espectaculo
            }
        }
        return espectaculoLiveData
    }

}


