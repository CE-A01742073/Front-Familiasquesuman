package com.familiasquesuman.demo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.familiasquesuman.demo.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

// Abre el selector de cuentas de Google (Credential Manager) y entrega el ID token para /auth/google
@Composable
fun rememberGoogleSignIn(onToken: (String) -> Unit, onError: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return {
        scope.launch {
            try {
                val opcion = GetGoogleIdOption.Builder()
                    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(false)
                    .build()
                val peticion = GetCredentialRequest.Builder().addCredentialOption(opcion).build()
                val cred = CredentialManager.create(context).getCredential(context, peticion).credential
                if (cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    onToken(GoogleIdTokenCredential.createFrom(cred.data).idToken)
                } else {
                    onError("No se recibió una cuenta de Google válida")
                }
            } catch (e: GetCredentialCancellationException) {
                // la persona cerró el selector
            } catch (e: NoCredentialException) {
                onError("No hay cuentas de Google en este teléfono. Agrega una en Ajustes o usa correo y contraseña.")
            } catch (e: GetCredentialException) {
                onError("No se pudo iniciar sesión con Google. Intenta de nuevo.")
            }
        }
    }
}
