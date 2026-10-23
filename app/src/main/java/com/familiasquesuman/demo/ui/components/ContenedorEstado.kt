package com.familiasquesuman.demo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.familiasquesuman.demo.ui.UiState

@Composable
fun <T> ContenedorEstado(
    estado: UiState<T>,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
    mensajeVacio: String = "No hay nada por aquí todavía.",
    contenido: @Composable (T) -> Unit
) {
    when (estado) {
        is UiState.Cargando -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is UiState.Vacio -> MensajeCentrado(
            modifier = modifier,
            icono = { Icon(Icons.Default.Inbox, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            titulo = "Sin resultados",
            texto = mensajeVacio
        )
        is UiState.Error -> MensajeCentrado(
            modifier = modifier,
            icono = { Icon(Icons.Default.CloudOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error) },
            titulo = "Algo salió mal",
            texto = estado.mensaje
        ) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onReintentar) { Text("Reintentar") }
        }
        is UiState.Datos -> contenido(estado.datos)
    }
}

@Composable
private fun MensajeCentrado(
    modifier: Modifier,
    icono: @Composable () -> Unit,
    titulo: String,
    texto: String,
    extra: @Composable () -> Unit = {}
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        icono()
        Spacer(Modifier.height(12.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(
            texto,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        extra()
    }
}
