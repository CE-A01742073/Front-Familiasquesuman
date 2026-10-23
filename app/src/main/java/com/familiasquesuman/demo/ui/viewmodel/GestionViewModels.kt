package com.familiasquesuman.demo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.data.GestionRepository
import com.familiasquesuman.demo.data.remote.ActividadIn
import com.familiasquesuman.demo.data.remote.ActividadPatch
import com.familiasquesuman.demo.data.remote.CampanaIn
import com.familiasquesuman.demo.data.remote.CampanaPatch
import com.familiasquesuman.demo.data.remote.ProyectoIn
import com.familiasquesuman.demo.data.remote.ProyectoPatch
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.AsistenteLista
import com.familiasquesuman.demo.model.Campana
import com.familiasquesuman.demo.model.Categoria
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.model.Comentario
import com.familiasquesuman.demo.model.Proyecto
import com.familiasquesuman.demo.model.ResumenAdmin
import com.familiasquesuman.demo.model.Testimonio
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.cargarEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

// ---- Panel de la asociación / admin: sus centros y las actividades de cada uno ----

data class GestionUiState(
    val centros: List<Centro> = emptyList(),
    val centroId: String? = null,
    val actividades: UiState<List<Actividad>> = UiState.Cargando,
    val campanas: List<Campana> = emptyList(),
    val proyectos: List<Proyecto> = emptyList(),
    val resumen: ResumenAdmin? = null,
    val mensaje: String? = null
)

