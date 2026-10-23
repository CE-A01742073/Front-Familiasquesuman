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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familiasquesuman.demo.ui.UiState
import com.familiasquesuman.demo.ui.components.AcentoAmarillo
import com.familiasquesuman.demo.ui.components.ContenedorEstado
import com.familiasquesuman.demo.ui.components.Seccion
import com.familiasquesuman.demo.ui.components.SelloVerificado
import com.familiasquesuman.demo.ui.components.TarjetaActividad
import com.familiasquesuman.demo.ui.theme.AzulClaro
import com.familiasquesuman.demo.ui.viewmodel.HomeUiState
import com.familiasquesuman.demo.ui.viewmodel.HomeViewModel

data class HomeAcciones(
    val onActividades: () -> Unit,
    val onDonar: () -> Unit,
    val onProyectos: () -> Unit,
    val onDirectorio: () -> Unit,
    val onNotificaciones: () -> Unit,
    val onCambiarCiudad: () -> Unit,
    val onActividad: (String) -> Unit,
    val onGaleria: () -> Unit,
    val onGestion: () -> Unit
)

@Composable
fun HomeScreen(vm: HomeViewModel, gestiona: Boolean, acciones: HomeAcciones) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val avisos by vm.avisos.collectAsStateWithLifecycle()
    HomeContenido(estado = estado, avisos = avisos, gestiona = gestiona, onReintentar = { vm.cargar() }, onDescartarAvisos = vm::descartarAvisos, acciones = acciones)
}

@Composable
fun HomeContenido(
    estado: UiState<HomeUiState>,
    avisos: List<String>,
    gestiona: Boolean,
    onReintentar: () -> Unit,
    onDescartarAvisos: () -> Unit,
    acciones: HomeAcciones
) {
    ContenedorEstado(estado = estado, onReintentar = onReintentar) { datos ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Logo(ancho = 260)
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = acciones.onCambiarCiudad,
                        label = { Text(datos.ciudad.etiqueta, maxLines = 1) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp)) },
                        trailingIcon = { Icon(Icons.Default.ExpandMore, null, modifier = Modifier.size(16.dp)) }
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = acciones.onNotificaciones) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                }
            }

            if (avisos.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Avisos de sincronización", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onErrorContainer)
                            avisos.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer) }
                            TextButton(onClick = onDescartarAvisos) { Text("Entendido") }
                        }
                    }
                }
            }
            if (gestiona) {
                item {
                    Card(
                        onClick = acciones.onGestion,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Dashboard, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Panel de gestión", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text("Actividades, asistentes, campañas y proyectos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }
            }
            item {
                Text("¿Cómo quieres ayudar hoy?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                AcentoAmarillo()
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Pilar("Actividades", "En familia durante el año", Icons.Default.Event, Color(0xFFFFB74D), Modifier.weight(1f), acciones.onActividades)
                    Pilar("Quiero donar", "Apoyo en especie y tiempo", Icons.Default.Favorite, Color(0xFFEF9A9A), Modifier.weight(1f), acciones.onDonar)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Pilar("Proyectos", "Causas con objetivos específicos", Icons.Default.Flag, Color(0xFF9575CD), Modifier.weight(1f), acciones.onProyectos)
                    Pilar("Directorio de visiteo", "Centros para visitar en familia", Icons.Default.Map, Color(0xFF66BB6A), Modifier.weight(1f), acciones.onDirectorio)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        SelloVerificado()
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Todos los centros del directorio están visitados y verificados por el equipo.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Seccion("Próximas actividades")
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = acciones.onActividades) { Text("Ver todas") }
                }
            }
            if (datos.proximas.isEmpty()) {
                item {
                    Text(
                        "No hay actividades próximas en ${datos.ciudad.etiqueta}.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(datos.proximas, key = { it.id }) { actividad ->
                TarjetaActividad(actividad) { acciones.onActividad(actividad.id) }
            }
            if (datos.testimonios.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Seccion("Nuestras familias")
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = acciones.onGaleria) { Text("Ver más") }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(datos.testimonios, key = { it.id }) { t ->
                            Card(
                                onClick = acciones.onGaleria,
                                modifier = Modifier.width(220.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column {
                                    if (t.fotoUrl != null) {
                                        AsyncImage(model = t.fotoUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(110.dp))
                                    }
                                    Column(Modifier.padding(10.dp)) {
                                        Text(t.familiaNombre, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                                        Text(t.texto, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun Pilar(
    titulo: String,
    sub: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) { Icon(icono, null, tint = color) }
            Column {
                Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
        }
    }
}
