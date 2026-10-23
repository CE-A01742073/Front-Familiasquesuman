package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

data class DetalleActividadUiState(
    val actividad: Actividad,
    val centro: Centro,
    val yaInscrito: Boolean,
    val realizada: Boolean
) {
    val lleno: Boolean get() = actividad.cuposDisponibles == 0
}

class DetalleActividadViewModel(
    private val repo: FamiliasRepository,
    private val actividadId: String
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<DetalleActividadUiState>>(UiState.Cargando)
    val estado: StateFlow<UiState<DetalleActividadUiState>> = _estado.asStateFlow()

    init {
        cargar()
        viewModelScope.launch { repo.inscripciones.drop(1).collect { cargar(mostrarCarga = false) } }
    }

    fun cargar(mostrarCarga: Boolean = true) {
        viewModelScope.launch {
            if (mostrarCarga) _estado.value = UiState.Cargando
            _estado.value = cargarEstado {
                val actividad = repo.obtenerActividad(actividadId)
                DetalleActividadUiState(
                    actividad = actividad,
                    centro = repo.obtenerCentro(actividad.centroId),
                    yaInscrito = repo.yaInscrito(actividadId),
                    realizada = repo.actividadRealizada(actividadId)
                )
            }
        }
    }
}
