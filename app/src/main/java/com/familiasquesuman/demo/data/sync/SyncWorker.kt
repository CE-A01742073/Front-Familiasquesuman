package com.familiasquesuman.demo.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.familiasquesuman.demo.data.AppContainer
import com.familiasquesuman.demo.data.remote.ApiException
import com.familiasquesuman.demo.data.remote.jsonApi

// Envía la cola en orden. Red caída o 5xx → reintenta; 4xx → se descarta y se avisa a la familia.
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repo = AppContainer.repositorio
        val outbox = AppContainer.outbox
        var reintentar = false
        for (p in outbox.todos()) {
            val op = runCatching { jsonApi.decodeFromString<OperacionPendiente>(p.payload) }.getOrNull()
            if (op == null) {
                outbox.borrar(p.clave)
                continue
            }
            try {
                repo.ejecutarPendiente(p.clave, op)
                outbox.borrar(p.clave)
            } catch (e: ApiException) {
                if (e.status == 0 || e.status >= 500) {
                    reintentar = true
                    outbox.registrarIntento(p.clave)
                    if (p.intentos >= 8) {
                        outbox.borrar(p.clave)
                        repo.avisar("No se pudo enviar: ${p.descripcion}")
                    }
                    break
                } else {
                    outbox.borrar(p.clave)
                    repo.avisar("${p.descripcion}: ${e.message}")
                }
            } catch (e: Exception) {
                outbox.borrar(p.clave)
            }
        }
        repo.refrescarTrasSync()
        return if (reintentar) Result.retry() else Result.success()
    }
}
