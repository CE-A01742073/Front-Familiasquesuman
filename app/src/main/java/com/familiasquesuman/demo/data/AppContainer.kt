package com.familiasquesuman.demo.data

import android.content.Context
import com.familiasquesuman.demo.data.local.CatalogoDb
import com.familiasquesuman.demo.data.remote.ApiClient
import com.familiasquesuman.demo.data.remote.SesionStore
import com.familiasquesuman.demo.data.sync.Outbox
import com.familiasquesuman.demo.notificaciones.NotificacionesLocales
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object AppContainer {
    lateinit var repositorio: FamiliasRepository
        private set
    lateinit var social: SocialRepository
        private set
    lateinit var gestion: GestionRepository
        private set
    lateinit var apiClient: ApiClient
        private set
    lateinit var outbox: Outbox
        private set

    fun inicializar(context: Context) {
        val app = context.applicationContext
        val sesion = SesionStore(app)
        apiClient = ApiClient(sesion)
        val db = CatalogoDb.crear(app)
        outbox = Outbox(db.pendientes(), app)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        repositorio = FamiliasRepository(
            almacen = AlmacenLocal(app),
            sesion = sesion,
            cliente = apiClient,
            db = db.catalogo(),
            outbox = outbox,
            scope = scope
        )
        social = SocialRepository(apiClient, outbox)
        gestion = GestionRepository(apiClient)
        NotificacionesLocales.programar(app)
    }
}
