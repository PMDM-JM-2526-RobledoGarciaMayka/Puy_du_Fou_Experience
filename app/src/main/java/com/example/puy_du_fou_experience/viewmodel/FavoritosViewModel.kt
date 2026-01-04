package com.example.puy_du_fou_experience.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.puy_du_fou_experience.data.EspectaculosDataBase
import com.example.puy_du_fou_experience.manager.FavoritosManager
import com.example.puy_du_fou_experience.model.Espectaculo
import com.example.puy_du_fou_experience.repository.EspectaculosRepository
import kotlinx.coroutines.launch

class FavoritosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EspectaculosRepository
    private val favoritosManager = FavoritosManager(application)

    private val _listaFavoritos = MutableLiveData<List<Espectaculo>>()
    val listaFavoritos: LiveData<List<Espectaculo>> get() = _listaFavoritos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        val dao = EspectaculosDataBase.getDatabase(application).EspectaculosDAO()
        repository = EspectaculosRepository(dao)
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Obtener todos los espectáculos de la base de datos
                val todosLosEspectaculos = repository.getAllEspectaculos()

                // Obtener los títulos de favoritos
                val favoritosTitulos = favoritosManager.obtenerFavoritos()

                // Filtrar solo los favoritos
                val favoritos = todosLosEspectaculos.filter { espectaculo ->
                    favoritosTitulos.contains(espectaculo.titulo)
                }

                _listaFavoritos.value = favoritos
            } catch (e: Exception) {
                e.printStackTrace()
                _listaFavoritos.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun recargarFavoritos() {
        cargarFavoritos()
    }

    fun obtenerCantidadFavoritos(): Int {
        return favoritosManager.obtenerFavoritos().size
    }
}