@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.familiasquesuman.demo.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.Categoria
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.components.BarraSuperior
import com.familiasquesuman.demo.ui.components.Chip
import com.familiasquesuman.demo.ui.components.ContenedorEstado
import com.familiasquesuman.demo.ui.components.ImagenPlaceholder
import com.familiasquesuman.demo.ui.components.Seccion
import com.familiasquesuman.demo.ui.components.SelloVerificado
import com.familiasquesuman.demo.ui.components.TarjetaActividad
import com.familiasquesuman.demo.ui.theme.Azul
import com.familiasquesuman.demo.ui.theme.Verde
import com.familiasquesuman.demo.ui.theme.VerdeClaro
import com.familiasquesuman.demo.ui.theme.AzulClaro
import com.familiasquesuman.demo.ui.viewmodel.ActividadesViewModel
import com.familiasquesuman.demo.ui.viewmodel.ConfirmacionViewModel
import com.familiasquesuman.demo.ui.viewmodel.DetalleActividadUiState
import com.familiasquesuman.demo.ui.viewmodel.DetalleActividadViewModel
import com.familiasquesuman.demo.ui.viewmodel.FormularioInscripcion
import com.familiasquesuman.demo.ui.viewmodel.InscripcionUiState
import com.familiasquesuman.demo.ui.viewmodel.InscripcionViewModel
import com.familiasquesuman.demo.ui.viewmodel.MisInscripcionesUiState
import com.familiasquesuman.demo.ui.viewmodel.MisInscripcionesViewModel
import com.familiasquesuman.demo.ui.viewmodel.SocialViewModel
import com.familiasquesuman.demo.model.TipoObjetivo
import com.familiasquesuman.demo.ui.agregarAlCalendario
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Mail

@Composable
fun ActividadesScreen(vm: ActividadesViewModel, onBack: () -> Unit, onActividad: (String) -> Unit) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val categoria by vm.categoria.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    ActividadesContenido(
        estado = estado,
        categoria = categoria,
        busqueda = busqueda,
        onCategoria = vm::cambiarCategoria,
        onBusqueda = vm::cambiarBusqueda,
        onReintentar = { vm.cargar() },
        onBack = onBack,
        onActividad = onActividad
    )
}

@Composable
fun ActividadesContenido(
    estado: UiState<List<Actividad>>,
    categoria: Categoria,
    busqueda: String,
    onCategoria: (Categoria) -> Unit,
    onBusqueda: (String) -> Unit,
    onReintentar: () -> Unit,
    onBack: () -> Unit,
    onActividad: (String) -> Unit
) {
    Scaffold(topBar = { BarraSuperior("Actividades en familia", onBack) }) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = onBusqueda,
                placeholder = { Text("Buscar actividad") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            FilaCategorias(seleccionada = categoria, onSeleccionar = onCategoria)
            ContenedorEstado(
                estado = estado,
                onReintentar = onReintentar,
                mensajeVacio = "No hay actividades con ese filtro."
            ) { lista ->
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lista, key = { it.id }) { actividad ->
                        TarjetaActividad(actividad) { onActividad(actividad.id) }
                    }
                }
            }
        }
    }
}

@Composable
fun FilaCategorias(seleccionada: Categoria, onSeleccionar: (Categoria) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(Categoria.entries) { cat ->
            FilterChip(
                selected = seleccionada == cat,
                onClick = { onSeleccionar(cat) },
                label = { Text(cat.etiqueta) }
            )
        }
    }
}

@Composable
fun DetalleActividadScreen(
    vm: DetalleActividadViewModel,
    socialVm: SocialViewModel,
    esFamilia: Boolean,
    onBack: () -> Unit,
    onInscribir: () -> Unit,
    onCentro: (String) -> Unit,
    onMensaje: (Centro) -> Unit
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado) { (estado as? UiState.Datos)?.datos?.actividad?.let { socialVm.actualizarLike(it.likes, it.meGusta) } }
    DetalleActividadContenido(
        estado = estado,
        socialVm = socialVm,
        esFamilia = esFamilia,
        onReintentar = { vm.cargar() },
        onBack = onBack,
        onInscribir = onInscribir,
        onCentro = onCentro,
        onMensaje = onMensaje
    )
}

