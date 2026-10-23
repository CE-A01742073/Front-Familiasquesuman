package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Familia
import com.familiasquesuman.demo.model.ModoTema
import com.familiasquesuman.demo.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class PerfilUiState(
    val usuario: Usuario?,
    val familia: Familia,
    val fotoLocal: String?,
    val completadas: Int,
    val proximas: Int,
    val donaciones: Int,
    val pendientesPorEnviar: Int,
    val modoTema: ModoTema,
    val guardando: Boolean
)

class PerfilViewModel(private val repo: FamiliasRepository) : ViewModel() {

    private val _guardando = MutableStateFlow(false)

    private data class Extra(val fotoLocal: String?, val pendientes: Int, val tema: ModoTema, val guardando: Boolean)

    private val extra = combine(repo.fotoPerfilLocal, repo.pendientesPorEnviar, repo.modoTema, _guardando) { f, p, t, g -> Extra(f, p, t, g) }

    val estado: StateFlow<PerfilUiState> = combine(
        repo.usuario, repo.familia, repo.inscripciones, repo.donaciones, extra
    ) { usuario, familia, inscripciones, donaciones, ex ->
        PerfilUiState(
            usuario = usuario,
            familia = familia,
            fotoLocal = ex.fotoLocal,
            completadas = inscripciones.count { it.pasada && it.estado != "cancelada" },
            proximas = inscripciones.count { !it.pasada },
            donaciones = donaciones.size,
            pendientesPorEnviar = ex.pendientes,
            modoTema = ex.tema,
            guardando = ex.guardando
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PerfilUiState(repo.usuario.value, repo.familia.value, null, 0, 0, 0, 0, repo.modoTema.value, false)
    )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun mensajeMostrado() { _mensaje.value = null }

    private fun ejecutar(exito: String? = null, bloque: suspend () -> Unit) {
        viewModelScope.launch {
            _guardando.value = true
            try {
                bloque()
                if (exito != null) _mensaje.value = exito
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo guardar"
            } finally {
                _guardando.value = false
            }
        }
    }

    fun actualizarPerfil(nombre: String, descripcion: String) = ejecutar("Perfil actualizado") { repo.actualizarPerfil(nombre, descripcion) }

    fun subirFoto(bytes: ByteArray, carpeta: File) = ejecutar("Foto actualizada") {
        val ruta = withContext(Dispatchers.IO) { File(carpeta, "perfil.jpg").apply { writeBytes(bytes) }.absolutePath }
        repo.subirFotoPerfil(bytes, ruta)
    }

    fun actualizarFamilia(nombre: String, zona: String) = ejecutar { repo.actualizarFamilia(nombre, zona) }

    fun agregarIntegrante(nombre: String, edad: String) = ejecutar { repo.agregarIntegrante(nombre, edad.trim().toIntOrNull()) }

    fun editarIntegrante(id: String, nombre: String, edad: String) = ejecutar { repo.editarIntegrante(id, nombre, edad.trim().toIntOrNull()) }

    fun eliminarIntegrante(id: String) = ejecutar { repo.eliminarIntegrante(id) }

    fun cambiarTema(modo: ModoTema) = repo.cambiarTema(modo)

    fun cerrarSesion() = repo.cerrarSesion()
}
