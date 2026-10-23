package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Familia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvisoPrivacidadViewModel(private val repo: FamiliasRepository) : ViewModel() {
    val familia: StateFlow<Familia> = repo.familia
    val sesionIniciada: StateFlow<Boolean> = repo.sesionIniciada

    private val _borrando = MutableStateFlow(false)
    val borrando: StateFlow<Boolean> = _borrando.asStateFlow()

    private val _cuentaCerrada = MutableStateFlow(false)
    val cuentaCerrada: StateFlow<Boolean> = _cuentaCerrada.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cerrarCuenta() {
        if (_borrando.value) return
        viewModelScope.launch {
            _borrando.value = true
            try {
                repo.cerrarCuenta()
                _cuentaCerrada.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "No se pudo eliminar la cuenta"
            } finally {
                _borrando.value = false
            }
        }
    }

    fun errorMostrado() { _error.value = null }
}
