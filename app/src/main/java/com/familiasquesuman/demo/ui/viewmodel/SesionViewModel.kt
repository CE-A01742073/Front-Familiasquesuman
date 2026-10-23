package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.data.remote.ApiException
import com.familiasquesuman.demo.model.Ciudad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Ciudad, login (RF-17/RF-20), registro (RF-16) y Google Sign-In

data class FormularioSesion(
    val nombre: String = "",
    val nombreFamilia: String = "",
    val correo: String = "",
    val password: String = "",
    val mostrarPassword: Boolean = false,
    val aceptaAviso: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val pedirFamiliaGoogle: Boolean = false
)

class SesionViewModel(private val repo: FamiliasRepository) : ViewModel() {
    val ciudad: StateFlow<Ciudad> = repo.ciudad

    private val _formulario = MutableStateFlow(FormularioSesion())
    val formulario: StateFlow<FormularioSesion> = _formulario.asStateFlow()

    private val _sesionLista = MutableStateFlow(false)
    val sesionLista: StateFlow<Boolean> = _sesionLista.asStateFlow()

    private var tokenGoogle: String? = null

    fun seleccionarCiudad(ciudad: Ciudad) = repo.seleccionarCiudad(ciudad)

    fun cambiarNombre(v: String) = _formulario.update { it.copy(nombre = v, error = null) }
    fun cambiarNombreFamilia(v: String) = _formulario.update { it.copy(nombreFamilia = v, error = null) }
    fun cambiarCorreo(v: String) = _formulario.update { it.copy(correo = v, error = null) }
    fun cambiarPassword(v: String) = _formulario.update { it.copy(password = v, error = null) }
    fun alternarMostrarPassword() = _formulario.update { it.copy(mostrarPassword = !it.mostrarPassword) }
    fun cambiarAceptaAviso(v: Boolean) = _formulario.update { it.copy(aceptaAviso = v, error = null) }

    fun iniciarSesion() = ejecutar { f -> repo.iniciarSesion(f.correo, f.password) }

    fun registrar() = ejecutar { f -> repo.registrar(f.nombre, f.nombreFamilia, f.correo, f.password, f.aceptaAviso) }

    // Primer intento sin nombre de familia; si es cuenta nueva el servidor lo pide (422) y se muestra el diálogo
    fun iniciarSesionGoogle(idToken: String) {
        tokenGoogle = idToken
        ejecutar(
            siFalla = { e ->
                if (e is ApiException && e.status == 422 && e.message?.contains("familia", ignoreCase = true) == true) {
                    _formulario.update { it.copy(cargando = false, error = null, pedirFamiliaGoogle = true) }
                    true
                } else false
            }
        ) { repo.iniciarSesionGoogle(idToken, null) }
    }

    fun confirmarFamiliaGoogle() {
        val token = tokenGoogle ?: return
        ejecutar { f -> repo.iniciarSesionGoogle(token, f.nombreFamilia) }
    }

    fun cancelarFamiliaGoogle() {
        tokenGoogle = null
        _formulario.update { it.copy(pedirFamiliaGoogle = false, error = null) }
    }

    fun errorGoogle(mensaje: String) = _formulario.update { it.copy(cargando = false, error = mensaje) }

    private fun ejecutar(siFalla: (Exception) -> Boolean = { false }, accion: suspend (FormularioSesion) -> Unit) {
        val f = _formulario.value
        if (f.cargando) return
        viewModelScope.launch {
            _formulario.update { it.copy(cargando = true, error = null) }
            try {
                accion(f)
                _formulario.update { it.copy(pedirFamiliaGoogle = false) }
                _sesionLista.value = true
            } catch (e: Exception) {
                if (!siFalla(e)) _formulario.update { it.copy(cargando = false, error = e.message ?: "No se pudo iniciar sesión") }
            }
        }
    }
}
