package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Inscripcion
import com.familiasquesuman.demo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MisInscripcionesUiState(
    val proximas: List<Inscripcion>,
    val pasadas: List<Inscripcion>,
    val pendientesPorEnviar: Int
)

class MisInscripcionesViewModel(private val repo: FamiliasRepository) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    // La lista viene del repositorio (servidor + cola offline); aquí solo se decide qué mostrar
    val estado: StateFlow<UiState<MisInscripcionesUiState>> =
        combine(repo.inscripciones, repo.pendientesPorEnviar, _cargando, _error) { lista, pendientes, cargando, error ->
            when {
                error != null && lista.isEmpty() -> UiState.Error(error)
                cargando && lista.isEmpty() -> UiState.Cargando
                lista.isEmpty() -> UiState.Vacio
                else -> UiState.Datos(
                    MisInscripcionesUiState(
                        proximas = lista.filter { !it.pasada }.sortedBy { it.fecha + it.horaInicio },
                        pasadas = lista.filter { it.pasada }.sortedByDescending { it.fecha },
                        pendientesPorEnviar = pendientes
                    )
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _cancelandoId = MutableStateFlow<String?>(null)
    val cancelandoId: StateFlow<String?> = _cancelandoId.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                repo.obtenerInscripciones()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ocurrió un error inesperado"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cancelar(inscripcionId: String) {
        if (_cancelandoId.value != null) return
        viewModelScope.launch {
            _cancelandoId.value = inscripcionId
            try {
                repo.cancelarInscripcion(inscripcionId)
                _mensaje.value = "Inscripción cancelada. Los cupos quedaron libres para otra familia."
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo cancelar"
            } finally {
                _cancelandoId.value = null
            }
        }
    }

    fun mensajeMostrado() { _mensaje.value = null }
}
