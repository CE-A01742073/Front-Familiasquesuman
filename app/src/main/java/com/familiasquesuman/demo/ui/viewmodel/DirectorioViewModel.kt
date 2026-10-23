package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Categoria
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DirectorioViewModel(private val repo: FamiliasRepository) : ViewModel() {

    private val _todos = MutableStateFlow<UiState<List<Centro>>>(UiState.Cargando)

    private val _categoria = MutableStateFlow(Categoria.TODAS)
    val categoria: StateFlow<Categoria> = _categoria.asStateFlow()

    val estado: StateFlow<UiState<List<Centro>>> =
        combine(_todos, _categoria) { todos, cat ->
            if (todos !is UiState.Datos) return@combine todos
            val filtrados = todos.datos.filter { cat == Categoria.TODAS || it.categoria == cat }
            if (filtrados.isEmpty()) UiState.Vacio else UiState.Datos(filtrados)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    init {
        viewModelScope.launch { repo.ciudad.collect { cargar() } }
    }

    fun cargar() {
        viewModelScope.launch {
            _todos.value = UiState.Cargando
            _todos.value = cargarEstado(esVacio = { it.isEmpty() }) {
                repo.obtenerCentros(repo.ciudad.value)
            }
        }
    }

    fun cambiarCategoria(categoria: Categoria) { _categoria.value = categoria }
}
