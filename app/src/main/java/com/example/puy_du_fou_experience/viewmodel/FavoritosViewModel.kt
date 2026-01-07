package com.example.puy_du_fou_experience.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.puy_du_fou_experience.data.EspectaculosDataBase
import com.example.puy_du_fou_experience.model.Espectaculo
import com.example.puy_du_fou_experience.otras_utilidades.manager.FavoritosManager
import com.example.puy_du_fou_experience.data.EspectaculosRepository
import kotlinx.coroutines.launch


class FavoritosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EspectaculosRepository
    private val favoritosManager = FavoritosManager(application)

    private val _listaFavoritos = MutableLiveData<List<Espectaculo>>()
    val listaFavoritos: LiveData<List<Espectaculo>> get() = _listaFavoritos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        //Obtener el DAO de la base de datos
        val dao = EspectaculosDataBase.getDatabase(application).EspectaculosDAO()
        //Inicializar el repositorio
        repository = EspectaculosRepository(dao)
        //Cargar los favoritos
        cargarFavoritos()
    }

    //Carga la lista de favoritos
    private fun cargarFavoritos() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                //Obtener todos los espectáculos de la base de datos
                val todosLosEspectaculos = repository.getAllEspectaculos()

                //Obtener los títulos de favoritos
                val favoritosTitulos = favoritosManager.obtenerFavoritos()

                //Filtrar solo los espectáctilos que están en la lista de favoritos
                val favoritos = todosLosEspectaculos.filter { espectaculo ->
                    favoritosTitulos.contains(espectaculo.titulo)
                }

                //Publica la lista de favoritos
                _listaFavoritos.value = favoritos
            } catch (e: Exception) {
                //En caso de error, publicar una lista vacía
                e.printStackTrace()
                _listaFavoritos.value = emptyList()

            } finally {
                _isLoading.value = false
            }
        }
    }

    //Fuerza la recarga de los favoritos
    fun recargarFavoritos() {
        cargarFavoritos()
    }

    //Devuelve la cantidad de favoritos
    fun obtenerCantidadFavoritos(): Int {
        return favoritosManager.obtenerFavoritos().size
    }
}