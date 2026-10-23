package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Proyecto
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProyectosViewModel(private val repo: FamiliasRepository) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Proyecto>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Proyecto>>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            _estado.value = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerProyectos() }
        }
    }
}

class DetalleProyectoViewModel(
    private val repo: FamiliasRepository,
    private val proyectoId: String
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Proyecto>>(UiState.Cargando)
    val estado: StateFlow<UiState<Proyecto>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            _estado.value = cargarEstado { repo.obtenerProyecto(proyectoId) }
        }
    }
}