class GestionViewModel(private val repo: GestionRepository, private val familias: FamiliasRepository) : ViewModel() {
    private val _estado = MutableStateFlow(GestionUiState())
    val estado: StateFlow<GestionUiState> = _estado.asStateFlow()
    val esAdmin: Boolean get() = familias.usuario.value?.esAdmin == true

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(actividades = UiState.Cargando) }
            try {
                val centros = repo.misCentros()
                val id = _estado.value.centroId ?: centros.firstOrNull()?.id
                val resumen = if (esAdmin) runCatching { repo.resumenAdmin() }.getOrNull() else null
                _estado.update { it.copy(centros = centros, centroId = id, resumen = resumen) }
                cargarActividades()
            } catch (e: Exception) {
                _estado.update { it.copy(actividades = UiState.Error(e.message ?: "Error")) }
            }
        }
    }

    fun elegirCentro(id: String) {
        _estado.update { it.copy(centroId = id) }
        cargarActividades()
    }

    private fun cargarActividades() {
        val id = _estado.value.centroId ?: run {
            _estado.update { it.copy(actividades = UiState.Vacio, campanas = emptyList(), proyectos = emptyList()) }
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(actividades = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.actividadesDeCentro(id) }
            // Si se cambió de centro mientras cargaba, se descarta la respuesta vieja
            _estado.update { if (it.centroId == id) it.copy(actividades = r) else it }
        }
        cargarCampanasYProyectos(id)
    }

    private fun cargarCampanasYProyectos(centroId: String) {
        viewModelScope.launch {
            val campanas = runCatching { repo.campanasDeCentro(centroId) }.getOrDefault(emptyList())
            val proyectos = runCatching { repo.proyectosDeCentro(centroId) }.getOrDefault(emptyList())
            _estado.update { if (it.centroId == centroId) it.copy(campanas = campanas, proyectos = proyectos) else it }
        }
    }

    fun activarCampana(c: Campana, activa: Boolean) {
        viewModelScope.launch {
            try {
                val nueva = repo.activarCampana(c.id, activa)
                _estado.update { s -> s.copy(campanas = s.campanas.map { if (it.id == c.id) nueva else it }, mensaje = if (activa) "Campaña activada" else "Campaña desactivada") }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun activarProyecto(p: Proyecto, activo: Boolean) {
        viewModelScope.launch {
            try {
                val nuevo = repo.activarProyecto(p.id, activo)
                _estado.update { s -> s.copy(proyectos = s.proyectos.map { if (it.id == p.id) nuevo else it }, mensaje = if (activo) "Proyecto activado" else "Proyecto desactivado") }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun cancelarActividad(id: String) {
        viewModelScope.launch {
            try {
                repo.cancelarActividad(id)
                _estado.update { it.copy(mensaje = "Actividad cancelada. Se avisó a las familias inscritas.") }
                cargarActividades()
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }
}

// ---- Crear / editar actividad ----

data class FormActividad(
    val titulo: String = "",
    val categoria: Categoria = Categoria.COMUNIDAD,
    val fecha: String = LocalDate.now().plusDays(7).toString(),
    val horaInicio: String = "10:00",
    val horaFin: String = "12:00",
    val lugar: String = "",
    val descripcion: String = "",
    val requisitos: String = "",
    val edadMinima: String = "0",
    val aptaNinos: Boolean = true,
    val cuposTotales: String = "20",
    val imagenUrl: String? = null,
    val guardando: Boolean = false,
    val guardada: Boolean = false,
    val error: String? = null
)

class FormActividadViewModel(
    private val repo: GestionRepository,
    private val centroId: String,
    private val actividadId: String?
) : ViewModel() {

    private val _form = MutableStateFlow(FormActividad())
    val form: StateFlow<FormActividad> = _form.asStateFlow()
    val esEdicion: Boolean get() = actividadId != null

    private val _cargando = MutableStateFlow(actividadId != null)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        if (actividadId != null) {
            viewModelScope.launch {
                runCatching { repo.actividadesDeCentro(centroId).first { it.id == actividadId } }.onSuccess { a ->
                    _form.value = FormActividad(
                        titulo = a.titulo, categoria = a.categoria, fecha = a.fecha, horaInicio = a.horaInicio.take(5),
                        horaFin = a.horaFin?.take(5) ?: "", lugar = a.lugar, descripcion = a.descripcion,
                        requisitos = a.requisitos.joinToString(", "), edadMinima = a.edadMinima.toString(),
                        aptaNinos = a.aptaNinos, cuposTotales = a.cuposTotales.toString(), imagenUrl = a.imagenUrl
                    )
                }.onFailure { e -> _form.update { it.copy(error = e.message) } }
                _cargando.value = false
            }
        }
    }

    fun cambiar(cambio: (FormActividad) -> FormActividad) = _form.update { cambio(it).copy(error = null) }

    private fun validar(f: FormActividad): String? = when {
        f.titulo.trim().length < 3 -> "Escribe un título (mínimo 3 caracteres)"
        runCatching { LocalDate.parse(f.fecha) }.isFailure -> "La fecha debe tener el formato AAAA-MM-DD"
        runCatching { LocalTime.parse(f.horaInicio) }.isFailure -> "La hora de inicio debe ser HH:MM"
        f.horaFin.isNotBlank() && runCatching { LocalTime.parse(f.horaFin) }.isFailure -> "La hora de fin debe ser HH:MM"
        f.lugar.trim().length < 3 -> "Escribe el lugar"
        f.descripcion.trim().length < 10 -> "La descripción debe tener al menos 10 caracteres"
        (f.edadMinima.toIntOrNull() ?: -1) !in 0..120 -> "Edad mínima inválida"
        (f.cuposTotales.toIntOrNull() ?: 0) !in 1..500 -> "Los cupos deben estar entre 1 y 500"
        else -> null
    }

    fun guardar() {
        val f = _form.value
        if (f.guardando) return
        validar(f)?.let { err -> _form.update { it.copy(error = err) }; return }
        val requisitos = f.requisitos.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val horaFin = f.horaFin.trim().ifBlank { null }
        viewModelScope.launch {
            _form.update { it.copy(guardando = true) }
            try {
                if (actividadId == null) {
                    repo.crearActividad(
                        centroId,
                        ActividadIn(
                            titulo = f.titulo.trim(), categoria = f.categoria.clave, fecha = f.fecha, horaInicio = f.horaInicio,
                            horaFin = horaFin, lugar = f.lugar.trim(), descripcion = f.descripcion.trim(), requisitos = requisitos,
                            edadMinima = f.edadMinima.toInt(), aptaNinos = f.aptaNinos, cuposTotales = f.cuposTotales.toInt()
                        )
                    )
                } else {
                    repo.editarActividad(
                        actividadId,
                        ActividadPatch(
                            titulo = f.titulo.trim(), categoria = f.categoria.clave, fecha = f.fecha, horaInicio = f.horaInicio,
                            horaFin = horaFin, lugar = f.lugar.trim(), descripcion = f.descripcion.trim(), requisitos = requisitos,
                            edadMinima = f.edadMinima.toInt(), aptaNinos = f.aptaNinos, cuposTotales = f.cuposTotales.toInt()
                        )
                    )
                }
                _form.update { it.copy(guardada = true) }
            } catch (e: Exception) {
                _form.update { it.copy(error = e.message ?: "No se pudo guardar") }
            } finally {
                _form.update { it.copy(guardando = false) }
            }
        }
    }

    fun subirImagen(bytes: ByteArray) {
        val id = actividadId ?: return
        viewModelScope.launch {
            _form.update { it.copy(guardando = true) }
            try {
                val a = repo.subirImagenActividad(id, bytes)
                _form.update { it.copy(imagenUrl = a.imagenUrl) }
            } catch (e: Exception) {
                _form.update { it.copy(error = e.message ?: "No se pudo subir la imagen") }
            } finally {
                _form.update { it.copy(guardando = false) }
            }
        }
    }
}

// ---- Lista de asistentes (RF-06) y toma de asistencia (RF-11/14) ----

data class AsistentesUiState(
    val lista: UiState<List<AsistenteLista>> = UiState.Cargando,
    val asistieron: Set<String> = emptySet(),
    val noAsistieron: Set<String> = emptySet(),
    val guardando: Boolean = false,
    val mensaje: String? = null
)

class AsistentesViewModel(private val repo: GestionRepository, private val actividadId: String) : ViewModel() {
    private val _estado = MutableStateFlow(AsistentesUiState())
    val estado: StateFlow<AsistentesUiState> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(lista = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.asistentes(actividadId) }
            _estado.update { it.copy(lista = r, asistieron = emptySet(), noAsistieron = emptySet()) }
        }
    }

    fun marcar(inscripcionId: String, asistio: Boolean) {
        _estado.update {
            if (asistio) it.copy(asistieron = it.asistieron + inscripcionId, noAsistieron = it.noAsistieron - inscripcionId)
            else it.copy(noAsistieron = it.noAsistieron + inscripcionId, asistieron = it.asistieron - inscripcionId)
        }
    }

    fun guardar() {
        val s = _estado.value
        if (s.guardando) return
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            try {
                repo.marcarAsistencia(actividadId, s.asistieron.toList(), s.noAsistieron.toList())
                _estado.update { it.copy(mensaje = "Asistencia guardada") }
                cargar()
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message ?: "No se pudo guardar") }
            } finally {
                _estado.update { it.copy(guardando = false) }
            }
        }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }
}

// ---- Campaña y proyecto nuevos (formularios cortos) ----

data class FormCampana(
    val titulo: String = "", val descripcion: String = "", val queLlevar: String = "",
    val dondeEntregar: String = "", val horarioEntrega: String = "", val hasta: String = "",
    val guardando: Boolean = false, val guardada: Boolean = false, val error: String? = null
)

data class FormProyecto(
    val titulo: String = "", val causa: String = "", val objetivo: String = "", val descripcion: String = "",
    val avance: String = "0", val guardando: Boolean = false, val guardada: Boolean = false, val error: String? = null
)

class FormCampanaViewModel(
    private val repo: GestionRepository,
    private val centroId: String,
    private val campanaId: String? = null
) : ViewModel() {
    private val _form = MutableStateFlow(FormCampana())
    val form: StateFlow<FormCampana> = _form.asStateFlow()
    val esEdicion: Boolean get() = campanaId != null

    private val _cargando = MutableStateFlow(campanaId != null)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        if (campanaId != null) {
            viewModelScope.launch {
                runCatching { repo.campanasDeCentro(centroId).first { it.id == campanaId } }.onSuccess { c ->
                    _form.value = FormCampana(
                        titulo = c.titulo, descripcion = c.descripcion, queLlevar = c.queLlevar.joinToString(", "),
                        dondeEntregar = c.dondeEntregar ?: "", horarioEntrega = c.horarioEntrega ?: "", hasta = c.hasta ?: ""
                    )
                }.onFailure { e -> _form.update { it.copy(error = e.message ?: "No se pudo cargar la campaña") } }
                _cargando.value = false
            }
        }
    }

    fun cambiar(cambio: (FormCampana) -> FormCampana) = _form.update { cambio(it).copy(error = null) }

    fun guardar() {
        val f = _form.value
        if (f.guardando) return
        val err = when {
            f.titulo.trim().length < 3 -> "Escribe un título"
            f.descripcion.trim().length < 10 -> "La descripción debe tener al menos 10 caracteres"
            f.hasta.isNotBlank() && runCatching { LocalDate.parse(f.hasta) }.isFailure -> "La fecha límite debe ser AAAA-MM-DD"
            else -> null
        }
        if (err != null) { _form.update { it.copy(error = err) }; return }
        val queLlevar = f.queLlevar.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        viewModelScope.launch {
            _form.update { it.copy(guardando = true) }
            try {
                if (campanaId == null) {
                    repo.crearCampana(
                        centroId,
                        CampanaIn(
                            titulo = f.titulo.trim(), descripcion = f.descripcion.trim(), queLlevar = queLlevar,
                            dondeEntregar = f.dondeEntregar.trim().ifBlank { null }, horarioEntrega = f.horarioEntrega.trim().ifBlank { null },
                            hasta = f.hasta.trim().ifBlank { null }
                        )
                    )
                } else {
                    repo.editarCampana(
                        campanaId,
                        CampanaPatch(
                            titulo = f.titulo.trim(), descripcion = f.descripcion.trim(), queLlevar = queLlevar,
                            dondeEntregar = f.dondeEntregar.trim().ifBlank { null }, horarioEntrega = f.horarioEntrega.trim().ifBlank { null },
                            hasta = f.hasta.trim().ifBlank { null }
                        )
                    )
                }
                _form.update { it.copy(guardada = true) }
            } catch (e: Exception) {
                _form.update { it.copy(error = e.message ?: "No se pudo guardar") }
            } finally {
                _form.update { it.copy(guardando = false) }
            }
        }
    }
}

