package com.familiasquesuman.demo.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.familiasquesuman.demo.data.local.PendienteEntity
import com.familiasquesuman.demo.data.local.PendientesDao
import com.familiasquesuman.demo.data.remote.jsonApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import java.util.UUID
import java.util.concurrent.TimeUnit

object TipoOperacion {
    const val INSCRIBIR = "inscribir"
    const val CANCELAR = "cancelar"
    const val DONAR = "donar"
    const val ENCUESTA = "encuesta"
    const val LIKE = "like"
    const val COMENTAR = "comentar"
}

// Una operación serializada en la cola. Solo se llenan los campos del tipo correspondiente.
@Serializable
data class OperacionPendiente(
    val tipo: String,
    val actividadId: String? = null,
    val integrantesIds: List<String> = emptyList(),
    val comentario: String? = null,
    val inscripcionId: String? = null,
    val campanaId: String? = null,
    val detalle: String? = null,
    val momento: String? = null,
    val respuestas: Map<String, JsonElement>? = null,
    val objetivoTipo: String? = null,
    val objetivoId: String? = null,
    val dar: Boolean = true,
    val texto: String? = null,
    val actividadTitulo: String? = null,
    val centroId: String? = null,
    val centroNombre: String? = null,
    val fecha: String? = null,
    val horaInicio: String? = null,
    val horaFin: String? = null,
    val lugar: String? = null,
    val nombres: List<String> = emptyList()
)

data class Pendiente(val clave: String, val op: OperacionPendiente)

class Outbox(private val dao: PendientesDao, private val context: Context) {

    val pendientes: Flow<List<Pendiente>> = dao.observar().map { lista ->
        lista.mapNotNull { p ->
            runCatching { Pendiente(p.clave, jsonApi.decodeFromString<OperacionPendiente>(p.payload)) }.getOrNull()
        }
    }

    suspend fun encolar(op: OperacionPendiente, descripcion: String): String {
        val clave = nuevaClave()
        dao.agregar(PendienteEntity(clave, op.tipo, jsonApi.encodeToString(op), descripcion, System.currentTimeMillis()))
        programar()
        return clave
    }

    suspend fun todos(): List<PendienteEntity> = dao.todos()
    suspend fun borrar(clave: String) = dao.borrar(clave)
    suspend fun registrarIntento(clave: String) = dao.registrarIntento(clave)
    suspend fun borrarTodo() = dao.borrarTodo()

    fun programar() {
        val trabajo = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork("sync-pendientes", ExistingWorkPolicy.APPEND_OR_REPLACE, trabajo)
    }

    companion object {
        fun nuevaClave(): String = UUID.randomUUID().toString().replace("-", "")
    }
}
