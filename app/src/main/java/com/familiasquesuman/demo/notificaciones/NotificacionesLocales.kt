package com.familiasquesuman.demo.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.familiasquesuman.demo.MainActivity
import com.familiasquesuman.demo.R
import com.familiasquesuman.demo.data.AlmacenLocal
import com.familiasquesuman.demo.data.AppContainer
import com.familiasquesuman.demo.model.horaCorta
import com.familiasquesuman.demo.ui.widget.ProximaActividadWidget
import java.time.LocalDate
import java.util.concurrent.TimeUnit

// Sin FCM: cada 15 min se consultan las notificaciones del servidor y se avisa de las nuevas.
// También recuerda las actividades del día siguiente.
object NotificacionesLocales {
    const val CANAL = "familias_avisos"
    const val EXTRA_ABRIR = "abrir"
    const val ABRIR_NOTIFICACIONES = "notificaciones"
    const val ABRIR_ACTIVIDADES = "actividades"

    fun programar(context: Context) {
        val trabajo = PeriodicWorkRequestBuilder<NotificacionesWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("avisos-locales", ExistingPeriodicWorkPolicy.KEEP, trabajo)
    }

    fun crearCanal(context: Context) {
        val canal = NotificationChannel(CANAL, "Avisos de Familias que Suman", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Confirmaciones, recordatorios y mensajes de las asociaciones"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun mostrar(context: Context, id: Int, titulo: String, texto: String, abrir: String) {
        if (!tienePermiso(context)) return
        crearCanal(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ABRIR, abrir)
        }
        val pendiente = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()
        runCatching { NotificationManagerCompat.from(context).notify(id, notificacion) }
    }
}

class NotificacionesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repo = AppContainer.repositorio
        if (repo.usuario.value == null) return Result.success()
        val almacen = AlmacenLocal(applicationContext)
        val guardadas = almacen.notificacionesAvisadas()
        // Primera corrida (o tras cerrar sesión): solo se registra lo que ya existe, sin mostrar nada
        val primeraVez = guardadas == null
        val avisadas = guardadas ?: emptySet()
        val nuevasAvisadas = mutableSetOf<String>()

        // 1. Notificaciones del servidor que no se han mostrado
        runCatching { repo.obtenerNotificaciones() }.onSuccess { lista ->
            lista.forEach { n ->
                if (!primeraVez && !n.leida && n.id !in avisadas) {
                    NotificacionesLocales.mostrar(applicationContext, n.id.hashCode(), n.titulo, n.cuerpo, NotificacionesLocales.ABRIR_NOTIFICACIONES)
                }
                nuevasAvisadas += n.id
            }
        }.onFailure { nuevasAvisadas += avisadas.filterNot { it.startsWith("recordatorio-") } }

        // 2. Recordatorio de las actividades de mañana (datos locales, sin red)
        val manana = LocalDate.now().plusDays(1).toString()
        repo.inscripciones.value.filter { it.fecha == manana && it.estado == "confirmada" }.forEach { i ->
            val clave = "recordatorio-${i.id}"
            if (!primeraVez && clave !in avisadas) {
                NotificacionesLocales.mostrar(
                    applicationContext, clave.hashCode(),
                    "Mañana: ${i.actividadTitulo}",
                    "${horaCorta(i.horaInicio)} en ${i.lugar}. Van: ${i.nombres.joinToString(", ")}",
                    NotificacionesLocales.ABRIR_ACTIVIDADES
                )
            }
            nuevasAvisadas += clave
        }

        almacen.guardarNotificacionesAvisadas(nuevasAvisadas)
        runCatching { ProximaActividadWidget().actualizar(applicationContext) }
        return Result.success()
    }
}
