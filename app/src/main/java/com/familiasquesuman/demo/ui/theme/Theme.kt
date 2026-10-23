package com.familiasquesuman.demo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.familiasquesuman.demo.model.ModoTema

// Colores de marca (familiasquesuman.com). En pantallas usar los tokens de MaterialTheme.colorScheme.
val Azul = Color(0xFF1F3A5F)
val AzulOscuro = Color(0xFF16263F)
val AzulClaro = Color(0xFFE8EEF6)
val AzulBoton = Color(0xFF2F6BE0)
val Amarillo = Color(0xFFF6B428)
val AmarilloClaro = Color(0xFFFFF3D6)
val Verde = Color(0xFF2E9E5B)
val VerdeClaro = Color(0xFFE6F5EC)
val Fondo = Color(0xFFF6F8FA)
val GrisTexto = Color(0xFF5F6B7A)
val Rojo = Color(0xFFB71C1C)

private val LightColors = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = Amarillo,
    onSecondary = Color(0xFF3A2E00),
    secondaryContainer = AmarilloClaro,
    onSecondaryContainer = Color(0xFF7A5A00),
    tertiary = Verde,
    onTertiary = Color.White,
    tertiaryContainer = VerdeClaro,
    onTertiaryContainer = Color(0xFF0E4D2A),
    background = Fondo,
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFEEF1F5),
    onSurfaceVariant = GrisTexto,
    outline = Color(0xFFC3C9D2),
    error = Rojo,
    errorContainer = Color(0xFFFFE0E0),
    onErrorContainer = Rojo
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9DB8E0),
    onPrimary = Color(0xFF0B1A2E),
    primaryContainer = Color(0xFF2B4A74),
    onPrimaryContainer = Color(0xFFDCE6F5),
    secondary = Amarillo,
    onSecondary = Color(0xFF3A2E00),
    secondaryContainer = Color(0xFF5A4400),
    onSecondaryContainer = Color(0xFFFFE3A3),
    tertiary = Color(0xFF7CD39C),
    onTertiary = Color(0xFF003919),
    tertiaryContainer = Color(0xFF1F5B37),
    onTertiaryContainer = Color(0xFFBFF0CF),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE2E5E9),
    surface = Color(0xFF171C22),
    onSurface = Color(0xFFE2E5E9),
    surfaceVariant = Color(0xFF242B34),
    onSurfaceVariant = Color(0xFFB4BCC8),
    outline = Color(0xFF5B6573),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// true cuando la app está en modo oscuro (según el ajuste de la app, no solo el del sistema)
val LocalTemaOscuro = staticCompositionLocalOf { false }

@Composable
fun FamiliasQueSumanTheme(modo: ModoTema = ModoTema.SISTEMA, content: @Composable () -> Unit) {
    val oscuro = when (modo) {
        ModoTema.SISTEMA -> isSystemInDarkTheme()
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
    }
    CompositionLocalProvider(LocalTemaOscuro provides oscuro) {
        MaterialTheme(
            colorScheme = if (oscuro) DarkColors else LightColors,
            content = content
        )
    }
}
