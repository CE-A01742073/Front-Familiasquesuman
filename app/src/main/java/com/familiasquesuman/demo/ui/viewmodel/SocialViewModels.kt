package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.EstadoLike
import com.familiasquesuman.demo.data.SocialRepository
import com.familiasquesuman.demo.model.Comentario
import com.familiasquesuman.demo.model.Conversacion
import com.familiasquesuman.demo.model.Mensaje
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// Likes + comentarios de un objetivo (actividad, campaña, proyecto o testimonio)
data class SocialUiState(
    val like: EstadoLike,
    val comentarios: UiState<List<Comentario>> = UiState.Cargando,
    val texto: String = "",
    val enviando: Boolean = false,
    val mensaje: String? = null
)

class SocialViewModel(
    private val repo: SocialRepository,
    private val tipo: String,
    private val objetivoId: String,
    likes: Int,
    meGusta: Boolean
) : ViewModel() {

    private val _estado = MutableStateFlow(SocialUiState(EstadoLike(likes, meGusta)))
    val estado: StateFlow<SocialUiState> = _estado.asStateFlow()

    init { cargarComentarios() }

    fun actualizarLike(likes: Int, meGusta: Boolean) {
        _estado.update { it.copy(like = EstadoLike(likes, meGusta)) }
    }

    fun cargarComentarios() {
        viewModelScope.launch {
            _estado.update { it.copy(comentarios = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.obtenerComentarios(tipo, objetivoId) }
            _estado.update { it.copy(comentarios = r) }
        }
    }

    fun alternarLike() {
        val actual = _estado.value.like
        val optimista = EstadoLike((actual.likes + if (actual.meGusta) -1 else 1).coerceAtLeast(0), !actual.meGusta)
        _estado.update { it.copy(like = optimista) }
        viewModelScope.launch {
            try {
                val r = repo.alternarLike(tipo, objetivoId, actual)
                _estado.update { it.copy(like = r) }
            } catch (e: Exception) {
                _estado.update { it.copy(like = actual, mensaje = e.message) }
            }
        }
    }

    fun cambiarTexto(t: String) = _estado.update { it.copy(texto = t) }

    fun comentar() {
        val t = _estado.value.texto
        if (t.isBlank() || _estado.value.enviando) return
        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            try {
                val c = repo.comentar(tipo, objetivoId, t)
                val aviso = when {
                    c == null -> "Sin conexión: tu comentario se enviará cuando haya internet."
                    c.estado == "pendiente" -> "Gracias. Tu comentario se publicará cuando el equipo lo revise."
                    else -> "Comentario publicado."
                }
                _estado.update { s ->
                    val lista = (s.comentarios as? UiState.Datos)?.datos ?: emptyList()
                    s.copy(
                        texto = "",
                        mensaje = aviso,
                        comentarios = if (c == null) s.comentarios else UiState.Datos(listOf(c) + lista)
                    )
                }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message ?: "No se pudo comentar") }
            } finally {
                _estado.update { it.copy(enviando = false) }
            }
        }
    }

    fun borrarComentario(id: String) {
        viewModelScope.launch {
            try {
                repo.borrarComentario(id)
                _estado.update { s ->
                    val lista = ((s.comentarios as? UiState.Datos)?.datos ?: emptyList()).filter { it.id != id }
                    s.copy(comentarios = if (lista.isEmpty()) UiState.Vacio else UiState.Datos(lista))
                }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun reportar(objetivoTipo: String, id: String, motivo: String) {
        viewModelScope.launch {
            try {
                repo.reportar(objetivoTipo, id, motivo)
                _estado.update { it.copy(mensaje = "Gracias, el equipo revisará el reporte.") }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }
}

class ConversacionesViewModel(private val repo: SocialRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<List<Conversacion>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Conversacion>>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            _estado.value = cargarEstado(esVacio = { it.isEmpty() }) { repo.conversaciones() }
        }
    }
}

// Chat con sondeo cada 5 s usando `desde` (solo mensajes nuevos)
data class ChatUiState(
    val mensajes: UiState<List<Mensaje>> = UiState.Cargando,
    val texto: String = "",
    val enviando: Boolean = false,
    val bloqueada: Boolean = false,
    val mensaje: String? = null
)

class ChatViewModel(
    private val repo: SocialRepository,
    private val conversacionId: String?,
    private val centroId: String?
) : ViewModel() {

    private val _estado = MutableStateFlow(ChatUiState())
    val estado: StateFlow<ChatUiState> = _estado.asStateFlow()

    private var idActual: String? = conversacionId
    private var ultimo: String? = null

    init {
        if (idActual != null) cargar() else _estado.update { it.copy(mensajes = UiState.Vacio) }
        viewModelScope.launch {
            while (isActive) {
                delay(5_000)
                val id = idActual ?: continue
                runCatching { repo.mensajes(id, ultimo) }.getOrNull()?.let { nuevos -> if (nuevos.isNotEmpty()) agregar(nuevos) }
            }
        }
    }

    fun cargar() {
        val id = idActual ?: return
        viewModelScope.launch {
            _estado.update { it.copy(mensajes = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.mensajes(id, null) }
            if (r is UiState.Datos) ultimo = r.datos.lastOrNull()?.creadoEn
            _estado.update { it.copy(mensajes = r) }
        }
    }

    private fun agregar(nuevos: List<Mensaje>) {
        _estado.update { s ->
            val actuales = (s.mensajes as? UiState.Datos)?.datos ?: emptyList()
            val ids = actuales.map { it.id }.toSet()
            val lista = actuales + nuevos.filter { it.id !in ids }
            ultimo = lista.lastOrNull()?.creadoEn ?: ultimo
            s.copy(mensajes = UiState.Datos(lista))
        }
    }

    fun cambiarTexto(t: String) = _estado.update { it.copy(texto = t) }

    fun enviar() {
        val t = _estado.value.texto
        if (t.isBlank() || _estado.value.enviando) return
        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            try {
                val id = idActual
                if (id == null) {
                    val c = repo.iniciarConversacion(centroId ?: "", t)
                    idActual = c.id
                    _estado.update { it.copy(texto = "") }
                    cargar()
                } else {
                    val m = repo.enviarMensaje(id, t)
                    _estado.update { it.copy(texto = "") }
                    agregar(listOf(m))
                }
            } catch (e: Exception) {
                val bloqueada = e.message?.contains("bloqueada", ignoreCase = true) == true
                _estado.update { it.copy(mensaje = e.message ?: "No se pudo enviar", bloqueada = it.bloqueada || bloqueada) }
            } finally {
                _estado.update { it.copy(enviando = false) }
            }
        }
    }

    fun bloquear() {
        val id = idActual ?: return
        viewModelScope.launch {
            try {
                repo.bloquear(id)
                _estado.update { it.copy(bloqueada = true, mensaje = "Conversación bloqueada. Ya no recibirás mensajes de este centro.") }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun reportar(mensajeId: String) {
        viewModelScope.launch {
            try {
                repo.reportar("mensaje", mensajeId, "Mensaje inapropiado reportado desde la app")
                _estado.update { it.copy(mensaje = "Gracias, el equipo revisará el reporte.") }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }
}
