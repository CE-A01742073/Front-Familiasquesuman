package com.familiasquesuman.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familiasquesuman.demo.data.AppContainer
import com.familiasquesuman.demo.notificaciones.NotificacionesLocales
import com.familiasquesuman.demo.ui.AppNav
import com.familiasquesuman.demo.ui.theme.FamiliasQueSumanTheme

class MainActivity : ComponentActivity() {
    // Destino pedido por una notificación del sistema (al abrir o con la app ya abierta)
    private val abrir = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        abrir.value = intent?.getStringExtra(NotificacionesLocales.EXTRA_ABRIR)
        setContent {
            val modo by AppContainer.repositorio.modoTema.collectAsStateWithLifecycle()
            val destino by abrir
            FamiliasQueSumanTheme(modo) {
                AppNav(abrirAlIniciar = destino, onDestinoConsumido = { abrir.value = null })
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        abrir.value = intent.getStringExtra(NotificacionesLocales.EXTRA_ABRIR)
    }
}
