@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.familiasquesuman.demo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familiasquesuman.demo.data.EstadoLike
import com.familiasquesuman.demo.model.Comentario
import com.familiasquesuman.demo.model.Conversacion
import com.familiasquesuman.demo.model.Mensaje
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.components.BarraSuperior
import com.familiasquesuman.demo.ui.components.Chip
import com.familiasquesuman.demo.ui.components.ContenedorEstado
import com.familiasquesuman.demo.ui.components.Seccion
import com.familiasquesuman.demo.ui.viewmodel.ChatViewModel
import com.familiasquesuman.demo.ui.viewmodel.ConversacionesViewModel
import com.familiasquesuman.demo.ui.viewmodel.SocialViewModel

// ---------- Likes ----------

@Composable
fun BotonLike(like: EstadoLike, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(onClick = onClick, modifier = modifier, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
        Icon(
            if (like.meGusta) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (like.meGusta) "Quitar me gusta" else "Me gusta",
            tint = if (like.meGusta) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(if (like.likes == 1) "1 me gusta" else "${like.likes} me gusta")
    }
}

// ---------- Likes + comentarios dentro de una pantalla de detalle ----------

@Composable
fun SeccionSocial(vm: SocialViewModel, tipoObjetivo: String, snackbar: SnackbarHostState) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    var reportandoId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(estado.mensaje) {
        val m = estado.mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        vm.mensajeMostrado()
    }

    reportandoId?.let { id ->
        DialogoReporte(
            onEnviar = { motivo -> vm.reportar("comentario", id, motivo); reportandoId = null },
            onCerrar = { reportandoId = null }
        )
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BotonLike(estado.like, vm::alternarLike)
        }
        Seccion("Comentarios")
        Text(
            "Los comentarios de las familias se revisan antes de publicarse.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = estado.texto,
            onValueChange = vm::cambiarTexto,
            placeholder = { Text("Escribe un comentario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4,
            trailingIcon = {
                IconButton(onClick = vm::comentar, enabled = estado.texto.isNotBlank() && !estado.enviando) {
                    if (estado.enviando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Icon(Icons.Default.Send, contentDescription = "Enviar comentario")
                }
            }
        )
        Spacer(Modifier.height(8.dp))
        when (val c = estado.comentarios) {
            is UiState.Cargando -> CircularProgressIndicator(Modifier.padding(8.dp).size(20.dp), strokeWidth = 2.dp)
            is UiState.Vacio -> Text("Sé la primera familia en comentar.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            is UiState.Error -> TextButton(onClick = vm::cargarComentarios) { Text("No se cargaron los comentarios. Reintentar") }
            is UiState.Datos -> c.datos.forEach { com ->
                TarjetaComentario(com, onBorrar = { vm.borrarComentario(com.id) }, onReportar = { reportandoId = com.id })
            }
        }
    }
}

@Composable
private fun TarjetaComentario(c: Comentario, onBorrar: () -> Unit, onReportar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(c.autorNombre, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text(c.hace, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (c.esMio) {
                    IconButton(onClick = onBorrar, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar comentario", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    IconButton(onClick = onReportar, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Flag, contentDescription = "Reportar comentario", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Text(c.texto, style = MaterialTheme.typography.bodyMedium)
            if (c.estado != "aprobado") {
                Spacer(Modifier.height(4.dp))
                Chip(if (c.estado == "pendiente") "En revisión" else "No publicado", fondo = MaterialTheme.colorScheme.surfaceVariant, textoColor = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun DialogoReporte(onEnviar: (String) -> Unit, onCerrar: () -> Unit) {
    var motivo by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(Icons.Default.Flag, null) },
        title = { Text("Reportar contenido") },
        text = {
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("¿Qué está mal?") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { TextButton(onClick = { onEnviar(motivo) }, enabled = motivo.trim().length >= 3) { Text("Enviar reporte") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

// ---------- Mensajes ----------

@Composable
fun ConversacionesScreen(vm: ConversacionesViewModel, esFamilia: Boolean, onAbrir: (Conversacion) -> Unit, onBuscarCentro: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    Scaffold(topBar = { BarraSuperior("Mensajes") }) { padding ->
        Column(Modifier.padding(padding)) {
            Text(
                if (esFamilia) "Habla directamente con las asociaciones. No hay mensajes entre familias."
                else "Familias que te han escrito.",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            if (esFamilia) {
                TextButton(onClick = onBuscarCentro, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text("Escribir a una asociación desde el directorio")
                }
            }
            ContenedorEstado(estado = estado, onReintentar = { vm.cargar() }, mensajeVacio = "Aún no tienes conversaciones.") { lista ->
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(lista, key = { it.id }) { c ->
                        ListItem(
                            headlineContent = { Text(if (esFamilia) c.centroNombre else c.familiaNombre, fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text(c.ultimoMensaje.ifBlank { "Sin mensajes" }, maxLines = 1) },
                            trailingContent = {
                                if (c.noLeidos > 0) Badge { Text("${c.noLeidos}") }
                                else if (c.bloqueada) Icon(Icons.Default.Block, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            leadingContent = {
                                Box(
                                    Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text((if (esFamilia) c.centroNombre else c.familiaNombre).firstOrNull()?.uppercase() ?: "?", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier
                                .clickable { onAbrir(c) }
                                .padding(horizontal = 4.dp),
                            tonalElevation = 0.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(vm: ChatViewModel, titulo: String, esFamilia: Boolean, onBack: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val listaEstado = rememberLazyListState()
    var confirmarBloqueo by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(estado.mensaje) {
        val m = estado.mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        vm.mensajeMostrado()
    }
    val total = (estado.mensajes as? UiState.Datos)?.datos?.size ?: 0
    LaunchedEffect(total) { if (total > 0) listaEstado.animateScrollToItem(total - 1) }

    if (confirmarBloqueo) {
        AlertDialog(
            onDismissRequest = { confirmarBloqueo = false },
            icon = { Icon(Icons.Default.Block, null) },
            title = { Text("¿Bloquear esta conversación?") },
            text = { Text("Ninguna de las dos partes podrá enviar más mensajes.") },
            confirmButton = { TextButton(onClick = { confirmarBloqueo = false; vm.bloquear() }) { Text("Bloquear", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { confirmarBloqueo = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { BarraSuperior(titulo, onBack) }
                if (!estado.bloqueada) {
                    IconButton(onClick = { confirmarBloqueo = true }) { Icon(Icons.Default.Block, contentDescription = "Bloquear conversación") }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (estado.bloqueada) {
                Text(
                    "Conversación bloqueada",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = estado.texto,
                        onValueChange = vm::cambiarTexto,
                        placeholder = { Text("Escribe un mensaje") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = vm::enviar, enabled = estado.texto.isNotBlank() && !estado.enviando) {
                        if (estado.enviando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    ) { padding ->
        when (val m = estado.mensajes) {
            is UiState.Cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is UiState.Error -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                TextButton(onClick = { vm.cargar() }) { Text("${m.mensaje}. Reintentar") }
            }
            is UiState.Vacio -> Box(Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    if (esFamilia) "Escribe tu primer mensaje a $titulo. Recuerda no compartir datos de tus hijos que no sean necesarios."
                    else "Aún no hay mensajes.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            is UiState.Datos -> LazyColumn(
                state = listaEstado,
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(m.datos, key = { it.id }) { msg -> Burbuja(msg, onReportar = { vm.reportar(msg.id) }) }
            }
        }
    }
}

@Composable
private fun Burbuja(m: Mensaje, onReportar: () -> Unit) {
    val mio = m.esMio
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mio) Arrangement.End else Arrangement.Start) {
        Column(
            Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(16.dp, 16.dp, if (mio) 4.dp else 16.dp, if (mio) 16.dp else 4.dp))
                .background(if (mio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(m.texto, color = if (mio) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    m.hora,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (mio) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!mio) {
                    Spacer(Modifier.width(6.dp))
                    IconButton(onClick = onReportar, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Flag, contentDescription = "Reportar mensaje", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
