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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Soap
import androidx.compose.material.icons.filled.Toys
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.key
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.familiasquesuman.demo.data.AvisoPrivacidad
import com.familiasquesuman.demo.model.ModoTema
import com.familiasquesuman.demo.ui.components.rememberSelectorFoto
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.EmojiEvents
import com.familiasquesuman.demo.model.Campana
import com.familiasquesuman.demo.model.Categoria
import com.familiasquesuman.demo.model.CategoriaDonacion
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.model.Integrante
import com.familiasquesuman.demo.model.Notificacion
import com.familiasquesuman.demo.model.Proyecto
import com.familiasquesuman.demo.model.TipoObjetivo
import com.familiasquesuman.demo.data.EstadoLike
import com.familiasquesuman.demo.ui.viewmodel.SocialViewModel
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.comoLlegar
import com.familiasquesuman.demo.ui.correo
import com.familiasquesuman.demo.ui.llamar
import com.familiasquesuman.demo.ui.whatsapp
import com.familiasquesuman.demo.ui.components.BarraSuperior
import com.familiasquesuman.demo.ui.components.Chip
import com.familiasquesuman.demo.ui.components.ContenedorEstado
import com.familiasquesuman.demo.ui.components.ImagenPlaceholder
import com.familiasquesuman.demo.ui.components.LogoCentro
import com.familiasquesuman.demo.ui.components.Seccion
import com.familiasquesuman.demo.ui.components.SelloVerificado
import com.familiasquesuman.demo.ui.components.TarjetaActividad
import com.familiasquesuman.demo.ui.components.TarjetaCentro
import com.familiasquesuman.demo.ui.components.color
import com.familiasquesuman.demo.ui.components.icono
import com.familiasquesuman.demo.ui.theme.Amarillo
import com.familiasquesuman.demo.ui.theme.Azul
import com.familiasquesuman.demo.ui.theme.AzulBoton
import com.familiasquesuman.demo.ui.theme.AzulOscuro
import com.familiasquesuman.demo.ui.theme.Verde
import com.familiasquesuman.demo.ui.theme.AzulClaro
import com.familiasquesuman.demo.ui.viewmodel.DetalleCentroUiState
import com.familiasquesuman.demo.ui.viewmodel.DetalleCentroViewModel
import com.familiasquesuman.demo.ui.viewmodel.DetalleProyectoViewModel
import com.familiasquesuman.demo.ui.viewmodel.DirectorioViewModel
import com.familiasquesuman.demo.ui.viewmodel.DonarViewModel
import com.familiasquesuman.demo.ui.viewmodel.AvisoPrivacidadViewModel
import com.familiasquesuman.demo.ui.viewmodel.ModoDonar
import com.familiasquesuman.demo.ui.viewmodel.NotificacionesViewModel
import com.familiasquesuman.demo.ui.viewmodel.PerfilUiState
import com.familiasquesuman.demo.ui.viewmodel.PerfilViewModel
import com.familiasquesuman.demo.ui.viewmodel.ProyectosViewModel

@Composable
fun DirectorioScreen(vm: DirectorioViewModel, onBack: () -> Unit, onCentro: (String) -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val categoria by vm.categoria.collectAsStateWithLifecycle()
    DirectorioContenido(
        estado = estado,
        categoria = categoria,
        onCategoria = vm::cambiarCategoria,
        onReintentar = { vm.cargar() },
        onBack = onBack,
        onCentro = onCentro
    )
}

@Composable
fun DirectorioContenido(
    estado: UiState<List<Centro>>,
    categoria: Categoria,
    onCategoria: (Categoria) -> Unit,
    onReintentar: () -> Unit,
    onBack: () -> Unit,
    onCentro: (String) -> Unit
) {
    Scaffold(topBar = { BarraSuperior("Directorio de visiteo", onBack) }) { padding ->
        Column(Modifier.padding(padding)) {
            Text(
                "Centros y espacios verificados para visitar y ayudar en familia.",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            FilaCategorias(seleccionada = categoria, onSeleccionar = onCategoria)
            ContenedorEstado(estado = estado, onReintentar = onReintentar, mensajeVacio = "No hay centros en esa categoría.") { lista ->
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(lista, key = { it.id }) { c -> TarjetaCentro(c) { onCentro(c.id) } }
                }
            }
        }
    }
}

@Composable
fun DetalleCentroScreen(
    vm: DetalleCentroViewModel,
    esFamilia: Boolean,
    onBack: () -> Unit,
    onActividad: (String) -> Unit,
    onDonar: () -> Unit,
    onMensaje: (Centro) -> Unit
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    DetalleCentroContenido(estado, esFamilia, { vm.cargar() }, onBack, onActividad, onDonar, onMensaje)
}