class FormProyectoViewModel(
    private val repo: GestionRepository,
    private val centroId: String,
    private val proyectoId: String? = null
) : ViewModel() {
    private val _form = MutableStateFlow(FormProyecto())
    val form: StateFlow<FormProyecto> = _form.asStateFlow()
    val esEdicion: Boolean get() = proyectoId != null

    private val _cargando = MutableStateFlow(proyectoId != null)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        if (proyectoId != null) {
            viewModelScope.launch {
                runCatching { repo.proyectosDeCentro(centroId).first { it.id == proyectoId } }.onSuccess { p ->
                    _form.value = FormProyecto(
                        titulo = p.titulo, causa = p.causa, objetivo = p.objetivo, descripcion = p.descripcion,
                        avance = (p.avance * 100).toInt().toString()
                    )
                }.onFailure { e -> _form.update { it.copy(error = e.message ?: "No se pudo cargar el proyecto") } }
                _cargando.value = false
            }
        }
    }

    fun cambiar(cambio: (FormProyecto) -> FormProyecto) = _form.update { cambio(it).copy(error = null) }

    fun guardar() {
        val f = _form.value
        if (f.guardando) return
        val avance = f.avance.toFloatOrNull()
        val err = when {
            f.titulo.trim().length < 3 -> "Escribe un título"
            f.causa.trim().length < 2 -> "Escribe la causa"
            f.objetivo.trim().length < 2 -> "Escribe el objetivo"
            f.descripcion.trim().length < 10 -> "La descripción debe tener al menos 10 caracteres"
            avance == null || avance !in 0f..100f -> "El avance es un porcentaje de 0 a 100"
            else -> null
        }
        if (err != null) { _form.update { it.copy(error = err) }; return }
        viewModelScope.launch {
            _form.update { it.copy(guardando = true) }
            try {
                val fraccion = (avance ?: 0f) / 100f
                if (proyectoId == null) {
                    repo.crearProyecto(centroId, ProyectoIn(f.titulo.trim(), f.causa.trim(), f.objetivo.trim(), f.descripcion.trim(), fraccion))
                } else {
                    repo.editarProyecto(proyectoId, ProyectoPatch(f.titulo.trim(), f.causa.trim(), f.objetivo.trim(), f.descripcion.trim(), fraccion))
                }
                _form.update { it.copy(guardada = true) }
            } catch (e: Exception) {
                _form.update { it.copy(error = e.message ?: "No se pudo guardar") }
            } finally {
                _form.update { it.copy(guardando = false) }
            }
        }
    }
}

