package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.EstadoLike
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.data.SocialRepository
import com.familiasquesuman.demo.model.TipoObjetivo
import kotlinx.coroutines.flow.update
import com.familiasquesuman.demo.model.Campana
import com.familiasquesuman.demo.model.CategoriaDonacion
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

enum class ModoDonar { CAMPANA, ESPECIE }

class DonarViewModel(private val repo: FamiliasRepository, private val social: SocialRepository) : ViewModel() {

    private val _modo = MutableStateFlow(ModoDonar.CAMPANA)
    val modo: StateFlow<ModoDonar> = _modo.asStateFlow()

    private val _categoria = MutableStateFlow(CategoriaDonacion.TODOS)
    val categoria: StateFlow<CategoriaDonacion> = _categoria.asStateFlow()

    private val _campanas = MutableStateFlow<UiState<List<Campana>>>(UiState.Cargando)
    private val _centros = MutableStateFlow<UiState<List<Centro>>>(UiState.Cargando)

    private val _enviandoId = MutableStateFlow<String?>(null)
    val enviandoId: StateFlow<String?> = _enviandoId.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    val campanas: StateFlow<UiState<List<Campana>>> =
        combine(_campanas, repo.donaciones) { estado, donaciones ->
            if (estado !is UiState.Datos) return@combine estado
            val apoyadas = donaciones.mapNotNull { it.campanaId }.toSet()
            UiState.Datos(estado.datos.map { it.copy(yaApoye = it.yaApoye || it.id in apoyadas) })
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    val centros: StateFlow<UiState<List<Centro>>> =
        combine(_centros, _categoria) { estado, cat ->
            if (estado !is UiState.Datos) return@combine estado
            val lista = estado.datos
                .filter { it.acepta.isNotEmpty() }
                .filter { cat == CategoriaDonacion.TODOS || cat in it.acepta }
            if (lista.isEmpty()) UiState.Vacio else UiState.Datos(lista)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    init {
        viewModelScope.launch { repo.ciudad.collect { cargar() } }
    }

    fun cargar() {
        viewModelScope.launch {
            _campanas.value = UiState.Cargando
            _campanas.value = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerCampanas(repo.ciudad.value) }
        }
        viewModelScope.launch {
            _centros.value = UiState.Cargando
            _centros.value = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerCentros(repo.ciudad.value) }
        }
    }

    fun cambiarModo(modo: ModoDonar) { _modo.value = modo }
    fun cambiarCategoria(categoria: CategoriaDonacion) { _categoria.value = categoria }

    fun ayudar(campana: Campana) {
        if (_enviandoId.value != null) return
        viewModelScope.launch {
            _enviandoId.value = campana.id
            try {
                repo.ayudarCampana(campana)
                _mensaje.value = "Registramos tu apoyo a \"${campana.titulo}\". ${campana.centroNombre} te contactará para coordinar."
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo registrar"
            } finally {
                _enviandoId.value = null
            }
        }
    }

    fun alternarLike(c: Campana) {
        val actual = EstadoLike(c.likes, c.meGusta)
        fun aplicar(e: EstadoLike) = _campanas.update { s ->
            if (s is UiState.Datos) UiState.Datos(s.datos.map { if (it.id == c.id) it.copy(likes = e.likes, meGusta = e.meGusta) else it }) else s
        }
        aplicar(EstadoLike((c.likes + if (c.meGusta) -1 else 1).coerceAtLeast(0), !c.meGusta))
        viewModelScope.launch {
            try {
                aplicar(social.alternarLike(TipoObjetivo.CAMPANA, c.id, actual))
            } catch (e: Exception) {
                aplicar(actual)
                _mensaje.value = e.message
            }
        }
    }

    fun mensajeMostrado() { _mensaje.value = null }
}