@Composable
fun DetalleActividadContenido(
    estado: UiState<DetalleActividadUiState>,
    socialVm: SocialViewModel,
    esFamilia: Boolean,
    onReintentar: () -> Unit,
    onBack: () -> Unit,
    onInscribir: () -> Unit,
    onCentro: (String) -> Unit,
    onMensaje: (Centro) -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    Scaffold(
        topBar = { BarraSuperior("Actividad", onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (esFamilia && estado is UiState.Datos && !estado.datos.realizada) {
                Box(Modifier.padding(16.dp)) {
                    BotonInscripcion(estado.datos, onInscribir)
                }
            }
        }
    ) { padding ->
        ContenedorEstado(estado = estado, onReintentar = onReintentar, modifier = Modifier.padding(padding)) { datos ->
            DetalleActividadCuerpo(
                actividad = datos.actividad,
                centro = datos.centro,
                lleno = datos.lleno,
                realizada = datos.realizada,
                yaInscrito = datos.yaInscrito,
                esFamilia = esFamilia,
                socialVm = socialVm,
                snackbar = snackbar,
                modifier = Modifier.padding(padding),
                onCentro = onCentro,
                onMensaje = onMensaje
            )
        }
    }
}

@Composable
private fun BotonInscripcion(datos: DetalleActividadUiState, onInscribir: () -> Unit) {
    when {
        datos.yaInscrito -> OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.CheckCircle, null)
            Spacer(Modifier.width(8.dp))
            Text("Tu familia ya está inscrita")
        }
        datos.lleno -> Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
            Text("Sin cupos disponibles")
        }
        else -> Button(onClick = onInscribir, modifier = Modifier.fillMaxWidth()) {
            Text("Inscribir a mi familia")
        }
    }
}

