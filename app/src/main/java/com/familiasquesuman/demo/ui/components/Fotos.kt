package com.familiasquesuman.demo.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.familiasquesuman.demo.data.Imagenes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectorFoto(val elegirDeGaleria: () -> Unit, val tomarFoto: () -> Unit)

// Photo Picker (sin permisos) o cámara vía FileProvider; entrega los bytes ya comprimidos
@Composable
fun rememberSelectorFoto(onFoto: (ByteArray) -> Unit, onError: (String) -> Unit = {}): SelectorFoto {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uriCamara = remember { mutableStateOf<Uri?>(null) }

    fun procesar(uri: Uri) {
        scope.launch {
            runCatching { withContext(Dispatchers.IO) { Imagenes.comprimir(context, uri) } }
                .onSuccess(onFoto)
                .onFailure { onError(it.message ?: "No se pudo leer la foto") }
        }
    }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) procesar(uri)
    }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val uri = uriCamara.value
        if (ok && uri != null) procesar(uri)
    }

    return remember {
        SelectorFoto(
            elegirDeGaleria = { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            tomarFoto = {
                val archivo = Imagenes.archivoTemporal(context)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                uriCamara.value = uri
                camara.launch(uri)
            }
        )
    }
}
