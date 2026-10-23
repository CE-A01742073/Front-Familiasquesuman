package com.familiasquesuman.demo.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.familiasquesuman.demo.ui.screens.LoginContenido
import com.familiasquesuman.demo.ui.theme.FamiliasQueSumanTheme
import com.familiasquesuman.demo.ui.viewmodel.FormularioSesion
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Prueba de UI de la pantalla de login (sin red: usa el composable sin ViewModel)
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val regla = createComposeRule()

    private fun mostrar(f: FormularioSesion, onEntrar: () -> Unit = {}, onGoogle: () -> Unit = {}) {
        regla.setContent {
            FamiliasQueSumanTheme {
                LoginContenido(
                    f = f,
                    onCorreo = {}, onPassword = {}, onMostrarPassword = {},
                    onEntrar = onEntrar, onGoogle = onGoogle, onRegistro = {}, onAviso = {}
                )
            }
        }
    }

    @Test
    fun muestraLasDosFormasDeEntrar() {
        mostrar(FormularioSesion())
        regla.onNodeWithText("Bienvenido").assertIsDisplayed()
        regla.onNodeWithText("Iniciar sesión").assertIsDisplayed()
        regla.onNodeWithText("Continuar con Google").assertIsDisplayed()
    }

    @Test
    fun muestraElErrorDelServidor() {
        mostrar(FormularioSesion(error = "Correo o contraseña incorrectos"))
        regla.onNodeWithText("Correo o contraseña incorrectos").assertIsDisplayed()
    }

    @Test
    fun losBotonesDisparanSusAcciones() {
        var entrar = false
        var google = false
        mostrar(FormularioSesion(), onEntrar = { entrar = true }, onGoogle = { google = true })
        regla.onNodeWithText("Iniciar sesión").performClick()
        regla.onNodeWithText("Continuar con Google").performClick()
        assertTrue(entrar && google)
    }
}
