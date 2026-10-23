package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.Categoria
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActividadesViewModel(private val repo: FamiliasRepository) : ViewModel() {

    private val _todas = MutableStateFlow<UiState<List<Actividad>>>(UiState.Cargando)

    private val _categoria = MutableStateFlow(Categoria.TODAS)
    val categoria: StateFlow<Categoria> = _categoria.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val estado: StateFlow<UiState<List<Actividad>>> =
        combine(_todas, _categoria, _busqueda) { todas, cat, texto ->
            if (todas !is UiState.Datos) return@combine todas
            val filtradas = todas.datos
                .filter { cat == Categoria.TODAS || it.categoria == cat }
                .filter { texto.isBlank() || it.titulo.contains(texto, ignoreCase = true) }
            if (filtradas.isEmpty()) UiState.Vacio else UiState.Datos(filtradas)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    init {
        viewModelScope.launch { repo.ciudad.collect { cargar() } }
        viewModelScope.launch { repo.inscripciones.drop(1).collect { cargar(mostrarCarga = false) } }
    }

    fun cargar(mostrarCarga: Boolean = true) {
        viewModelScope.launch {
            if (mostrarCarga) _todas.value = UiState.Cargando
            _todas.value = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerActividades(repo.ciudad.value) }
        }
    }

    fun cambiarCategoria(categoria: Categoria) { _categoria.value = categoria }
    fun cambiarBusqueda(texto: String) { _busqueda.value = texto }
}
