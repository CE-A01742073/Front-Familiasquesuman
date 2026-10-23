package com.familiasquesuman.demo.data

import com.familiasquesuman.demo.data.remote.ApiClient
import com.familiasquesuman.demo.data.remote.ApiException
import com.familiasquesuman.demo.data.remote.ComentarioIn
import com.familiasquesuman.demo.data.remote.ConversacionIn
import com.familiasquesuman.demo.data.remote.MensajeIn
import com.familiasquesuman.demo.data.remote.ReporteIn
import com.familiasquesuman.demo.data.remote.llamarApi
import com.familiasquesuman.demo.data.sync.OperacionPendiente
import com.familiasquesuman.demo.data.sync.Outbox
import com.familiasquesuman.demo.data.sync.TipoOperacion
import com.familiasquesuman.demo.model.Comentario
import com.familiasquesuman.demo.model.Conversacion
import com.familiasquesuman.demo.model.Mensaje

data class EstadoLike(val likes: Int, val meGusta: Boolean)

// Likes, comentarios moderados, reportes y mensajería familia ↔ asociación
class SocialRepository(private val cliente: ApiClient, private val outbox: Outbox) {
    private val api get() = cliente.api

    // Sin red el like se guarda en la cola y la UI se queda con el valor optimista
    suspend fun alternarLike(tipo: String, id: String, actual: EstadoLike): EstadoLike {
        val dar = !actual.meGusta
        val optimista = EstadoLike(likes = (actual.likes + if (dar) 1 else -1).coerceAtLeast(0), meGusta = dar)
        return try {
            val r = llamarApi { if (dar) api.darLike(tipo, id) else api.quitarLike(tipo, id) }
            EstadoLike(r.likes, r.meGusta)
        } catch (e: ApiException) {
            if (e.status != 0) throw e
            outbox.encolar(OperacionPendiente(tipo = TipoOperacion.LIKE, objetivoTipo = tipo, objetivoId = id, dar = dar), "Me gusta")
            optimista
        }
    }

    suspend fun obtenerComentarios(tipo: String, id: String): List<Comentario> =
        llamarApi { api.comentarios(tipo, id) }.items.map { it.aModelo() }

    // Devuelve null si quedó en la cola (sin red)
    suspend fun comentar(tipo: String, id: String, texto: String): Comentario? {
        val t = texto.trim()
        require(t.isNotEmpty()) { "Escribe un comentario" }
        require(t.length <= 400) { "El comentario es demasiado largo" }
        return try {
            llamarApi { api.comentar(tipo, id, ComentarioIn(t)) }.aModelo()
        } catch (e: ApiException) {
            if (e.status != 0) throw e
            outbox.encolar(OperacionPendiente(tipo = TipoOperacion.COMENTAR, objetivoTipo = tipo, objetivoId = id, texto = t), "Comentario")
            null
        }
    }

    suspend fun borrarComentario(id: String) {
        llamarApi { api.borrarComentario(id) }
    }

    suspend fun reportar(objetivoTipo: String, objetivoId: String, motivo: String) {
        require(motivo.trim().length >= 3) { "Describe brevemente el motivo" }
        llamarApi { api.reportar(ReporteIn(objetivoTipo, objetivoId, motivo.trim().take(300))) }
    }

    suspend fun conversaciones(): List<Conversacion> =
        llamarApi { api.conversaciones() }.map { it.aModelo() }

    suspend fun iniciarConversacion(centroId: String, texto: String): Conversacion {
        require(texto.trim().isNotEmpty()) { "Escribe tu mensaje" }
        return llamarApi { api.iniciarConversacion(ConversacionIn(centroId, texto.trim())) }.aModelo()
    }

    suspend fun mensajes(conversacionId: String, desde: String?): List<Mensaje> =
        llamarApi { api.mensajes(conversacionId, desde) }.map { it.aModelo() }

    suspend fun enviarMensaje(conversacionId: String, texto: String): Mensaje {
        require(texto.trim().isNotEmpty()) { "Escribe tu mensaje" }
        return llamarApi { api.enviarMensaje(conversacionId, MensajeIn(texto.trim().take(1000))) }.aModelo()
    }

    suspend fun bloquear(conversacionId: String) {
        llamarApi { api.bloquearConversacion(conversacionId) }
    }
}
