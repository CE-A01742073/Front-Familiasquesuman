@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.familiasquesuman.demo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.Campana
import com.familiasquesuman.demo.model.Categoria
import com.familiasquesuman.demo.model.Proyecto
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.components.BarraSuperior
import com.familiasquesuman.demo.ui.components.Chip
import com.familiasquesuman.demo.ui.components.ContenedorEstado
import com.familiasquesuman.demo.ui.components.ImagenPlaceholder
import com.familiasquesuman.demo.ui.components.Seccion
import com.familiasquesuman.demo.ui.components.rememberSelectorFoto
import com.familiasquesuman.demo.ui.viewmodel.AsistentesViewModel
import com.familiasquesuman.demo.ui.viewmodel.FormActividadViewModel
import com.familiasquesuman.demo.ui.viewmodel.FormCampanaViewModel
import com.familiasquesuman.demo.ui.viewmodel.FormProyectoViewModel
import com.familiasquesuman.demo.ui.viewmodel.GestionViewModel
import com.familiasquesuman.demo.ui.viewmodel.ModeracionViewModel

data class GestionAcciones(
    val onNuevaActividad: (String) -> Unit,
    val onEditarActividad: (String, String) -> Unit,
    val onAsistentes: (String) -> Unit,
    val onNuevaCampana: (String) -> Unit,
    val onEditarCampana: (String, String) -> Unit,
    val onNuevoProyecto: (String) -> Unit,
    val onEditarProyecto: (String, String) -> Unit,
    val onModeracion: () -> Unit
)

// ---------- Panel de gestión ----------

