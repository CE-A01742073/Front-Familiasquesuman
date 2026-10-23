package com.familiasquesuman.demo.data.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.familiasquesuman.demo.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Tokens y usuario en EncryptedSharedPreferences (llave en Android Keystore)
class SesionStore(context: Context) {

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "sesion_segura",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val json = Json { ignoreUnknownKeys = true }

    private val _usuario = MutableStateFlow(leerUsuario())
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    val sesionActiva: Boolean get() = accessToken != null && refreshToken != null

    @get:Synchronized
    val accessToken: String? get() = prefs.getString(ACCESS, null)

    @get:Synchronized
    val refreshToken: String? get() = prefs.getString(REFRESH, null)

    @Synchronized
    fun guardarTokens(access: String, refresh: String) {
        prefs.edit().putString(ACCESS, access).putString(REFRESH, refresh).apply()
    }

    fun guardarUsuario(usuario: Usuario) {
        prefs.edit().putString(USUARIO, json.encodeToString(usuario)).apply()
        _usuario.value = usuario
    }

    @Synchronized
    fun limpiar() {
        prefs.edit().clear().apply()
        _usuario.value = null
    }

    private fun leerUsuario(): Usuario? =
        prefs.getString(USUARIO, null)?.let { runCatching { json.decodeFromString<Usuario>(it) }.getOrNull() }

    private companion object {
        const val ACCESS = "access_token"
        const val REFRESH = "refresh_token"
        const val USUARIO = "usuario"
    }
}
