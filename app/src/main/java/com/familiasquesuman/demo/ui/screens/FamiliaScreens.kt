@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.familiasquesuman.demo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.familiasquesuman.demo.model.Inscripcion
import com.familiasquesuman.demo.model.Pregunta
import com.familiasquesuman.demo.model.Testimonio
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.components.BarraSuperior
import com.familiasquesuman.demo.ui.components.Chip
import com.familiasquesuman.demo.ui.components.ContenedorEstado
import com.familiasquesuman.demo.ui.components.Seccion
import com.familiasquesuman.demo.ui.components.rememberSelectorFoto
import com.familiasquesuman.demo.ui.viewmodel.EncuestaViewModel
import com.familiasquesuman.demo.ui.viewmodel.EncuestasRespondidasViewModel
import com.familiasquesuman.demo.ui.viewmodel.GaleriaViewModel
import com.familiasquesuman.demo.ui.viewmodel.InsigniasViewModel

// ---------- Encuesta antes / después (RF-13) ----------

@Composable
fun EncuestaScreen(vm: EncuestaViewModel, onBack: () -> Unit, onEnviada: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.enviada) { if (estado.enviada) onEnviada() }
    val titulo = if (vm.momento == "antes") "Antes de la actividad" else "Después de la actividad"

    Scaffold(
        topBar = { BarraSuperior(titulo, onBack) },
        bottomBar = {
            if (estado.preguntas is UiState.Datos) {
                Column(Modifier.padding(16.dp)) {
                    estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp)) }
                    Button(onClick = vm::enviar, enabled = !estado.enviando, modifier = Modifier.fillMaxWidth()) {
                        if (estado.enviando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        else Text("Enviar respuestas")
                    }
                }
            }
        }
    ) { padding ->
        ContenedorEstado(estado = estado.preguntas, onReintentar = { vm.cargar() }, modifier = Modifier.padding(padding)) { preguntas ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Tus respuestas nos ayudan a mejorar las actividades. Son breves y quedan ligadas a esta inscripción.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                preguntas.forEach { p -> PreguntaItem(p, estado.respuestas[p.clave] ?: "") { vm.responder(p.clave, it) } }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PreguntaItem(p: Pregunta, valor: String, onCambio: (String) -> Unit) {
    Seccion(p.pregunta)
    when (p.tipo) {
        "escala" -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { n ->
                FilterChip(selected = valor == n.toString(), onClick = { onCambio(n.toString()) }, label = { Text("$n") })
            }
        }
        "si_no" -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = valor == "si", onClick = { onCambio("si") }, label = { Text("Sí") })
            FilterChip(selected = valor == "no", onClick = { onCambio("no") }, label = { Text("No") })
        }
        else -> OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// ---------- Encuestas respondidas (RF-13) ----------

@Composable
fun EncuestasRespondidasScreen(vm: EncuestasRespondidasViewModel, onBack: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    Scaffold(topBar = { BarraSuperior("Mis respuestas", onBack) }) { padding ->
        ContenedorEstado(
            estado = estado,
            onReintentar = { vm.cargar() },
            modifier = Modifier.padding(padding),
            mensajeVacio = "Aún no has respondido ninguna encuesta de esta actividad."
        ) { encuestas ->
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(encuestas, key = { it.momento }) { e ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(e.titulo, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                Chip(e.fechaTexto)
                            }
                            e.respuestas.forEach { r ->
                                Spacer(Modifier.height(10.dp))
                                Text(r.pregunta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(r.respuesta, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Insignias y niveles (RF-14) ----------

@Composable
fun InsigniasScreen(vm: InsigniasViewModel, onBack: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    Scaffold(topBar = { BarraSuperior("Mi camino solidario", onBack) }) { padding ->
        ContenedorEstado(estado = estado, onReintentar = { vm.cargar() }, modifier = Modifier.padding(padding)) { c ->
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.EmojiEvents, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Nivel ${c.nivel}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(
                                "${c.actividadesCompletadas} ${if (c.actividadesCompletadas == 1) "actividad completada" else "actividades completadas"}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (c.siguienteNivel != null) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Faltan ${c.faltanParaSiguiente} para llegar a ${c.siguienteNivel}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                item { Seccion("Insignias") }
                items(c.insignias, key = { it.clave }) { i ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (i.obtenida) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (i.obtenida) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    null,
                                    tint = if (i.obtenida) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(i.nombre, fontWeight = FontWeight.SemiBold)
                                Text(i.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                val avance = (c.actividadesCompletadas.toFloat() / i.minimo.coerceAtLeast(1)).coerceIn(0f, 1f)
                                Spacer(Modifier.height(6.dp))
                                LinearProgressIndicator(progress = { avance }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Galería: testimonios con foto (RF-12) ----------

@Composable
fun GaleriaScreen(vm: GaleriaViewModel, onBack: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var creando by rememberSaveable { mutableStateOf(false) }
    var borrandoId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(estado.mensaje) {
        val m = estado.mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        vm.mensajeMostrado()
    }

    if (creando) {
        DialogoTestimonio(
            actividades = estado.actividadesParaTestimonio,
            enviando = estado.enviando,
            onEnviar = { actividadId, texto, foto -> vm.enviar(actividadId, texto, foto); creando = false },
            onCerrar = { creando = false },
            onError = { vm.mensajeMostrado() }
        )
    }
    borrandoId?.let { id ->
        AlertDialog(
            onDismissRequest = { borrandoId = null },
            title = { Text("¿Eliminar este testimonio?") },
            text = { Text("Se borrará también la foto del servidor.") },
            confirmButton = { TextButton(onClick = { vm.borrar(id); borrandoId = null }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { borrandoId = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = { BarraSuperior("Nuestras familias", onBack) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Publicados") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Mis testimonios") })
            }
            if (tab == 0) {
                ContenedorEstado(estado = estado.publicos, onReintentar = { vm.cargar() }, mensajeVacio = "Todavía no hay testimonios publicados.") { lista ->
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(lista, key = { it.id }) { t -> TarjetaTestimonio(t, propio = false) }
                    }
                }
            } else {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Comparte cómo vivieron la actividad. El equipo revisa cada testimonio y foto antes de publicarlos; cuida la privacidad de tus hijos.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { creando = true },
                        enabled = estado.actividadesParaTestimonio.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddAPhoto, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (estado.actividadesParaTestimonio.isEmpty()) "Participa en una actividad para compartir" else "Compartir testimonio")
                    }
                }
                ContenedorEstado(estado = estado.mios, onReintentar = { vm.cargar() }, mensajeVacio = "Aún no has compartido testimonios.") { lista ->
                    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(lista, key = { it.id }) { t -> TarjetaTestimonio(t, propio = true, onBorrar = { borrandoId = t.id }) }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaTestimonio(t: Testimonio, propio: Boolean, onBorrar: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            if (t.fotoUrl != null) {
                AsyncImage(
                    model = t.fotoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(t.familiaNombre, fontWeight = FontWeight.SemiBold)
                        if (t.actividadTitulo.isNotBlank()) Text(t.actividadTitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (propio) {
                        when (t.estado) {
                            "pendiente" -> Chip("En revisión", fondo = MaterialTheme.colorScheme.secondaryContainer, textoColor = MaterialTheme.colorScheme.onSecondaryContainer)
                            "rechazado" -> Chip("No publicado", fondo = MaterialTheme.colorScheme.errorContainer, textoColor = MaterialTheme.colorScheme.onErrorContainer)
                            else -> Chip("Publicado", fondo = MaterialTheme.colorScheme.tertiaryContainer, textoColor = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                        IconButton(onClick = onBorrar) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } else {
                        Text(t.hace, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(t.texto, style = MaterialTheme.typography.bodyMedium)
                if (propio && t.estado == "rechazado" && t.motivoRechazo != null) {
                    Spacer(Modifier.height(4.dp))
                    Text("Motivo: ${t.motivoRechazo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun DialogoTestimonio(
    actividades: List<Inscripcion>,
    enviando: Boolean,
    onEnviar: (String, String, ByteArray?) -> Unit,
    onCerrar: () -> Unit,
    onError: (String) -> Unit
) {
    var actividadId by rememberSaveable { mutableStateOf(actividades.firstOrNull()?.actividadId ?: "") }
    var texto by rememberSaveable { mutableStateOf("") }
    var foto by remember { mutableStateOf<ByteArray?>(null) }
    var abierto by remember { mutableStateOf(false) }
    val selector = rememberSelectorFoto(onFoto = { foto = it }, onError = onError)
    val titulo = actividades.firstOrNull { it.actividadId == actividadId }?.actividadTitulo ?: "Elige la actividad"

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Compartir testimonio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Actividad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                        actividades.distinctBy { it.actividadId }.forEach { i ->
                            DropdownMenuItem(text = { Text(i.actividadTitulo) }, onClick = { actividadId = i.actividadId; abierto = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it.take(600) },
                    label = { Text("¿Cómo lo vivieron?") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = selector.tomarFoto, modifier = Modifier.weight(1f)) { Text("Cámara") }
                    OutlinedButton(onClick = selector.elegirDeGaleria, modifier = Modifier.weight(1f)) { Text("Galería") }
                }
                Text(
                    foto?.let { "Foto lista (${it.size / 1024} KB). Se quitan los datos de ubicación al subirla." } ?: "Foto opcional.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = { onEnviar(actividadId, texto, foto) }, enabled = !enviando && actividadId.isNotBlank() && texto.trim().length >= 10) {
                Text("Enviar a revisión")
            }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
