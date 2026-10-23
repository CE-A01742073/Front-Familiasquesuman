package com.familiasquesuman.demo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

object Imagenes {
    private const val LADO_MAX = 1600

    // Decodifica y reduce la imagen antes de subirla (el servidor la re-codifica y quita EXIF)
    fun comprimir(context: Context, uri: Uri): ByteArray {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
        var escala = 1
        while (maxOf(limites.outWidth, limites.outHeight) / escala > LADO_MAX) escala *= 2
        val opciones = BitmapFactory.Options().apply { inSampleSize = escala }
        val bitmap = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
            ?: throw IllegalArgumentException("No se pudo leer la imagen")
        val salida = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, salida)
        bitmap.recycle()
        return salida.toByteArray()
    }

    fun parte(nombreCampo: String, bytes: ByteArray): MultipartBody.Part =
        MultipartBody.Part.createFormData(nombreCampo, "foto.jpg", bytes.toRequestBody("image/jpeg".toMediaType()))

    fun archivoTemporal(context: Context): File {
        val dir = File(context.cacheDir, "fotos").apply { mkdirs() }
        return File(dir, "captura_${System.currentTimeMillis()}.jpg")
    }
}
