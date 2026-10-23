package com.familiasquesuman.demo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.familiasquesuman.demo.model.Ciudad
import com.familiasquesuman.demo.model.Donacion
import com.familiasquesuman.demo.model.Familia
import com.familiasquesuman.demo.model.Inscripcion
import com.familiasquesuman.demo.model.ModoTema
import com.familiasquesuman.demo.ui.widget.ProximaActividadWidget
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Preferencias y copia local de lo que es de la familia (para verlo sin conexión)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "familias_que_suman")

data class DatosGuardados(
    val ciudad: Ciudad,
    val ciudadElegida: Boolean,
    val inscripciones: List<Inscripcion>,
    val donaciones: List<Donacion>,
    val fotosPendientes: Int,
    val familia: Familia?,
    val modoTema: ModoTema,
    val avisos: List<String>,
    val fotoPerfilLocal: String?
)

class AlmacenLocal(private val context: Context) {

    private val CIUDAD = stringPreferencesKey("ciudad")
    private val CIUDAD_ELEGIDA = booleanPreferencesKey("ciudad_elegida")
    private val INSCRIPCIONES = stringPreferencesKey("inscripciones")
    private val DONACIONES = stringPreferencesKey("donaciones")
    private val FOTOS = intPreferencesKey("fotos_pendientes")
    private val FAMILIA = stringPreferencesKey("familia")
    private val TEMA = stringPreferencesKey("modo_tema")
    private val AVISOS = stringPreferencesKey("avisos")
    private val FOTO_PERFIL = stringPreferencesKey("foto_perfil_local")
    private val NOTIF_LEIDAS = stringPreferencesKey("notificaciones_leidas")
    private val NOTIF_NO_LEIDAS = stringPreferencesKey("notificaciones_no_leidas")
    private val NOTIF_AVISADAS = stringPreferencesKey("notificaciones_avisadas")

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun leer(): DatosGuardados {
        val p = context.dataStore.data.first()
        return DatosGuardados(
            ciudad = Ciudad.desdeClave(p[CIUDAD]),
            ciudadElegida = p[CIUDAD_ELEGIDA] ?: false,
            inscripciones = p[INSCRIPCIONES]?.let { decodificar<List<Inscripcion>>(it) } ?: emptyList(),
            donaciones = p[DONACIONES]?.let { decodificar<List<Donacion>>(it) } ?: emptyList(),
            fotosPendientes = p[FOTOS] ?: 0,
            familia = p[FAMILIA]?.let { decodificar<Familia>(it) },
            modoTema = p[TEMA]?.let { t -> ModoTema.entries.firstOrNull { it.name == t } } ?: ModoTema.SISTEMA,
            avisos = p[AVISOS]?.let { decodificar<List<String>>(it) } ?: emptyList(),
            fotoPerfilLocal = p[FOTO_PERFIL]
        )
    }

    private inline fun <reified T> decodificar(texto: String): T? =
        runCatching { json.decodeFromString<T>(texto) }.getOrNull()

    suspend fun guardarCiudad(ciudad: Ciudad) = context.dataStore.edit {
        it[CIUDAD] = ciudad.clave
        it[CIUDAD_ELEGIDA] = true
    }

    suspend fun guardarInscripciones(lista: List<Inscripcion>) {
        context.dataStore.edit { it[INSCRIPCIONES] = json.encodeToString(lista) }
        runCatching { ProximaActividadWidget().actualizar(context) }
    }

    suspend fun guardarDonaciones(lista: List<Donacion>) = context.dataStore.edit {
        it[DONACIONES] = json.encodeToString(lista)
    }

    suspend fun guardarFotos(cantidad: Int) = context.dataStore.edit { it[FOTOS] = cantidad }

    suspend fun guardarFamilia(familia: Familia) = context.dataStore.edit {
        it[FAMILIA] = json.encodeToString(familia)
    }

    suspend fun guardarTema(modo: ModoTema) = context.dataStore.edit { it[TEMA] = modo.name }

    suspend fun guardarAvisos(lista: List<String>) = context.dataStore.edit { it[AVISOS] = json.encodeToString(lista) }

    suspend fun guardarFotoPerfilLocal(ruta: String?) = context.dataStore.edit {
        if (ruta == null) it.remove(FOTO_PERFIL) else it[FOTO_PERFIL] = ruta
    }

    // Notificaciones marcadas como leídas una por una (el servidor solo sabe marcar todas)
    suspend fun notificacionesLeidas(): Set<String> =
        context.dataStore.data.first()[NOTIF_LEIDAS]?.let { decodificar<Set<String>>(it) } ?: emptySet()

    suspend fun guardarNotificacionesLeidas(ids: Set<String>) = context.dataStore.edit {
        if (ids.isEmpty()) it.remove(NOTIF_LEIDAS) else it[NOTIF_LEIDAS] = json.encodeToString(ids)
    }

    // Marcadas de nuevo como no leídas en el teléfono aunque el servidor ya las tenga leídas
    suspend fun notificacionesNoLeidas(): Set<String> =
        context.dataStore.data.first()[NOTIF_NO_LEIDAS]?.let { decodificar<Set<String>>(it) } ?: emptySet()

    suspend fun guardarNotificacionesNoLeidas(ids: Set<String>) = context.dataStore.edit {
        if (ids.isEmpty()) it.remove(NOTIF_NO_LEIDAS) else it[NOTIF_NO_LEIDAS] = json.encodeToString(ids)
    }

    // Ids ya mostrados como notificación del sistema (para no repetir). null = nunca se ha corrido
    suspend fun notificacionesAvisadas(): Set<String>? =
        context.dataStore.data.first()[NOTIF_AVISADAS]?.let { decodificar<Set<String>>(it) }

    suspend fun guardarNotificacionesAvisadas(ids: Set<String>) = context.dataStore.edit {
        it[NOTIF_AVISADAS] = json.encodeToString(ids)
    }

    suspend fun marcaSync(ciudad: Ciudad): String? =
        context.dataStore.data.first()[stringPreferencesKey("sync_${ciudad.clave}")]

    suspend fun guardarMarcaSync(ciudad: Ciudad, marca: String) = context.dataStore.edit {
        it[stringPreferencesKey("sync_${ciudad.clave}")] = marca
    }

    // Al cerrar sesión se conserva la ciudad; al borrar la cuenta se limpia todo
    suspend fun borrarDatosDeUsuario() = context.dataStore.edit {
        it.remove(INSCRIPCIONES)
        it.remove(DONACIONES)
        it.remove(FOTOS)
        it.remove(FAMILIA)
        it.remove(AVISOS)
        it.remove(FOTO_PERFIL)
        it.remove(NOTIF_LEIDAS)
        it.remove(NOTIF_NO_LEIDAS)
        it.remove(NOTIF_AVISADAS)
    }

    suspend fun borrarTodo() = context.dataStore.edit { it.clear() }
}
