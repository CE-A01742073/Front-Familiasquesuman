package com.familiasquesuman.demo.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.familiasquesuman.demo.MainActivity
import com.familiasquesuman.demo.data.AlmacenLocal
import com.familiasquesuman.demo.model.Inscripcion

// Widget de pantalla de inicio: siguiente actividad confirmada y cuántas lleva la familia. Solo datos locales.
class ProximaActividadWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val datos = AlmacenLocal(context).leer()
        val proxima = datos.inscripciones
            .filter { !it.pasada && it.estado == "confirmada" }
            .minByOrNull { it.fecha + it.horaInicio }
        val completadas = datos.inscripciones.count { it.estado == "asistio" }
        val sesion = datos.familia != null
        provideContent { GlanceTheme { Contenido(proxima, completadas, sesion) } }
    }

    suspend fun actualizar(context: Context) = updateAll(context)

    @Composable
    private fun Contenido(proxima: Inscripcion?, completadas: Int, sesion: Boolean) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .cornerRadius(16.dp)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Text(
                "Familias que Suman",
                style = TextStyle(color = GlanceTheme.colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(GlanceModifier.height(4.dp))
            when {
                !sesion -> Text("Inicia sesión para ver tu próxima actividad", style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 14.sp))
                proxima == null -> Text("Sin actividades próximas. Toca para inscribirte.", style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 14.sp))
                else -> {
                    Text(proxima.actividadTitulo, style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold), maxLines = 1)
                    Text("${proxima.fechaTexto} · ${proxima.hora}", style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant, fontSize = 13.sp), maxLines = 1)
                    Text(proxima.lugar, style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant, fontSize = 12.sp), maxLines = 1)
                }
            }
            Spacer(GlanceModifier.height(6.dp))
            Text(
                "$completadas ${if (completadas == 1) "actividad completada" else "actividades completadas"}",
                style = TextStyle(color = GlanceTheme.colors.secondary, fontSize = 12.sp),
                modifier = GlanceModifier.fillMaxWidth()
            )
        }
    }
}

class ProximaActividadWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ProximaActividadWidget()
}
