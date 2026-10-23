package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Inscripcion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ConfirmacionViewModel(repo: FamiliasRepository, actividadId: String) : ViewModel() {
    val inscripcion: StateFlow<Inscripcion?> = repo.inscripciones
        .map { lista -> lista.firstOrNull { it.actividadId == actividadId } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, repo.inscripciones.value.firstOrNull { it.actividadId == actividadId })
}
