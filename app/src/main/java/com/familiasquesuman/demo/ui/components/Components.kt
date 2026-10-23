package com.familiasquesuman.demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.Categoria
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.ui.theme.Amarillo
import com.familiasquesuman.demo.ui.theme.Azul
import com.familiasquesuman.demo.ui.theme.Verde
import com.familiasquesuman.demo.ui.theme.AzulClaro

fun Categoria.icono(): ImageVector = when (this) {
    Categoria.NINOS -> Icons.Default.SentimentSatisfied
    Categoria.ADULTOS_MAYORES -> Icons.Default.Groups
    Categoria.MEDIO_AMBIENTE -> Icons.Default.Eco
    Categoria.ANIMALES -> Icons.Default.Pets
    Categoria.COMUNIDAD -> Icons.Default.VolunteerActivism
    Categoria.TODAS -> Icons.Default.VolunteerActivism
}

fun Categoria.color(): Color = when (this) {
    Categoria.NINOS -> Color(0xFFFFB74D)
    Categoria.ADULTOS_MAYORES -> Color(0xFF9575CD)
    Categoria.MEDIO_AMBIENTE -> Color(0xFF66BB6A)
    Categoria.ANIMALES -> Color(0xFF4FC3F7)
    Categoria.COMUNIDAD -> Color(0xFFEF9A9A)
    Categoria.TODAS -> Azul
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onBack: (() -> Unit)? = null, acciones: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

// Imagen servida por la API; si no hay, un bloque de color con el ícono de la categoría
@Composable
fun ImagenPlaceholder(categoria: Categoria, alto: Int = 140, imagenUrl: String? = null, modifier: Modifier = Modifier) {
    if (imagenUrl != null) {
        AsyncImage(
            model = imagenUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .height(alto.dp)
                .background(categoria.color().copy(alpha = 0.2f))
        )
        return
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto.dp)
            .background(categoria.color().copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = categoria.icono(),
            contentDescription = null,
            tint = categoria.color(),
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun Chip(texto: String, fondo: Color = MaterialTheme.colorScheme.primaryContainer, textoColor: Color = MaterialTheme.colorScheme.onPrimaryContainer) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelMedium, color = textoColor)
    }
}

@Composable
fun SelloVerificado() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            "Verificado por Familias que Suman",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun TarjetaActividad(actividad: Actividad, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            ImagenPlaceholder(actividad.categoria, imagenUrl = actividad.imagenUrl)
            Column(Modifier.padding(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Chip(actividad.categoria.etiqueta)
                    if (actividad.cuposDisponibles == 0) {
                        Chip("Lleno", fondo = MaterialTheme.colorScheme.errorContainer, textoColor = MaterialTheme.colorScheme.onErrorContainer)
                    } else {
                        Chip("${actividad.cuposDisponibles} cupos", fondo = MaterialTheme.colorScheme.secondaryContainer, textoColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(actividad.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(actividad.centroNombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text("${actividad.fechaTexto} · ${actividad.hora}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun LogoCentro(imagenUrl: String?, categoria: Categoria, tamano: Int = 56) {
    Box(
        Modifier
            .size(tamano.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (imagenUrl != null) MaterialTheme.colorScheme.surface else categoria.color().copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        if (imagenUrl != null) {
            AsyncImage(
                model = imagenUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(tamano.dp)
            )
        } else {
            Icon(categoria.icono(), null, tint = categoria.color())
        }
    }
}

@Composable
fun TarjetaCentro(centro: Centro, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            LogoCentro(centro.imagenUrl, centro.categoria)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(centro.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(centro.direccion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                if (centro.verificado) SelloVerificado()
            }
        }
    }
}

@Composable
fun Seccion(titulo: String) {
    Text(
        titulo,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
    )
}

@Composable
fun AcentoAmarillo() {
    Box(
        Modifier
            .width(40.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Amarillo)
    )
}