@Composable
private fun DetalleActividadCuerpo(
    actividad: Actividad,
    centro: Centro,
    lleno: Boolean,
    realizada: Boolean,
    yaInscrito: Boolean,
    esFamilia: Boolean,
    socialVm: SocialViewModel,
    snackbar: SnackbarHostState,
    modifier: Modifier,
    onCentro: (String) -> Unit,
    onMensaje: (Centro) -> Unit
) {
    val context = LocalContext.current
    Column(modifier.verticalScroll(rememberScrollState())) {
        ImagenPlaceholder(actividad.categoria, alto = 200, imagenUrl = actividad.imagenUrl)
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Chip(actividad.categoria.etiqueta)
                Chip("Edad mínima ${actividad.edadMinima} años", fondo = MaterialTheme.colorScheme.surfaceVariant, textoColor = MaterialTheme.colorScheme.onSurfaceVariant)
                if (realizada) Chip("Completada", fondo = MaterialTheme.colorScheme.tertiaryContainer, textoColor = MaterialTheme.colorScheme.onTertiaryContainer)
            }
            Spacer(Modifier.height(10.dp))
            Text(actividad.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            TextButton(onClick = { onCentro(centro.id) }, contentPadding = PaddingValues(0.dp)) {
                Text(centro.nombre, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            SelloVerificado()

            Spacer(Modifier.height(16.dp))
            FilaInfo(Icons.Default.CalendarMonth, "${actividad.fechaTexto} · ${actividad.hora}")
            FilaInfo(Icons.Default.Place, actividad.lugar.ifBlank { centro.direccion })
            FilaInfo(
                Icons.Default.Group,
                if (lleno) "Sin cupos" else "${actividad.cuposDisponibles} de ${actividad.cuposTotales} cupos disponibles"
            )
            FilaInfo(Icons.Default.ChildCare, if (actividad.aptaNinos) "Ideal para niños desde ${actividad.edadMinima} años" else "Actividad para mayores de ${actividad.edadMinima} años")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                if (yaInscrito) {
                    OutlinedButton(
                        onClick = { context.agregarAlCalendario(actividad.titulo, actividad.lugar.ifBlank { centro.direccion }, actividad.fecha, actividad.horaInicio, actividad.horaFin) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Al calendario", maxLines = 1)
                    }
                }
                if (esFamilia) {
                    OutlinedButton(onClick = { onMensaje(centro) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 8.dp)) {
                        Icon(Icons.Default.Mail, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Preguntar", maxLines = 1)
                    }
                }
            }

            Seccion("Descripción")
            Text(actividad.descripcion, style = MaterialTheme.typography.bodyMedium)

            Seccion("Requisitos")
            actividad.requisitos.forEach {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
            SeccionSocial(socialVm, TipoObjetivo.ACTIVIDAD, snackbar)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilaInfo(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icono, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun InscripcionScreen(vm: InscripcionViewModel, onBack: () -> Unit, onConfirmado: () -> Unit) {
    val datos by vm.datos.collectAsStateWithLifecycle()
    val formulario by vm.formulario.collectAsStateWithLifecycle()
    val realizada by vm.inscripcionRealizada.collectAsStateWithLifecycle()

    LaunchedEffect(realizada) {
        if (realizada) onConfirmado()
    }

    InscripcionContenido(
        datos = datos,
        formulario = formulario,
        onReintentar = { vm.cargar() },
        onAlternar = vm::alternarIntegrante,
        onComentario = vm::cambiarComentario,
        onConfirmar = vm::confirmar,
        onBack = onBack
    )
}

@Composable
fun InscripcionContenido(
    datos: UiState<InscripcionUiState>,
    formulario: FormularioInscripcion,
    onReintentar: () -> Unit,
    onAlternar: (String) -> Unit,
    onComentario: (String) -> Unit,
    onConfirmar: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { BarraSuperior("Inscripción", onBack) },
        bottomBar = {
            if (datos is UiState.Datos) {
                Column(Modifier.padding(16.dp)) {
                    if (formulario.error != null) {
                        Text(
                            formulario.error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = onConfirmar,
                        enabled = formulario.puedeConfirmar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (formulario.enviando) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.width(10.dp))
                            Text("Registrando…")
                        } else {
                            Text("Confirmar inscripción (${formulario.seleccionados.size})")
                        }
                    }
                }
            }
        }
    ) { padding ->
        ContenedorEstado(estado = datos, onReintentar = onReintentar, modifier = Modifier.padding(padding)) { d ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(d.actividad.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${d.actividad.fechaTexto} · ${d.actividad.hora}", color = MaterialTheme.colorScheme.onSurfaceVariant)

                Seccion("¿Quiénes van?")
                d.integrantes.forEach { integrante ->
                    val elegible = integrante.id in d.elegibles
                    val marcado = integrante.id in formulario.seleccionados
                    Card(
                        onClick = { onAlternar(integrante.id) },
                        enabled = elegible,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (marcado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = marcado,
                                enabled = elegible,
                                onCheckedChange = { onAlternar(integrante.id) }
                            )
                            Column {
                                Text(integrante.nombre, fontWeight = FontWeight.SemiBold)
                                Text(
                                    if (elegible) "${integrante.edad} años"
                                    else "${integrante.edad} años · menor a la edad mínima (${d.actividad.edadMinima})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Seccion("Comentario para el centro (opcional)")
                OutlinedTextField(
                    value = formulario.comentario,
                    onValueChange = onComentario,
                    placeholder = { Text("Ej. Llegaremos 10 minutos tarde") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ConfirmacionScreen(
    vm: ConfirmacionViewModel,
    onVerInscripciones: () -> Unit,
    onInicio: () -> Unit
) {
    val inscripcion by vm.inscripcion.collectAsStateWithLifecycle()
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(96.dp))
        Spacer(Modifier.height(16.dp))
        Text("¡Listo, familia!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Tu inscripción quedó registrada. El centro recibirá tus datos y te enviaremos un recordatorio un día antes.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        inscripcion?.let { i ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(i.actividadTitulo, fontWeight = FontWeight.Bold)
                    Text(i.centroNombre, style = MaterialTheme.typography.bodySmall)
                    Text("${i.fechaTexto} · ${i.hora}", style = MaterialTheme.typography.bodySmall)
                    Text("Van: ${i.nombres.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onVerInscripciones, modifier = Modifier.fillMaxWidth()) { Text("Ver mis actividades") }
        TextButton(onClick = onInicio) { Text("Volver al inicio") }
    }
}

@Composable
fun MisInscripcionesScreen(
    vm: MisInscripcionesViewModel,
    onActividad: (String) -> Unit,
    onEncuesta: (String, String) -> Unit,
    onVerEncuestas: (String) -> Unit
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val cancelandoId by vm.cancelandoId.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        val m = mensaje ?: return@LaunchedEffect
        snackbar.showSnackbar(m)
        vm.mensajeMostrado()
    }
    MisInscripcionesContenido(
        estado = estado,
        cancelandoId = cancelandoId,
        snackbar = snackbar,
        onReintentar = { vm.cargar() },
        onCancelar = vm::cancelar,
        onActividad = onActividad,
        onEncuesta = onEncuesta,
        onVerEncuestas = onVerEncuestas
    )
}

@Composable
fun MisInscripcionesContenido(
    estado: UiState<MisInscripcionesUiState>,
    cancelandoId: String?,
    snackbar: SnackbarHostState,
    onReintentar: () -> Unit,
    onCancelar: (String) -> Unit,
    onActividad: (String) -> Unit,
    onEncuesta: (String, String) -> Unit,
    onVerEncuestas: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var confirmarId by rememberSaveable { mutableStateOf<String?>(null) }

    confirmarId?.let { id ->
        AlertDialog(
            onDismissRequest = { confirmarId = null },
            title = { Text("¿Cancelar inscripción?") },
            text = { Text("Los cupos de tu familia quedarán libres para otra familia.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarId = null
                    onCancelar(id)
                }) { Text("Sí, cancelar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmarId = null }) { Text("Volver") } }
        )
    }

    Scaffold(
        topBar = { BarraSuperior("Mis actividades") },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        ContenedorEstado(
            estado = estado,
            onReintentar = onReintentar,
            modifier = Modifier.padding(padding),
            mensajeVacio = "Aún no tienes actividades. Inscríbete a una desde Inicio."
        ) { datos ->
            val lista = if (tab == 0) datos.proximas else datos.pasadas
            Column(Modifier.padding(padding)) {
                if (datos.pendientesPorEnviar > 0) {
                    Text(
                        "Sin conexión: ${datos.pendientesPorEnviar} cambio(s) se enviarán automáticamente cuando haya internet.",
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                TabRow(selectedTabIndex = tab) {
                    Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Próximas (${datos.proximas.size})") })
                    Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Pasadas (${datos.pasadas.size})") })
                }
                if (lista.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Nada por aquí todavía.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(lista, key = { it.id }) { i ->
                            Card(
                                onClick = { onActividad(i.actividadId) },
                                modifier = Modifier.fillMaxWidth().animateItem(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(i.actividadTitulo, fontWeight = FontWeight.SemiBold)
                                            Text(i.centroNombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Chip(etiquetaEstado(i.estado, i.pasada))
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text("${i.fechaTexto} · ${i.hora}", style = MaterialTheme.typography.bodySmall)
                                    Text("Van: ${i.nombres.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                                    if (i.pendiente) {
                                        Text("Se confirmará con el centro cuando haya conexión.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        if (!i.pasada && !i.pendiente) {
                                            TextButton(
                                                onClick = { context.agregarAlCalendario(i.actividadTitulo, i.lugar, i.fecha, i.horaInicio, i.horaFin) },
                                                contentPadding = PaddingValues(horizontal = 4.dp)
                                            ) {
                                                Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Calendario")
                                            }
                                        }
                                        if (!i.pendiente && i.estado != "cancelada") {
                                            if (!i.pasada && !i.encuestaAntes) {
                                                TextButton(onClick = { onEncuesta(i.id, "antes") }, contentPadding = PaddingValues(horizontal = 4.dp)) { Text("Encuesta previa") }
                                            }
                                            if (i.pasada && !i.encuestaDespues) {
                                                TextButton(onClick = { onEncuesta(i.id, "despues") }, contentPadding = PaddingValues(horizontal = 4.dp)) { Text("Contar cómo fue") }
                                            }
                                            if (i.encuestaAntes || i.encuestaDespues) {
                                                TextButton(onClick = { onVerEncuestas(i.id) }, contentPadding = PaddingValues(horizontal = 4.dp)) { Text("Ver respuestas") }
                                            }
                                        }
                                    }
                                    if (!i.pasada && (i.estado == "confirmada" || i.pendiente)) {
                                        TextButton(
                                            onClick = { confirmarId = i.id },
                                            enabled = cancelandoId == null,
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            if (cancelandoId == i.id) {
                                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                            } else {
                                                Text("Cancelar inscripción", color = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun etiquetaEstado(estado: String, pasada: Boolean): String = when (estado) {
    "pendiente" -> "Pendiente de confirmar"
    "asistio" -> "Completada"
    "no_asistio" -> "No asistieron"
    "cancelada" -> "Cancelada"
    else -> if (pasada) "Realizada" else "Confirmada"
}
