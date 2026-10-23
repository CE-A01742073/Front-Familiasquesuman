package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.Integrante
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InscripcionUiState(
    val actividad: Actividad,
    val integrantes: List<Integrante>,
    val elegibles: Set<String>
)

data class FormularioInscripcion(
    val seleccionados: Set<String> = emptySet(),
    val comentario: String = "",
    val enviando: Boolean = false,
    val error: String? = null
) {
    val puedeConfirmar: Boolean get() = seleccionados.isNotEmpty() && !enviando
}

class InscripcionViewModel(
    private val repo: FamiliasRepository,
    private val actividadId: String
) : ViewModel() {

    private val _datos = MutableStateFlow<UiState<InscripcionUiState>>(UiState.Cargando)
    val datos: StateFlow<UiState<InscripcionUiState>> = _datos.asStateFlow()

    private val _formulario = MutableStateFlow(FormularioInscripcion())
    val formulario: StateFlow<FormularioInscripcion> = _formulario.asStateFlow()

    private val _inscripcionRealizada = MutableStateFlow(false)
    val inscripcionRealizada: StateFlow<Boolean> = _inscripcionRealizada.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _datos.value = UiState.Cargando
            _datos.value = cargarEstado {
                val actividad = repo.obtenerActividad(actividadId)
                val elegibles = repo.integrantesElegibles(actividad).map { it.id }.toSet()
                _formulario.update { it.copy(seleccionados = elegibles) }
                InscripcionUiState(actividad, repo.familia.value.integrantes, elegibles)
            }
        }
    }

    fun alternarIntegrante(id: String) {
        val elegibles = (_datos.value as? UiState.Datos)?.datos?.elegibles ?: return
        if (id !in elegibles) return
        _formulario.update { f ->
            f.copy(seleccionados = if (id in f.seleccionados) f.seleccionados - id else f.seleccionados + id)
        }
    }

    fun cambiarComentario(texto: String) {
        _formulario.update { it.copy(comentario = texto, error = null) }
    }

    fun confirmar() {
        val f = _formulario.value
        if (!f.puedeConfirmar) return
        viewModelScope.launch {
            _formulario.update { it.copy(enviando = true, error = null) }
            try {
                val actividad = (_datos.value as? UiState.Datos)?.datos?.actividad ?: repo.obtenerActividad(actividadId)
                repo.inscribir(actividad, f.seleccionados.toList(), f.comentario)
                _inscripcionRealizada.value = true
            } catch (e: Exception) {
                _formulario.update { it.copy(enviando = false, error = e.message ?: "No se pudo inscribir") }
            }
        }
    }
}
