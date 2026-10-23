package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Notificacion
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificacionesViewModel(private val repo: FamiliasRepository) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Notificacion>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Notificacion>>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            _estado.value = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerNotificaciones() }
        }
    }

    private fun actualizar(cambio: (Notificacion) -> Notificacion) =
        _estado.update { s -> if (s is UiState.Datos) UiState.Datos(s.datos.map(cambio)) else s }

    // Deslizar alterna el estado; se guarda en el teléfono (ver FamiliasRepository.marcarNotificacion)
    fun alternarLeida(id: String) {
        val actual = (_estado.value as? UiState.Datos)?.datos ?: return
        val n = actual.firstOrNull { it.id == id } ?: return
        val nueva = !n.leida
        actualizar { if (it.id == id) it.copy(leida = nueva) else it }
        val quedan = (_estado.value as? UiState.Datos)?.datos?.any { !it.leida } ?: false
        viewModelScope.launch { runCatching { repo.marcarNotificacion(id, nueva, quedan) } }
    }

    fun marcarTodasLeidas() {
        actualizar { it.copy(leida = true) }
        viewModelScope.launch { runCatching { repo.marcarTodasLeidas() } }
    }
}
