package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.Ciudad
import com.familiasquesuman.demo.model.Testimonio
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

data class HomeUiState(
    val nombreFamilia: String,
    val ciudad: Ciudad,
    val proximas: List<Actividad>,
    val testimonios: List<Testimonio> = emptyList()
)

class HomeViewModel(private val repo: FamiliasRepository) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<HomeUiState>>(UiState.Cargando)
    val estado: StateFlow<UiState<HomeUiState>> = _estado.asStateFlow()

    val avisos: StateFlow<List<String>> = repo.avisos
    fun descartarAvisos() = repo.descartarAvisos()

    init {
        viewModelScope.launch { repo.ciudad.collect { cargar() } }
        viewModelScope.launch { repo.inscripciones.drop(1).collect { cargar(mostrarCarga = false) } }
        viewModelScope.launch { repo.familia.drop(1).collect { cargar(mostrarCarga = false) } }
    }

    fun cargar(mostrarCarga: Boolean = true) {
        viewModelScope.launch {
            if (mostrarCarga) _estado.value = UiState.Cargando
            _estado.value = cargarEstado {
                val ciudad = repo.ciudad.value
                val proximas = repo.obtenerActividades(ciudad).take(4)
                HomeUiState(
                    nombreFamilia = repo.familia.value.nombre,
                    ciudad = ciudad,
                    proximas = proximas,
                    testimonios = runCatching { repo.obtenerTestimoniosPublicos() }.getOrDefault(emptyList()).take(6)
                )
            }
        }
    }
}
