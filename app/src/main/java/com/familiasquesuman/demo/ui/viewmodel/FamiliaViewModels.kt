package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.CaminoSolidario
import com.familiasquesuman.demo.model.EncuestaRespondida
import com.familiasquesuman.demo.model.Inscripcion
import com.familiasquesuman.demo.model.Pregunta
import com.familiasquesuman.demo.model.Testimonio
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

// ---- Encuesta antes / después (RF-13) ----

data class EncuestaUiState(
    val preguntas: UiState<List<Pregunta>> = UiState.Cargando,
    val respuestas: Map<String, String> = emptyMap(),
    val enviando: Boolean = false,
    val enviada: Boolean = false,
    val error: String? = null
)

class EncuestaViewModel(
    private val repo: FamiliasRepository,
    private val inscripcionId: String,
    val momento: String
) : ViewModel() {

    private val _estado = MutableStateFlow(EncuestaUiState())
    val estado: StateFlow<EncuestaUiState> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(preguntas = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerPreguntas(momento) }
            _estado.update { it.copy(preguntas = r) }
        }
    }

    fun responder(clave: String, valor: String) {
        _estado.update { it.copy(respuestas = it.respuestas + (clave to valor), error = null) }
    }

    fun enviar() {
        val s = _estado.value
        val preguntas = (s.preguntas as? UiState.Datos)?.datos ?: return
        if (s.enviando) return
        val faltantes = preguntas.filter { s.respuestas[it.clave].isNullOrBlank() }
        if (faltantes.isNotEmpty()) {
            _estado.update { it.copy(error = "Falta responder: ${faltantes.first().pregunta}") }
            return
        }
        val cuerpo: Map<String, JsonElement> = preguntas.associate { p ->
            val v = s.respuestas[p.clave].orEmpty()
            p.clave to if (p.tipo == "escala") JsonPrimitive(v.toIntOrNull() ?: 3) else JsonPrimitive(v)
        }
        viewModelScope.launch {
            _estado.update { it.copy(enviando = true, error = null) }
            try {
                repo.responderEncuesta(inscripcionId, momento, cuerpo)
                _estado.update { it.copy(enviada = true) }
            } catch (e: Exception) {
                _estado.update { it.copy(error = e.message ?: "No se pudo enviar") }
            } finally {
                _estado.update { it.copy(enviando = false) }
            }
        }
    }
}

// ---- Encuestas ya respondidas de una inscripción (RF-13) ----

class EncuestasRespondidasViewModel(private val repo: FamiliasRepository, private val inscripcionId: String) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<List<EncuestaRespondida>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<EncuestaRespondida>>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            _estado.value = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerEncuestasRespondidas(inscripcionId) }
        }
    }
}

// ---- Insignias y niveles (RF-14) ----

class InsigniasViewModel(private val repo: FamiliasRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<CaminoSolidario>>(UiState.Cargando)
    val estado: StateFlow<UiState<CaminoSolidario>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            _estado.value = cargarEstado { repo.obtenerInsignias() }
        }
    }
}

// ---- Testimonios (RF-12): los míos, los publicados, y enviar uno nuevo ----

data class GaleriaUiState(
    val mios: UiState<List<Testimonio>> = UiState.Cargando,
    val publicos: UiState<List<Testimonio>> = UiState.Cargando,
    val actividadesParaTestimonio: List<Inscripcion> = emptyList(),
    val enviando: Boolean = false,
    val mensaje: String? = null
)

class GaleriaViewModel(private val repo: FamiliasRepository) : ViewModel() {
    private val _estado = MutableStateFlow(GaleriaUiState())
    val estado: StateFlow<GaleriaUiState> = _estado.asStateFlow()

    init {
        cargar()
        viewModelScope.launch { repo.inscripciones.collect { lista -> _estado.update { it.copy(actividadesParaTestimonio = lista.filter { i -> i.pasada && i.estado != "cancelada" }) } } }
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(mios = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerMisTestimonios() }
            _estado.update { it.copy(mios = r) }
        }
        viewModelScope.launch {
            _estado.update { it.copy(publicos = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerTestimoniosPublicos() }
            _estado.update { it.copy(publicos = r) }
        }
    }

    fun enviar(actividadId: String, texto: String, foto: ByteArray?) {
        if (_estado.value.enviando) return
        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            try {
                val t = repo.crearTestimonio(actividadId, texto, foto)
                _estado.update { s ->
                    val lista = (s.mios as? UiState.Datos)?.datos ?: emptyList()
                    s.copy(mios = UiState.Datos(listOf(t) + lista), mensaje = "¡Gracias! El equipo revisará tu testimonio antes de publicarlo.")
                }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message ?: "No se pudo enviar") }
            } finally {
                _estado.update { it.copy(enviando = false) }
            }
        }
    }

    fun borrar(id: String) {
        viewModelScope.launch {
            try {
                repo.borrarTestimonio(id)
                _estado.update { s ->
                    val lista = ((s.mios as? UiState.Datos)?.datos ?: emptyList()).filter { it.id != id }
                    s.copy(mios = if (lista.isEmpty()) UiState.Vacio else UiState.Datos(lista), mensaje = "Testimonio eliminado")
                }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }
}