// ---- Moderación (admin): testimonios y comentarios pendientes ----

data class ModeracionUiState(
    val testimonios: UiState<List<Testimonio>> = UiState.Cargando,
    val comentarios: UiState<List<Comentario>> = UiState.Cargando,
    val procesandoId: String? = null,
    val mensaje: String? = null
)

class ModeracionViewModel(private val repo: GestionRepository) : ViewModel() {
    private val _estado = MutableStateFlow(ModeracionUiState())
    val estado: StateFlow<ModeracionUiState> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(testimonios = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.testimoniosPendientes() }
            _estado.update { it.copy(testimonios = r) }
        }
        viewModelScope.launch {
            _estado.update { it.copy(comentarios = UiState.Cargando) }
            val r = cargarEstado(esVacio = { it.isEmpty() }) { repo.comentariosPendientes() }
            _estado.update { it.copy(comentarios = r) }
        }
    }

    fun moderarTestimonio(id: String, aprobar: Boolean, motivo: String?) = procesar(id) {
        repo.moderarTestimonio(id, aprobar, motivo)
        _estado.update { s ->
            val lista = ((s.testimonios as? UiState.Datos)?.datos ?: emptyList()).filter { it.id != id }
            s.copy(testimonios = if (lista.isEmpty()) UiState.Vacio else UiState.Datos(lista))
        }
    }

    fun moderarComentario(id: String, aprobar: Boolean, motivo: String?) = procesar(id) {
        repo.moderarComentario(id, aprobar, motivo)
        _estado.update { s ->
            val lista = ((s.comentarios as? UiState.Datos)?.datos ?: emptyList()).filter { it.id != id }
            s.copy(comentarios = if (lista.isEmpty()) UiState.Vacio else UiState.Datos(lista))
        }
    }

    private fun procesar(id: String, bloque: suspend () -> Unit) {
        if (_estado.value.procesandoId != null) return
        viewModelScope.launch {
            _estado.update { it.copy(procesandoId = id) }
            try {
                bloque()
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message ?: "No se pudo moderar") }
            } finally {
                _estado.update { it.copy(procesandoId = null) }
            }
        }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }
}