@Composable
fun DetalleCentroContenido(
    estado: UiState<DetalleCentroUiState>,
    esFamilia: Boolean,
    onReintentar: () -> Unit,
    onBack: () -> Unit,
    onActividad: (String) -> Unit,
    onDonar: () -> Unit,
    onMensaje: (Centro) -> Unit
) {
    val context = LocalContext.current
    Scaffold(topBar = { BarraSuperior("Centro", onBack) }) { padding ->
        ContenedorEstado(estado = estado, onReintentar = onReintentar, modifier = Modifier.padding(padding)) { datos ->
            val centro = datos.centro
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(bottom = 24.dp)) {
                item { ImagenPlaceholder(centro.categoria, alto = 180, imagenUrl = centro.imagenUrl) }
                item {
                    Column(Modifier.padding(16.dp)) {
                        Chip(centro.categoria.etiqueta)
                        Spacer(Modifier.height(8.dp))
                        Text(centro.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        if (centro.verificado) SelloVerificado()
                        Spacer(Modifier.height(12.dp))
                        FilaInfo(Icons.Default.Place, centro.direccion)
                        FilaInfo(Icons.Default.Schedule, "Visitas: ${centro.horarioVisitas}")

                        Seccion("Sobre el centro")
                        Text(centro.descripcion)

                        Seccion("¿Qué necesitan?")
                        centro.queNecesitan.forEach {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(it)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (centro.whatsapp != null) {
                                OutlinedButton(onClick = { context.whatsapp(centro.whatsapp) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 6.dp)) {
                                    Icon(Icons.Default.Chat, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("WhatsApp", maxLines = 1)
                                }
                            }
                            if (centro.telefono != null) {
                                OutlinedButton(onClick = { context.llamar(centro.telefono) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 6.dp)) {
                                    Icon(Icons.Default.Call, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Llamar", maxLines = 1)
                                }
                            }
                            OutlinedButton(onClick = { context.comoLlegar(centro.direccion) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 6.dp)) {
                                Icon(Icons.Default.NearMe, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Llegar", maxLines = 1)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        // RF-08: contacto por correo con el asunto prellenado
                        if (centro.correo != null) {
                            OutlinedButton(
                                onClick = { context.correo(centro.correo, "Contacto desde Familias que Suman", "Hola, les escribo desde la app Familias que Suman.\n\n") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Escribir a ${centro.correo}", maxLines = 1)
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        if (esFamilia) {
                            Button(onClick = { onMensaje(centro) }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Mail, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Enviar mensaje a la asociación")
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        OutlinedButton(onClick = onDonar, modifier = Modifier.fillMaxWidth()) { Text("Quiero donar a este centro") }

                        if (datos.actividades.isNotEmpty()) Seccion("Actividades de este centro")
                    }
                }
                items(datos.actividades, key = { it.id }) { actividad ->
                    Box(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        TarjetaActividad(actividad) { onActividad(actividad.id) }
                    }
                }
            }
        }
    }
}

@Composable
fun DonarScreen(vm: DonarViewModel, onCentro: (String) -> Unit) {
    val modo by vm.modo.collectAsStateWithLifecycle()
    val categoria by vm.categoria.collectAsStateWithLifecycle()
    val campanas by vm.campanas.collectAsStateWithLifecycle()
    val centros by vm.centros.collectAsStateWithLifecycle()
    val enviandoId by vm.enviandoId.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        val m = mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        vm.mensajeMostrado()
    }

    DonarContenido(
        modo = modo,
        categoria = categoria,
        campanas = campanas,
        centros = centros,
        enviandoId = enviandoId,
        snackbar = snackbar,
        onModo = vm::cambiarModo,
        onCategoria = vm::cambiarCategoria,
        onAyudar = vm::ayudar,
        onLike = vm::alternarLike,
        onReintentar = { vm.cargar() },
        onCentro = onCentro
    )
}

@Composable
fun DonarContenido(
    modo: ModoDonar,
    categoria: CategoriaDonacion,
    campanas: UiState<List<Campana>>,
    centros: UiState<List<Centro>>,
    enviandoId: String?,
    snackbar: SnackbarHostState,
    onModo: (ModoDonar) -> Unit,
    onCategoria: (CategoriaDonacion) -> Unit,
    onAyudar: (Campana) -> Unit,
    onLike: (Campana) -> Unit,
    onReintentar: () -> Unit,
    onCentro: (String) -> Unit
) {
    Scaffold(
        topBar = { BarraSuperior("Quiero donar") },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Text(
                "Tu generosidad hace la diferencia en la vida de muchos.",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BotonModo(
                    texto = "Donar a campaña",
                    icono = Icons.Default.Campaign,
                    activo = modo == ModoDonar.CAMPANA,
                    colorActivo = Amarillo,
                    textoActivo = Color(0xFF3A2E00),
                    modifier = Modifier.weight(1f)
                ) { onModo(ModoDonar.CAMPANA) }
                BotonModo(
                    texto = "Tengo algo para donar",
                    icono = Icons.Default.VolunteerActivism,
                    activo = modo == ModoDonar.ESPECIE,
                    colorActivo = AzulOscuro,
                    textoActivo = Color.White,
                    modifier = Modifier.weight(1f)
                ) { onModo(ModoDonar.ESPECIE) }
            }
            Spacer(Modifier.height(8.dp))

            when (modo) {
                ModoDonar.CAMPANA -> ListaCampanas(campanas, enviandoId, onAyudar, onLike, onReintentar, onCentro)
                ModoDonar.ESPECIE -> ListaCentrosDonacion(centros, categoria, onCategoria, onReintentar, onCentro)
            }
        }
    }
}

@Composable
private fun BotonModo(
    texto: String,
    icono: ImageVector,
    activo: Boolean,
    colorActivo: Color,
    textoActivo: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (activo) {
        Button(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = colorActivo, contentColor = textoActivo),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Icon(icono, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(texto, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Icon(icono, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(texto, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        }
    }
}

@Composable
private fun ListaCampanas(
    estado: UiState<List<Campana>>,
    enviandoId: String?,
    onAyudar: (Campana) -> Unit,
    onLike: (Campana) -> Unit,
    onReintentar: () -> Unit,
    onCentro: (String) -> Unit
) {
    ContenedorEstado(estado = estado, onReintentar = onReintentar, mensajeVacio = "No hay campañas activas en tu ciudad.") { lista ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lista, key = { it.id }) { c ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        ImagenPlaceholder(Categoria.COMUNIDAD, alto = 120, imagenUrl = c.imagenUrl)
                        Column(Modifier.padding(14.dp)) {
                            Chip("Campaña", fondo = MaterialTheme.colorScheme.secondaryContainer, textoColor = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.height(6.dp))
                            Text(c.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(c.centroNombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(6.dp))
                            Text(c.descripcion, style = MaterialTheme.typography.bodyMedium)
                            if (c.queLlevar.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                Text("Qué llevar: ${c.queLlevar.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (c.dondeEntregar != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Place, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(4.dp))
                                    Text(c.dondeEntregar + (c.horarioEntrega?.let { " · $it" } ?: ""), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            if (c.hastaTexto != null) {
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Hasta: ${c.hastaTexto}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            BotonLike(EstadoLike(c.likes, c.meGusta), onClick = { onLike(c) })
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onAyudar(c) },
                                    enabled = !c.yaApoye && enviandoId == null,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) {
                                    when {
                                        enviandoId == c.id -> CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                                        c.yaApoye -> {
                                            Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Apoyo registrado")
                                        }
                                        else -> Text("Quiero ayudar")
                                    }
                                }
                                OutlinedButton(onClick = { onCentro(c.centroId) }) { Text("Ver más") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListaCentrosDonacion(
    estado: UiState<List<Centro>>,
    categoria: CategoriaDonacion,
    onCategoria: (CategoriaDonacion) -> Unit,
    onReintentar: () -> Unit,
    onCentro: (String) -> Unit
) {
    val context = LocalContext.current
    Column {
        Text(
            "¿Qué quieres donar?",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CategoriaDonacion.entries) { cat ->
                FilterChip(
                    selected = categoria == cat,
                    onClick = { onCategoria(cat) },
                    label = { Text(cat.etiqueta) },
                    leadingIcon = { Icon(cat.icono(), null, modifier = Modifier.size(16.dp)) }
                )
            }
        }
        ContenedorEstado(estado = estado, onReintentar = onReintentar, mensajeVacio = "Ningún centro recibe eso por ahora.") { lista ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lista, key = { it.id }) { c ->
                    Card(
                        onClick = { onCentro(c.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                LogoCentro(c.imagenUrl, c.categoria, tamano = 52)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    if (c.verificado) SelloVerificado()
                                    Text(c.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(c.tipo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(c.descripcion, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(4.dp))
                                Text(c.direccion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(c.acepta) { cat ->
                                    Chip(cat.etiqueta, fondo = MaterialTheme.colorScheme.surfaceVariant, textoColor = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (c.whatsapp != null) {
                                    Button(
                                        onClick = { context.whatsapp(c.whatsapp) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("WhatsApp", maxLines = 1)
                                    }
                                }
                                if (c.telefono != null) {
                                    OutlinedButton(
                                        onClick = { context.llamar(c.telefono) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiary),
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Call, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Llamar", maxLines = 1)
                                    }
                                }
                                Button(
                                    onClick = { context.comoLlegar(c.direccion) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 6.dp)
                                ) {
                                    Icon(Icons.Default.NearMe, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Cómo llegar", maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun CategoriaDonacion.icono(): ImageVector = when (this) {
    CategoriaDonacion.TODOS -> Icons.Default.Inventory2
    CategoriaDonacion.JUGUETES -> Icons.Default.Toys
    CategoriaDonacion.ROPA -> Icons.Default.Checkroom
    CategoriaDonacion.ALIMENTOS -> Icons.Default.Restaurant
    CategoriaDonacion.HIGIENE -> Icons.Default.Soap
    CategoriaDonacion.SILLAS_RUEDAS -> Icons.Default.Accessible
    CategoriaDonacion.UTILES -> Icons.Default.School
    CategoriaDonacion.COBIJAS -> Icons.Default.Bed
    CategoriaDonacion.CROQUETAS -> Icons.Default.Pets
}

@Composable
fun AvisoPrivacidadScreen(vm: AvisoPrivacidadViewModel, onBack: () -> Unit, onCuentaCerrada: () -> Unit) {
    val context = LocalContext.current
    val correoContacto = "hola@familiasquesuman.com"
    val familia by vm.familia.collectAsStateWithLifecycle()
    val sesionIniciada by vm.sesionIniciada.collectAsStateWithLifecycle()
    val borrando by vm.borrando.collectAsStateWithLifecycle()
    val cuentaCerrada by vm.cuentaCerrada.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var confirmarCierre by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(cuentaCerrada) { if (cuentaCerrada) onCuentaCerrada() }
    LaunchedEffect(error) {
        val e = error ?: return@LaunchedEffect
        snackbar.showSnackbar(e)
        vm.errorMostrado()
    }

    if (confirmarCierre) {
        AlertDialog(
            onDismissRequest = { if (!borrando) confirmarCierre = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Eliminar la cuenta de ${familia.nombre}?") },
            text = {
                Text(
                    "Se eliminarán del servidor todos los datos de tu familia, incluidos los de tus hijos: " +
                        "integrantes, inscripciones, fotos y mensajes. Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(enabled = !borrando, onClick = { vm.cerrarCuenta() }) {
                    if (borrando) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Eliminar cuenta", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(enabled = !borrando, onClick = { confirmarCierre = false }) { Text("Cancelar") } }
        )
    }

    AvisoPrivacidadContenido(
        snackbar = snackbar,
        mostrarAcciones = sesionIniciada,
        onBack = onBack,
        onSolicitarDatos = {
            context.correo(
                destinatario = correoContacto,
                asunto = "Solicitud de derechos ARCO",
                cuerpo = "Hola, soy ${familia.nombre} (${familia.correo}). Quiero ejercer mi derecho de: " +
                    "[Acceso / Rectificación / Cancelación / Oposición]. Descripción: "
            )
        },
        onCerrarCuenta = { confirmarCierre = true }
    )
}

@Composable
fun AvisoPrivacidadContenido(
    snackbar: SnackbarHostState,
    mostrarAcciones: Boolean,
    onBack: () -> Unit,
    onSolicitarDatos: () -> Unit,
    onCerrarCuenta: () -> Unit
) {
    Scaffold(
        topBar = { BarraSuperior("Aviso de privacidad", onBack) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("Aviso de Privacidad Integral", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(AvisoPrivacidad.ACTUALIZACION, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(AvisoPrivacidad.secciones) { seccion ->
                Column {
                    if (seccion.titulo != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(seccion.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                    }
                    seccion.parrafos.forEach { parrafo ->
                        Text(parrafo, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }
            if (mostrarAcciones) item {
                Divider(Modifier.padding(vertical = 8.dp))
                Text("Tus datos, tu decisión", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Puedes pedir en cualquier momento ver, corregir o borrar lo que guardamos de tu familia.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onSolicitarDatos, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Mail, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Solicitar acceso, corrección o eliminación de datos")
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onCerrarCuenta,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteForever, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar mi cuenta y eliminar mis datos")
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ProyectosScreen(vm: ProyectosViewModel, onBack: () -> Unit, onProyecto: (String) -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    ProyectosContenido(estado, { vm.cargar() }, onBack, onProyecto)
}

@Composable
fun ProyectosContenido(
    estado: UiState<List<Proyecto>>,
    onReintentar: () -> Unit,
    onBack: () -> Unit,
    onProyecto: (String) -> Unit
) {
    Scaffold(topBar = { BarraSuperior("Proyectos", onBack) }) { padding ->
        ContenedorEstado(estado = estado, onReintentar = onReintentar, modifier = Modifier.padding(padding)) { lista ->
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Proyectos con causas y objetivos específicos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(lista, key = { it.id }) { p ->
                    Card(
                        onClick = { onProyecto(p.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LogoCentro(p.imagenUrl, Categoria.COMUNIDAD)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Chip(p.causa, fondo = MaterialTheme.colorScheme.secondaryContainer, textoColor = MaterialTheme.colorScheme.onSecondaryContainer)
                                    Spacer(Modifier.height(4.dp))
                                    Text(p.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(p.centroNombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            BarraAvance(p.avance, alto = 8)
                            Spacer(Modifier.height(4.dp))
                            Text("${(p.avance * 100).toInt()} % · Meta: ${p.objetivo}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraAvance(avance: Float, alto: Int) {
    LinearProgressIndicator(
        progress = { avance },
        modifier = Modifier
            .fillMaxWidth()
            .height(alto.dp)
            .clip(RoundedCornerShape((alto / 2).dp)),
        color = Amarillo,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
fun DetalleProyectoScreen(vm: DetalleProyectoViewModel, socialVm: SocialViewModel, onBack: () -> Unit, onDonar: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado) { (estado as? UiState.Datos)?.datos?.let { socialVm.actualizarLike(it.likes, it.meGusta) } }
    DetalleProyectoContenido(estado, socialVm, { vm.cargar() }, onBack, onDonar)
}

@Composable
fun DetalleProyectoContenido(
    estado: UiState<Proyecto>,
    socialVm: SocialViewModel,
    onReintentar: () -> Unit,
    onBack: () -> Unit,
    onDonar: () -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    Scaffold(
        topBar = { BarraSuperior("Proyecto", onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (estado is UiState.Datos) {
                Box(Modifier.padding(16.dp)) {
                    Button(onClick = onDonar, modifier = Modifier.fillMaxWidth()) { Text("Sumar a este proyecto") }
                }
            }
        }
    ) { padding ->
        ContenedorEstado(estado = estado, onReintentar = onReintentar, modifier = Modifier.padding(padding)) { p ->
            Column(
                Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ImagenPlaceholder(Categoria.COMUNIDAD, alto = 180, imagenUrl = p.imagenUrl)
                Column(Modifier.padding(16.dp)) {
                    Chip(p.causa, fondo = MaterialTheme.colorScheme.secondaryContainer, textoColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(Modifier.height(8.dp))
                    Text(p.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(p.centroNombre, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    BarraAvance(p.avance, alto = 10)
                    Spacer(Modifier.height(6.dp))
                    Text("${(p.avance * 100).toInt()} % de la meta", fontWeight = FontWeight.SemiBold)
                    Seccion("Objetivo")
                    Text(p.objetivo)
                    Seccion("Descripción")
                    Text(p.descripcion)
                    Spacer(Modifier.height(16.dp))
                    SeccionSocial(socialVm, TipoObjetivo.PROYECTO, snackbar)
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun PerfilScreen(
    vm: PerfilViewModel,
    onGaleria: () -> Unit,
    onInsignias: () -> Unit,
    onNotificaciones: () -> Unit,
    onAviso: () -> Unit,
    onCambiarCiudad: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val selector = rememberSelectorFoto(onFoto = { bytes -> vm.subirFoto(bytes, context.filesDir) })
    PerfilContenido(
        estado = estado,
        mensaje = mensaje,
        onMensajeMostrado = vm::mensajeMostrado,
        onGuardarPerfil = vm::actualizarPerfil,
        onFotoGaleria = selector.elegirDeGaleria,
        onFotoCamara = selector.tomarFoto,
        onGuardarFamilia = vm::actualizarFamilia,
        onAgregarIntegrante = vm::agregarIntegrante,
        onEditarIntegrante = vm::editarIntegrante,
        onEliminarIntegrante = vm::eliminarIntegrante,
        onTema = vm::cambiarTema,
        onGaleria = onGaleria,
        onInsignias = onInsignias,
        onNotificaciones = onNotificaciones,
        onAviso = onAviso,
        onCambiarCiudad = onCambiarCiudad,
        onCerrarSesion = {
            vm.cerrarSesion()
            onCerrarSesion()
        }
    )
}

@Composable
fun PerfilContenido(
    estado: PerfilUiState,
    mensaje: String?,
    onMensajeMostrado: () -> Unit,
    onGuardarPerfil: (String, String) -> Unit,
    onFotoGaleria: () -> Unit,
    onFotoCamara: () -> Unit,
    onGuardarFamilia: (String, String) -> Unit,
    onAgregarIntegrante: (String, String) -> Unit,
    onEditarIntegrante: (String, String, String) -> Unit,
    onEliminarIntegrante: (String) -> Unit,
    onTema: (ModoTema) -> Unit,
    onGaleria: () -> Unit,
    onInsignias: () -> Unit,
    onNotificaciones: () -> Unit,
    onAviso: () -> Unit,
    onCambiarCiudad: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val f = estado.familia
    val u = estado.usuario
    val esFamilia = u?.esFamilia != false
    val snackbar = remember { SnackbarHostState() }
    var editandoPerfil by rememberSaveable { mutableStateOf(false) }
    var editandoFamilia by rememberSaveable { mutableStateOf(false) }
    var agregando by rememberSaveable { mutableStateOf(false) }
    var editandoId by rememberSaveable { mutableStateOf<String?>(null) }
    var eliminandoId by rememberSaveable { mutableStateOf<String?>(null) }
    var eligiendoFoto by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(mensaje) {
        val m = mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        onMensajeMostrado()
    }

    if (editandoPerfil && u != null) {
        DialogoPerfil(
            nombre = u.nombre,
            descripcion = u.descripcion,
            onGuardar = { n, d -> onGuardarPerfil(n, d); editandoPerfil = false },
            onCerrar = { editandoPerfil = false }
        )
    }
    if (eligiendoFoto) {
        AlertDialog(
            onDismissRequest = { eligiendoFoto = false },
            title = { Text("Foto de perfil") },
            text = { Text("Se quitan los datos de ubicación de la imagen antes de guardarla.") },
            confirmButton = { TextButton(onClick = { eligiendoFoto = false; onFotoCamara() }) { Text("Cámara") } },
            dismissButton = { TextButton(onClick = { eligiendoFoto = false; onFotoGaleria() }) { Text("Galería") } }
        )
    }
    if (editandoFamilia) {
        DialogoFamilia(
            nombre = f.nombre,
            zona = f.zona,
            onGuardar = { n, z -> onGuardarFamilia(n, z); editandoFamilia = false },
            onCerrar = { editandoFamilia = false }
        )
    }
    if (agregando) {
        DialogoIntegrante(
            titulo = "Agregar integrante",
            onGuardar = { n, e -> onAgregarIntegrante(n, e); agregando = false },
            onCerrar = { agregando = false }
        )
    }
    editandoId?.let { id ->
        val integrante = f.integrantes.firstOrNull { it.id == id }
        if (integrante == null) {
            editandoId = null
        } else {
            DialogoIntegrante(
                titulo = "Editar integrante",
                nombreInicial = integrante.nombre,
                edadInicial = integrante.edad.toString(),
                onGuardar = { n, e -> onEditarIntegrante(id, n, e); editandoId = null },
                onCerrar = { editandoId = null }
            )
        }
    }
    eliminandoId?.let { id ->
        val integrante = f.integrantes.firstOrNull { it.id == id }
        if (integrante == null) {
            eliminandoId = null
        } else {
            AlertDialog(
                onDismissRequest = { eliminandoId = null },
                icon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("¿Quitar a ${integrante.nombre}?") },
                text = { Text("Dejará de aparecer en tu familia y no podrás seleccionarlo en nuevas inscripciones.") },
                confirmButton = {
                    TextButton(onClick = {
                        onEliminarIntegrante(id)
                        eliminandoId = null
                    }) { Text("Quitar", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = { TextButton(onClick = { eliminandoId = null }) { Text("Cancelar") } }
            )
        }
    }

    Scaffold(
        topBar = { BarraSuperior(if (esFamilia) "Mi familia" else "Mi cuenta") },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Azul),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Amarillo)
                            .clickable { eligiendoFoto = true },
                        contentAlignment = Alignment.Center
                    ) {
                        val foto = u?.fotoUrl ?: estado.fotoLocal?.let { java.io.File(it) }
                        if (foto != null) {
                            AsyncImage(model = foto, contentDescription = "Foto de perfil", contentScale = ContentScale.Crop, modifier = Modifier.size(64.dp))
                        } else {
                            Text(
                                inicialFamilia(if (esFamilia) f.nombre else (u?.nombre ?: "")),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = AzulOscuro
                            )
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(if (esFamilia) f.nombre else (u?.nombre ?: ""), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(u?.correo ?: f.correo, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                        if (esFamilia && f.zona.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, null, tint = Amarillo, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(f.zona, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                        if (!esFamilia && u != null) {
                            Text(if (u.esAdmin) "Administrador" else "Asociación", style = MaterialTheme.typography.bodySmall, color = Amarillo)
                        }
                    }
                    IconButton(onClick = { if (esFamilia) editandoFamilia = true else editandoPerfil = true }) {
                        Icon(Icons.Default.Edit, "Editar", tint = Color.White)
                    }
                }
            }

            if (estado.guardando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }

            if (u != null) {
                Seccion("Sobre mí")
                Card(
                    onClick = { editandoPerfil = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(u.nombre, fontWeight = FontWeight.SemiBold)
                        Text(
                            u.descripcion.ifBlank { "Agrega una descripción breve de tu familia o de ti." },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (esFamilia) {
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Estadistica("${estado.completadas}", "completadas", Modifier.weight(1f))
                    Estadistica("${estado.proximas}", "próximas", Modifier.weight(1f))
                    Estadistica("${estado.donaciones}", "donaciones", Modifier.weight(1f))
                }
                if (estado.pendientesPorEnviar > 0) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${estado.pendientesPorEnviar} cambio(s) se enviarán cuando haya conexión.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Seccion("Integrantes (${f.integrantes.size})")
                    TextButton(onClick = { agregando = true }) {
                        Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Agregar")
                    }
                }
                if (f.integrantes.isNotEmpty()) {
                    Text(
                        "Toca para editar · desliza a la derecha para quitar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                f.integrantes.forEach { integrante ->
                    key(integrante.id) {
                        FilaDeslizable(
                            modifier = Modifier.padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)),
                            icono = Icons.Default.Delete,
                            fondo = MaterialTheme.colorScheme.errorContainer,
                            tinte = MaterialTheme.colorScheme.onErrorContainer,
                            onDeslizar = { eliminandoId = integrante.id }
                        ) {
                            FilaIntegrante(integrante = integrante, onEditar = { editandoId = integrante.id })
                        }
                    }
                }
            }

            Seccion("Más")
            if (esFamilia) {
                ListItem(
                    headlineContent = { Text("Mi camino solidario") },
                    supportingContent = { Text("Nivel e insignias de la familia") },
                    leadingContent = { Icon(Icons.Default.EmojiEvents, null) },
                    modifier = Modifier.clickable(onClick = onInsignias)
                )
                ListItem(
                    headlineContent = { Text("Nuestras familias") },
                    supportingContent = { Text("Testimonios y fotos") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, null) },
                    modifier = Modifier.clickable(onClick = onGaleria)
                )
            }
            ListItem(
                headlineContent = { Text("Notificaciones") },
                leadingContent = { Icon(Icons.Default.Notifications, null) },
                modifier = Modifier.clickable(onClick = onNotificaciones)
            )
            ListItem(
                headlineContent = { Text("Cambiar ciudad") },
                leadingContent = { Icon(Icons.Default.Place, null) },
                modifier = Modifier.clickable(onClick = onCambiarCiudad)
            )
            ListItem(
                headlineContent = { Text("Aviso de privacidad") },
                supportingContent = { Text("Tus datos, tu decisión: incluye eliminar la cuenta") },
                leadingContent = { Icon(Icons.Default.PrivacyTip, null) },
                modifier = Modifier.clickable(onClick = onAviso)
            )

            Seccion("Apariencia")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModoTema.entries.forEach { m ->
                    FilterChip(selected = estado.modoTema == m, onClick = { onTema(m) }, label = { Text(m.etiqueta) })
                }
            }

            Divider(Modifier.padding(vertical = 8.dp))
            ListItem(
                headlineContent = { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) },
                leadingContent = { Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.clickable(onClick = onCerrarSesion)
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun inicialFamilia(nombre: String): String {
    val palabra = nombre.trim().split(" ").lastOrNull { it.isNotBlank() } ?: return "F"
    return palabra.first().uppercaseChar().toString()
}

@Composable
private fun FilaIntegrante(integrante: Integrante, onEditar: () -> Unit) {
    Card(
        onClick = onEditar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(integrante.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(integrante.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "${integrante.edad} años · ${if (integrante.edad >= 18) "Adulto" else "Menor"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 8.dp))
        }
    }
}

@Composable
private fun DialogoPerfil(nombre: String, descripcion: String, onGuardar: (String, String) -> Unit, onCerrar: () -> Unit) {
    var n by rememberSaveable { mutableStateOf(nombre) }
    var d by rememberSaveable { mutableStateOf(descripcion) }
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Mi perfil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = d, onValueChange = { d = it.take(300) }, label = { Text("Descripción") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { onGuardar(n, d) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

@Composable
private fun DialogoFamilia(
    nombre: String,
    zona: String,
    onGuardar: (String, String) -> Unit,
    onCerrar: () -> Unit
) {
    var n by rememberSaveable { mutableStateOf(nombre) }
    var z by rememberSaveable { mutableStateOf(zona) }
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Editar familia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = n,
                    onValueChange = { n = it },
                    label = { Text("Nombre de la familia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = z,
                    onValueChange = { z = it },
                    label = { Text("Zona o colonia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(onClick = { onGuardar(n, z) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

@Composable
private fun DialogoIntegrante(
    titulo: String,
    nombreInicial: String = "",
    edadInicial: String = "",
    onGuardar: (String, String) -> Unit,
    onCerrar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(nombreInicial) }
    var edad by rememberSaveable { mutableStateOf(edadInicial) }
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(titulo) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = edad,
                    onValueChange = { nuevo -> edad = nuevo.filter { it.isDigit() }.take(3) },
                    label = { Text("Edad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(onClick = { onGuardar(nombre, edad) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

@Composable
private fun Estadistica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun NotificacionesScreen(vm: NotificacionesViewModel, onBack: () -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    NotificacionesContenido(estado, { vm.cargar() }, onBack, vm::alternarLeida, vm::marcarTodasLeidas)
}

@Composable
fun NotificacionesContenido(
    estado: UiState<List<Notificacion>>,
    onReintentar: () -> Unit,
    onBack: () -> Unit,
    onAlternarLeida: (String) -> Unit = {},
    onTodasLeidas: () -> Unit = {}
) {
    val haySinLeer = (estado as? UiState.Datos)?.datos?.any { !it.leida } == true
    Scaffold(
        topBar = {
            BarraSuperior("Notificaciones", onBack) {
                if (haySinLeer) {
                    IconButton(onClick = onTodasLeidas) { Icon(Icons.Default.DoneAll, contentDescription = "Marcar todas como leídas") }
                }
            }
        }
    ) { padding ->
        ContenedorEstado(
            estado = estado,
            onReintentar = onReintentar,
            modifier = Modifier.padding(padding),
            mensajeVacio = "No tienes notificaciones."
        ) { lista ->
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(vertical = 8.dp)) {
                item {
                    Text(
                        "Desliza una notificación para marcarla como leída; vuelve a deslizar para dejarla sin leer.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                items(lista, key = { it.id }) { n ->
                    FilaDeslizable(
                        modifier = Modifier.animateItem(),
                        icono = if (n.leida) Icons.Default.MarkEmailUnread else Icons.Default.DoneAll,
                        fondo = if (n.leida) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                        tinte = if (n.leida) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                        ambosLados = true,
                        onDeslizar = { onAlternarLeida(n.id) }
                    ) {
                        ListItem(
                            headlineContent = { Text(n.titulo, fontWeight = if (n.leida) FontWeight.Normal else FontWeight.SemiBold) },
                            supportingContent = { Text(n.cuerpo) },
                            trailingContent = { Text(n.hace, style = MaterialTheme.typography.labelSmall) },
                            leadingContent = {
                                Box(
                                    Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (n.leida) MaterialTheme.colorScheme.surfaceVariant else Amarillo)
                                )
                            }
                        )
                    }
                    Divider()
                }
            }
        }
    }
}

// Fila que se desliza (estilo WhatsApp): muestra un fondo con ícono y dispara la acción sin quitar la fila
@Composable
fun FilaDeslizable(
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: ImageVector,
    fondo: Color,
    tinte: Color,
    ambosLados: Boolean = false,
    onDeslizar: () -> Unit,
    contenido: @Composable () -> Unit
) {
    val estado = rememberSwipeToDismissBoxState(positionalThreshold = { distancia -> distancia * 0.35f })
    val accion by rememberUpdatedState(onDeslizar)
    // Se dispara una sola vez por gesto: al llegar al extremo se ejecuta la acción y la fila regresa
    LaunchedEffect(estado.currentValue) {
        if (estado.currentValue != SwipeToDismissBoxValue.Settled) {
            accion()
            estado.reset()
        }
    }
    SwipeToDismissBox(
        state = estado,
        modifier = modifier,
        enableDismissFromStartToEnd = habilitado,
        enableDismissFromEndToStart = habilitado && ambosLados,
        backgroundContent = {
            val alineacion = if (estado.dismissDirection == SwipeToDismissBoxValue.EndToStart) Alignment.CenterEnd else Alignment.CenterStart
            Box(
                Modifier
                    .fillMaxSize()
                    .background(fondo)
                    .padding(horizontal = 24.dp),
                contentAlignment = alineacion
            ) { Icon(icono, null, tint = tinte) }
        },
        content = { Box(Modifier.background(MaterialTheme.colorScheme.background)) { contenido() } }
    )
}
