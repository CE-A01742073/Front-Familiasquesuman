package com.familiasquesuman.demo.ui

import kotlinx.coroutines.CancellationException

/* Todos los estados,
se crean genericos y se usa el sealed */

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data object Vacio : UiState<Nothing>
    data class Error(val mensaje: String) : UiState<Nothing>
    data class Datos<T>(val datos: T) : UiState<T>
}

suspend fun <T> cargarEstado(
    esVacio: (T) -> Boolean = { false },
    bloque: suspend () -> T
): UiState<T> = try {
    val datos = bloque()
    if (esVacio(datos)) UiState.Vacio else UiState.Datos(datos)
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    UiState.Error(e.message ?: "Ocurrió un error inesperado")
}
