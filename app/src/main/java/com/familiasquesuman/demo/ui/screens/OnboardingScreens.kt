@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.familiasquesuman.demo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familiasquesuman.demo.R
import com.familiasquesuman.demo.ui.theme.LocalTemaOscuro
import com.familiasquesuman.demo.model.Ciudad
import com.familiasquesuman.demo.ui.components.rememberGoogleSignIn
import com.familiasquesuman.demo.ui.theme.Amarillo
import com.familiasquesuman.demo.ui.theme.Azul
import com.familiasquesuman.demo.ui.theme.AzulClaro
import com.familiasquesuman.demo.ui.theme.AzulOscuro
import com.familiasquesuman.demo.ui.theme.Fondo
import com.familiasquesuman.demo.ui.viewmodel.DestinoInicial
import com.familiasquesuman.demo.ui.viewmodel.FormularioSesion
import com.familiasquesuman.demo.ui.viewmodel.SesionViewModel
import com.familiasquesuman.demo.ui.viewmodel.SplashViewModel

@Composable
fun SplashScreen(vm: SplashViewModel, onListo: (DestinoInicial) -> Unit) {
    val destino by vm.destino.collectAsStateWithLifecycle()
    LaunchedEffect(destino) {
        val d = destino ?: return@LaunchedEffect
        onListo(d)
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Logo(ancho = 300)
            Spacer(Modifier.height(12.dp))
            Text("Pequeñas acciones, gran impacto", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun Logo(ancho: Int) {
    Image(
        painter = painterResource(if (LocalTemaOscuro.current) R.drawable.logo_fqs_claro else R.drawable.logo_fqs),
        contentDescription = "Familias que Suman",
        modifier = Modifier.width(ancho.dp)
    )
}

@Composable
fun CiudadScreen(vm: SesionViewModel, onContinuar: () -> Unit) {
    val ciudad by vm.ciudad.collectAsStateWithLifecycle()
    CiudadContenido(
        seleccionada = ciudad,
        onSeleccionar = vm::seleccionarCiudad,
        onContinuar = onContinuar
    )
}

@Composable
fun CiudadContenido(seleccionada: Ciudad, onSeleccionar: (Ciudad) -> Unit, onContinuar: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
        Spacer(Modifier.height(12.dp))
        Text("¿En qué ciudad quieres sumar?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Selecciona tu ciudad para ver la información más cercana.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Ciudad.entries.forEach { ciudad ->
            val esta = seleccionada == ciudad
            Card(
                onClick = { onSeleccionar(ciudad) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (esta) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = esta, onClick = { onSeleccionar(ciudad) })
                    Spacer(Modifier.width(8.dp))
                    Text(ciudad.etiqueta, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onContinuar, modifier = Modifier.fillMaxWidth()) {
            Text("Continuar")
        }
    }
}

// ---------- Login (RF-17, RF-20) ----------

@Composable
fun LoginScreen(vm: SesionViewModel, onEntrar: () -> Unit, onRegistro: () -> Unit, onAviso: () -> Unit) {
    val formulario by vm.formulario.collectAsStateWithLifecycle()
    val lista by vm.sesionLista.collectAsStateWithLifecycle()
    LaunchedEffect(lista) { if (lista) onEntrar() }
    val google = rememberGoogleSignIn(onToken = vm::iniciarSesionGoogle, onError = vm::errorGoogle)

    if (formulario.pedirFamiliaGoogle) {
        AlertDialog(
            onDismissRequest = vm::cancelarFamiliaGoogle,
            title = { Text("¡Bienvenida, familia!") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Es tu primera vez. ¿Cómo se llama tu familia? Al continuar aceptas el aviso de privacidad.", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = formulario.nombreFamilia,
                        onValueChange = vm::cambiarNombreFamilia,
                        label = { Text("Nombre de la familia") },
                        placeholder = { Text("Ej. Familia Ramírez") },
                        singleLine = true,
                        enabled = !formulario.cargando,
                        modifier = Modifier.fillMaxWidth()
                    )
                    formulario.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            },
            confirmButton = {
                Button(onClick = vm::confirmarFamiliaGoogle, enabled = !formulario.cargando && formulario.nombreFamilia.trim().length >= 2) {
                    if (formulario.cargando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Continuar")
                }
            },
            dismissButton = { TextButton(onClick = vm::cancelarFamiliaGoogle, enabled = !formulario.cargando) { Text("Cancelar") } }
        )
    }

    LoginContenido(
        f = formulario,
        onCorreo = vm::cambiarCorreo,
        onPassword = vm::cambiarPassword,
        onMostrarPassword = vm::alternarMostrarPassword,
        onEntrar = vm::iniciarSesion,
        onGoogle = google,
        onRegistro = onRegistro,
        onAviso = onAviso
    )
}

@Composable
fun LoginContenido(
    f: FormularioSesion,
    onCorreo: (String) -> Unit,
    onPassword: (String) -> Unit,
    onMostrarPassword: () -> Unit,
    onEntrar: () -> Unit,
    onGoogle: () -> Unit,
    onRegistro: () -> Unit,
    onAviso: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Encabezado()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-44).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Bienvenido", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Inicia sesión para inscribir a tu familia en actividades, donar en especie y llevar tu registro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))

                CampoCorreo(f.correo, onCorreo, f.cargando)
                Spacer(Modifier.height(10.dp))
                CampoPassword(f.password, f.mostrarPassword, onPassword, onMostrarPassword, f.cargando)

                AnimatedVisibility(visible = f.error != null) {
                    Column {
                        Spacer(Modifier.height(10.dp))
                        Text(f.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }
                }
                Spacer(Modifier.height(18.dp))

                BotonPrincipal(texto = "Iniciar sesión", cargando = f.cargando, onClick = onEntrar)

                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f))
                    Text("  o  ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onGoogle,
                    enabled = !f.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color(0xFF4285F4), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Continuar con Google", style = MaterialTheme.typography.titleSmall)
                }

                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onRegistro, enabled = !f.cargando) {
                    Text("¿Aún no tienes cuenta? Crear cuenta", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(8.dp))
                PieAviso(onAviso)
            }
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

// ---------- Registro (RF-16) ----------

@Composable
fun RegistroScreen(vm: SesionViewModel, onBack: () -> Unit, onEntrar: () -> Unit, onAviso: () -> Unit) {
    val formulario by vm.formulario.collectAsStateWithLifecycle()
    val ciudad by vm.ciudad.collectAsStateWithLifecycle()
    val lista by vm.sesionLista.collectAsStateWithLifecycle()
    LaunchedEffect(lista) { if (lista) onEntrar() }

    RegistroContenido(
        f = formulario,
        ciudad = ciudad,
        onNombre = vm::cambiarNombre,
        onNombreFamilia = vm::cambiarNombreFamilia,
        onCorreo = vm::cambiarCorreo,
        onPassword = vm::cambiarPassword,
        onMostrarPassword = vm::alternarMostrarPassword,
        onAceptaAviso = vm::cambiarAceptaAviso,
        onRegistrar = vm::registrar,
        onBack = onBack,
        onAviso = onAviso
    )
}

@Composable
fun RegistroContenido(
    f: FormularioSesion,
    ciudad: Ciudad,
    onNombre: (String) -> Unit,
    onNombreFamilia: (String) -> Unit,
    onCorreo: (String) -> Unit,
    onPassword: (String) -> Unit,
    onMostrarPassword: () -> Unit,
    onAceptaAviso: (Boolean) -> Unit,
    onRegistrar: () -> Unit,
    onBack: () -> Unit,
    onAviso: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
            Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Text(
            "Tu familia en ${ciudad.etiqueta}. Podrás cambiar la ciudad después.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = f.nombre,
            onValueChange = onNombre,
            label = { Text("Tu nombre") },
            singleLine = true,
            enabled = !f.cargando,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = f.nombreFamilia,
            onValueChange = onNombreFamilia,
            label = { Text("Nombre de la familia") },
            placeholder = { Text("Ej. Familia Ramírez") },
            singleLine = true,
            enabled = !f.cargando,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        CampoCorreo(f.correo, onCorreo, f.cargando)
        Spacer(Modifier.height(10.dp))
        CampoPassword(f.password, f.mostrarPassword, onPassword, onMostrarPassword, f.cargando)
        Text(
            "Mínimo 12 caracteres.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = f.aceptaAviso, onCheckedChange = onAceptaAviso, enabled = !f.cargando)
            Text("He leído y acepto el ", style = MaterialTheme.typography.bodySmall)
            Text(
                "Aviso de privacidad",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onAviso)
            )
        }

        if (f.error != null) {
            Spacer(Modifier.height(6.dp))
            Text(f.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(18.dp))
        BotonPrincipal(texto = "Crear cuenta", cargando = f.cargando, onClick = onRegistrar)
        Spacer(Modifier.height(24.dp))
        Spacer(Modifier.navigationBarsPadding())
    }
}

// ---------- Piezas compartidas ----------

@Composable
private fun Encabezado() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(AzulOscuro, Azul)),
                RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
            )
            .statusBarsPadding()
            .padding(top = 40.dp, bottom = 72.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(220.dp, 36.dp)
                .offset(x = 120.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Amarillo.copy(alpha = 0.18f))
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) { Logo(ancho = 220) }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Pequeñas acciones, gran impacto",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Monterrey · Hermosillo",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun CampoCorreo(valor: String, onCambio: (String) -> Unit, cargando: Boolean) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text("Correo electrónico") },
        singleLine = true,
        enabled = !cargando,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
    )
}

// RF-20: mostrar / ocultar contraseña
@Composable
private fun CampoPassword(
    valor: String,
    visible: Boolean,
    onCambio: (String) -> Unit,
    onAlternar: () -> Unit,
    cargando: Boolean
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text("Contraseña") },
        singleLine = true,
        enabled = !cargando,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onAlternar) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BotonPrincipal(texto: String, cargando: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(texto, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun PieAviso(onAviso: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "Al continuar aceptas el ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Aviso de privacidad",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onAviso)
        )
    }
}