@Composable
fun GestionScreen(vm: GestionViewModel, acciones: GestionAcciones) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var cancelandoId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(estado.mensaje) {
        val m = estado.mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        vm.mensajeMostrado()
    }
    cancelandoId?.let { id ->
        AlertDialog(
            onDismissRequest = { cancelandoId = null },
            title = { Text("¿Cancelar la actividad?") },
            text = { Text("Se avisará a las familias inscritas y se liberarán los cupos. No se puede deshacer.") },
            confirmButton = { TextButton(onClick = { vm.cancelarActividad(id); cancelandoId = null }) { Text("Cancelar actividad", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { cancelandoId = null }) { Text("Volver") } }
        )
    }

    val centroId = estado.centroId
    Scaffold(
        topBar = { BarraSuperior(if (vm.esAdmin) "Administración" else "Mi asociación") },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            if (centroId != null) {
                ExtendedFloatingActionButton(
                    onClick = { acciones.onNuevaActividad(centroId) },
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Nueva actividad") }
                )
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(bottom = 88.dp)) {
            estado.resumen?.let { r ->
                item {
                    Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Dato("${r.familias}", "familias", Modifier.weight(1f))
                        Dato("${r.actividadesActivas}", "activas", Modifier.weight(1f))
                        Dato("${r.inscripciones}", "inscritos", Modifier.weight(1f))
                        Dato("${r.testimoniosPendientes + r.comentariosPendientes}", "por moderar", Modifier.weight(1f))
                    }
                    TextButton(onClick = acciones.onModeracion, modifier = Modifier.padding(horizontal = 8.dp)) {
                        Icon(Icons.Default.Flag, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Moderar testimonios y comentarios")
                    }
                }
            }
            item {
                if (estado.centros.size > 1) {
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(estado.centros, key = { it.id }) { c ->
                            FilterChip(selected = c.id == centroId, onClick = { vm.elegirCentro(c.id) }, label = { Text(c.nombre, maxLines = 1) })
                        }
                    }
                } else if (estado.centros.size == 1) {
                    Text(estado.centros.first().nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                }
                if (centroId != null) {
                    Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { acciones.onNuevaCampana(centroId) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Campaign, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Nueva campaña", maxLines = 1)
                        }
                        OutlinedButton(onClick = { acciones.onNuevoProyecto(centroId) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Flag, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Nuevo proyecto", maxLines = 1)
                        }
                    }
                }
            }

            if (estado.campanas.isNotEmpty()) {
                item { Seccion("Campañas") }
                items(estado.campanas, key = { "c-" + it.id }) { c ->
                    TarjetaGestionPublicacion(
                        modifier = Modifier.animateItem(),
                        titulo = c.titulo,
                        detalle = c.hastaTexto?.let { "Hasta $it" } ?: "Sin fecha límite",
                        activa = c.activa,
                        onEditar = { acciones.onEditarCampana(c.centroId, c.id) },
                        onActivar = { vm.activarCampana(c, it) }
                    )
                }
            }
            if (estado.proyectos.isNotEmpty()) {
                item { Seccion("Proyectos") }
                items(estado.proyectos, key = { "p-" + it.id }) { p ->
                    TarjetaGestionPublicacion(
                        modifier = Modifier.animateItem(),
                        titulo = p.titulo,
                        detalle = "${p.causa} · ${(p.avance * 100).toInt()}% de avance",
                        activa = p.activo,
                        onEditar = { acciones.onEditarProyecto(p.centroId, p.id) },
                        onActivar = { vm.activarProyecto(p, it) }
                    )
                }
            }

            item { Seccion("Actividades") }
            when (val act = estado.actividades) {
                is UiState.Cargando -> item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                is UiState.Error -> item {
                    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(act.mensaje, color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = { vm.cargar() }) { Text("Reintentar") }
                    }
                }
                is UiState.Vacio -> item {
                    Text(
                        if (centroId == null) "Tu cuenta aún no tiene un centro asignado. Pide al administrador que lo vincule." else "Este centro no tiene actividades. Crea la primera.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                is UiState.Datos -> items(act.datos, key = { "a-" + it.id }) { a ->
                    Box(Modifier.padding(horizontal = 16.dp, vertical = 5.dp).animateItem()) {
                        TarjetaGestionActividad(
                            a = a,
                            onEditar = { acciones.onEditarActividad(a.centroId, a.id) },
                            onAsistentes = { acciones.onAsistentes(a.id) },
                            onCancelar = { cancelandoId = a.id }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaGestionPublicacion(modifier: Modifier = Modifier, titulo: String, detalle: String, activa: Boolean, onEditar: () -> Unit, onActivar: (Boolean) -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                if (!activa) Chip("Inactiva", fondo = MaterialTheme.colorScheme.surfaceVariant, textoColor = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, "Editar") }
            Switch(checked = activa, onCheckedChange = onActivar)
        }
    }
}

@Composable
private fun Dato(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1)
        }
    }
}

@Composable
private fun TarjetaGestionActividad(a: Actividad, onEditar: () -> Unit, onAsistentes: () -> Unit, onCancelar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(a.titulo, fontWeight = FontWeight.SemiBold)
                    Text("${a.fechaTexto} · ${a.hora}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                when (a.estado) {
                    "cancelada" -> Chip("Cancelada", fondo = MaterialTheme.colorScheme.errorContainer, textoColor = MaterialTheme.colorScheme.onErrorContainer)
                    "finalizada" -> Chip("Finalizada", fondo = MaterialTheme.colorScheme.surfaceVariant, textoColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    else -> Chip("${a.cuposOcupados}/${a.cuposTotales} lugares")
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(onClick = onAsistentes, contentPadding = PaddingValues(horizontal = 10.dp)) {
                    Icon(Icons.Default.Group, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Asistentes")
                }
                if (a.activa) {
                    OutlinedButton(onClick = onEditar, contentPadding = PaddingValues(horizontal = 10.dp)) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar")
                    }
                    TextButton(onClick = onCancelar) { Text("Cancelar", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

// ---------- Formulario de actividad ----------

@Composable
fun FormActividadScreen(vm: FormActividadViewModel, onBack: () -> Unit, onGuardada: () -> Unit) {
    val f by vm.form.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    LaunchedEffect(f.guardada) { if (f.guardada) onGuardada() }
    val selector = rememberSelectorFoto(onFoto = vm::subirImagen)

    Scaffold(
        topBar = { BarraSuperior(if (vm.esEdicion) "Editar actividad" else "Nueva actividad", onBack) },
        bottomBar = {
            Column(Modifier.padding(16.dp)) {
                f.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp)) }
                Button(onClick = vm::guardar, enabled = !f.guardando && !cargando, modifier = Modifier.fillMaxWidth()) {
                    if (f.guardando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text(if (vm.esEdicion) "Guardar cambios" else "Publicar actividad")
                }
            }
        }
    ) { padding ->
        if (cargando) {
            Box(Modifier.padding(padding).fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (vm.esEdicion) {
                ImagenPlaceholder(f.categoria, alto = 160, imagenUrl = f.imagenUrl)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = selector.elegirDeGaleria, modifier = Modifier.weight(1f)) { Text("Imagen de galería") }
                    OutlinedButton(onClick = selector.tomarFoto, modifier = Modifier.weight(1f)) { Text("Tomar foto") }
                }
            }
            Campo("Título", f.titulo) { v -> vm.cambiar { it.copy(titulo = v) } }
            Text("Categoría", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Categoria.entries.filter { it != Categoria.TODAS }) { c ->
                    FilterChip(selected = f.categoria == c, onClick = { vm.cambiar { it.copy(categoria = c) } }, label = { Text(c.etiqueta) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo("Fecha (AAAA-MM-DD)", f.fecha, Modifier.weight(1f)) { v -> vm.cambiar { it.copy(fecha = v) } }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo("Inicio (HH:MM)", f.horaInicio, Modifier.weight(1f)) { v -> vm.cambiar { it.copy(horaInicio = v) } }
                Campo("Fin (HH:MM)", f.horaFin, Modifier.weight(1f)) { v -> vm.cambiar { it.copy(horaFin = v) } }
            }
            Campo("Lugar", f.lugar) { v -> vm.cambiar { it.copy(lugar = v) } }
            Campo("Descripción", f.descripcion, minLines = 3) { v -> vm.cambiar { it.copy(descripcion = v) } }
            Campo("Requisitos (separados por coma)", f.requisitos) { v -> vm.cambiar { it.copy(requisitos = v) } }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo("Edad mínima", f.edadMinima, Modifier.weight(1f), numerico = true) { v -> vm.cambiar { it.copy(edadMinima = v.filter { c -> c.isDigit() }) } }
                Campo("Cupos (personas)", f.cuposTotales, Modifier.weight(1f), numerico = true) { v -> vm.cambiar { it.copy(cuposTotales = v.filter { c -> c.isDigit() }) } }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Apta para niños", modifier = Modifier.weight(1f))
                Switch(checked = f.aptaNinos, onCheckedChange = { v -> vm.cambiar { it.copy(aptaNinos = v) } })
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Campo(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    minLines: Int = 1,
    numerico: Boolean = false,
    onCambio: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        minLines = minLines,
        singleLine = minLines == 1,
        keyboardOptions = if (numerico) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = modifier
    )
}

// ---------- Asistentes (RF-06) y asistencia ----------

@Composable
fun AsistentesScreen(vm: AsistentesViewModel, onBack: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(estado.mensaje) {
        val m = estado.mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        vm.mensajeMostrado()
    }
    Scaffold(
        topBar = { BarraSuperior("Asistentes", onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (estado.lista is UiState.Datos && (estado.asistieron.isNotEmpty() || estado.noAsistieron.isNotEmpty())) {
                Box(Modifier.padding(16.dp)) {
                    Button(onClick = vm::guardar, enabled = !estado.guardando, modifier = Modifier.fillMaxWidth()) {
                        if (estado.guardando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        else Text("Guardar asistencia (${estado.asistieron.size + estado.noAsistieron.size})")
                    }
                }
            }
        }
    ) { padding ->
        ContenedorEstado(estado = estado.lista, onReintentar = { vm.cargar() }, modifier = Modifier.padding(padding), mensajeVacio = "Nadie se ha inscrito todavía.") { lista ->
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(
                        "Total: ${lista.sumOf { it.personas }} personas de ${lista.size} familias. Marca quién asistió al terminar la actividad.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                items(lista, key = { it.inscripcionId }) { a ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(a.familiaNombre, fontWeight = FontWeight.SemiBold)
                                    Text(a.contactoEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Chip(
                                    when (a.estado) {
                                        "asistio" -> "Asistió"
                                        "no_asistio" -> "No asistió"
                                        else -> "${a.personas} personas"
                                    }
                                )
                            }
                            Text("Van: ${a.asistentes.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                            if (!a.comentario.isNullOrBlank()) Text("Nota: ${a.comentario}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (a.estado == "confirmada") {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = a.inscripcionId in estado.asistieron, onCheckedChange = { vm.marcar(a.inscripcionId, true) })
                                    Text("Asistió")
                                    Spacer(Modifier.width(12.dp))
                                    Checkbox(checked = a.inscripcionId in estado.noAsistieron, onCheckedChange = { vm.marcar(a.inscripcionId, false) })
                                    Text("No asistió")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Campaña y proyecto nuevos ----------

@Composable
fun FormCampanaScreen(vm: FormCampanaViewModel, onBack: () -> Unit, onGuardada: () -> Unit) {
    val f by vm.form.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    LaunchedEffect(f.guardada) { if (f.guardada) onGuardada() }
    Scaffold(
        topBar = { BarraSuperior(if (vm.esEdicion) "Editar campaña" else "Nueva campaña", onBack) },
        bottomBar = {
            Column(Modifier.padding(16.dp)) {
                f.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp)) }
                Button(onClick = vm::guardar, enabled = !f.guardando && !cargando, modifier = Modifier.fillMaxWidth()) {
                    if (f.guardando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text(if (vm.esEdicion) "Guardar cambios" else "Publicar campaña")
                }
            }
        }
    ) { padding ->
        if (cargando) {
            Box(Modifier.padding(padding).fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(Modifier.padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Campo("Título", f.titulo) { v -> vm.cambiar { it.copy(titulo = v) } }
            Campo("Descripción", f.descripcion, minLines = 3) { v -> vm.cambiar { it.copy(descripcion = v) } }
            Campo("Qué llevar (separado por coma)", f.queLlevar) { v -> vm.cambiar { it.copy(queLlevar = v) } }
            Campo("Dónde entregar", f.dondeEntregar) { v -> vm.cambiar { it.copy(dondeEntregar = v) } }
            Campo("Horario de entrega", f.horarioEntrega) { v -> vm.cambiar { it.copy(horarioEntrega = v) } }
            Campo("Hasta (AAAA-MM-DD, opcional)", f.hasta) { v -> vm.cambiar { it.copy(hasta = v) } }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun FormProyectoScreen(vm: FormProyectoViewModel, onBack: () -> Unit, onGuardada: () -> Unit) {
    val f by vm.form.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    LaunchedEffect(f.guardada) { if (f.guardada) onGuardada() }
    Scaffold(
        topBar = { BarraSuperior(if (vm.esEdicion) "Editar proyecto" else "Nuevo proyecto", onBack) },
        bottomBar = {
            Column(Modifier.padding(16.dp)) {
                f.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp)) }
                Button(onClick = vm::guardar, enabled = !f.guardando && !cargando, modifier = Modifier.fillMaxWidth()) {
                    if (f.guardando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text(if (vm.esEdicion) "Guardar cambios" else "Publicar proyecto")
                }
            }
        }
    ) { padding ->
        if (cargando) {
            Box(Modifier.padding(padding).fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(Modifier.padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Campo("Título", f.titulo) { v -> vm.cambiar { it.copy(titulo = v) } }
            Campo("Causa (ej. Educación)", f.causa) { v -> vm.cambiar { it.copy(causa = v) } }
            Campo("Objetivo / meta", f.objetivo) { v -> vm.cambiar { it.copy(objetivo = v) } }
            Campo("Descripción", f.descripcion, minLines = 3) { v -> vm.cambiar { it.copy(descripcion = v) } }
            Campo("Avance (%)", f.avance, numerico = true) { v -> vm.cambiar { it.copy(avance = v.filter { c -> c.isDigit() || c == '.' }) } }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ---------- Moderación (admin) ----------

@Composable
fun ModeracionScreen(vm: ModeracionViewModel, onBack: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var rechazando by rememberSaveable { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(estado.mensaje) {
        val m = estado.mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        vm.mensajeMostrado()
    }
    rechazando?.let { (tipo, id) ->
        var motivo by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { rechazando = null },
            title = { Text("Motivo del rechazo") },
            text = { OutlinedTextField(value = motivo, onValueChange = { motivo = it }, label = { Text("Se le enviará a la familia") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = {
                    if (tipo == "testimonio") vm.moderarTestimonio(id, false, motivo) else vm.moderarComentario(id, false, motivo)
                    rechazando = null
                }) { Text("Rechazar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { rechazando = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(topBar = { BarraSuperior("Moderación", onBack) }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Testimonios") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Comentarios") })
            }
            if (tab == 0) {
                ContenedorEstado(estado = estado.testimonios, onReintentar = { vm.cargar() }, mensajeVacio = "No hay testimonios pendientes.") { lista ->
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(lista, key = { it.id }) { t ->
                            Column(Modifier.animateItem()) {
                                TarjetaTestimonio(t, propio = false)
                                BotonesModeracion(
                                    procesando = estado.procesandoId == t.id,
                                    onAprobar = { vm.moderarTestimonio(t.id, true, null) },
                                    onRechazar = { rechazando = "testimonio" to t.id }
                                )
                            }
                        }
                    }
                }
            } else {
                ContenedorEstado(estado = estado.comentarios, onReintentar = { vm.cargar() }, mensajeVacio = "No hay comentarios pendientes.") { lista ->
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(lista, key = { it.id }) { c ->
                            Card(modifier = Modifier.fillMaxWidth().animateItem(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(c.autorNombre, fontWeight = FontWeight.SemiBold)
                                    Text(c.texto)
                                    Text(c.hace, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    BotonesModeracion(
                                        procesando = estado.procesandoId == c.id,
                                        onAprobar = { vm.moderarComentario(c.id, true, null) },
                                        onRechazar = { rechazando = "comentario" to c.id }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BotonesModeracion(procesando: Boolean, onAprobar: () -> Unit, onRechazar: () -> Unit) {
    Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onAprobar, enabled = !procesando, modifier = Modifier.weight(1f), colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
            if (procesando) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onTertiary)
            else {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Aprobar")
            }
        }
        OutlinedButton(onClick = onRechazar, enabled = !procesando, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Rechazar")
        }
    }
}
