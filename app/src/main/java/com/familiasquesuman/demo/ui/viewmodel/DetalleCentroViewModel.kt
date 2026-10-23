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
import kotlinx.coroutines.launch

data class DetalleCentroUiState(
    val centro: Centro,
    val actividades: List<Actividad>
)

class DetalleCentroViewModel(
    private val repo: FamiliasRepository,
    private val centroId: String
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<DetalleCentroUiState>>(UiState.Cargando)
    val estado: StateFlow<UiState<DetalleCentroUiState>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            _estado.value = cargarEstado {
                DetalleCentroUiState(
                    centro = repo.obtenerCentro(centroId),
                    actividades = repo.obtenerActividadesDeCentro(centroId)
                )
            }
        }
    }
}
