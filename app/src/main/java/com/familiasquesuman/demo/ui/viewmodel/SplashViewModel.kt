package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class DestinoInicial { CIUDAD, LOGIN, HOME }

class SplashViewModel(private val repo: FamiliasRepository) : ViewModel() {

    private val _destino = MutableStateFlow<DestinoInicial?>(null)
    val destino: StateFlow<DestinoInicial?> = _destino.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1200)
            repo.listo.first { it }
            _destino.value = when {
                !repo.ciudadElegida.value -> DestinoInicial.CIUDAD
                !repo.sesionIniciada.value -> DestinoInicial.LOGIN
                else -> DestinoInicial.HOME
            }
        }
    }
}
