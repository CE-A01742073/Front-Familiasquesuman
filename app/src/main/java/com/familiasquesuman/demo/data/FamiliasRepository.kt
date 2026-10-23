package com.familiasquesuman.demo.data

import com.familiasquesuman.demo.data.local.CatalogoDao
import com.familiasquesuman.demo.data.local.CatalogoEntity
import com.familiasquesuman.demo.data.local.Tipo
import com.familiasquesuman.demo.data.remote.ActividadDto
import com.familiasquesuman.demo.data.remote.ApiClient
import com.familiasquesuman.demo.data.remote.ApiException
import com.familiasquesuman.demo.data.remote.CampanaDto
import com.familiasquesuman.demo.data.remote.CentroDto
import com.familiasquesuman.demo.data.remote.ComentarioIn
import com.familiasquesuman.demo.data.remote.DonacionIn
import com.familiasquesuman.demo.data.remote.EncuestaIn
import com.familiasquesuman.demo.data.remote.FamiliaPatch
import com.familiasquesuman.demo.data.remote.GoogleIn
import com.familiasquesuman.demo.data.remote.InscripcionIn
import com.familiasquesuman.demo.data.remote.IntegranteIn
import com.familiasquesuman.demo.data.remote.LoginIn
import com.familiasquesuman.demo.data.remote.PerfilPatch
import com.familiasquesuman.demo.data.remote.ProyectoDto
import com.familiasquesuman.demo.data.remote.RefreshIn
import com.familiasquesuman.demo.data.remote.RegistroIn
import com.familiasquesuman.demo.data.remote.SesionStore
import com.familiasquesuman.demo.data.remote.TokensDto
import com.familiasquesuman.demo.data.remote.jsonApi
import com.familiasquesuman.demo.data.remote.llamarApi
import com.familiasquesuman.demo.data.sync.OperacionPendiente
import com.familiasquesuman.demo.data.sync.Outbox
import com.familiasquesuman.demo.data.sync.TipoOperacion
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.CaminoSolidario
import com.familiasquesuman.demo.model.Campana
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.model.Ciudad
import com.familiasquesuman.demo.model.Donacion
import com.familiasquesuman.demo.model.EncuestaRespondida
import com.familiasquesuman.demo.model.Familia
import com.familiasquesuman.demo.model.Inscripcion
import com.familiasquesuman.demo.model.Integrante
import com.familiasquesuman.demo.model.ModoTema
import com.familiasquesuman.demo.model.Notificacion
import com.familiasquesuman.demo.model.Pregunta
import com.familiasquesuman.demo.model.Proyecto
import com.familiasquesuman.demo.model.RespuestaEncuesta
import com.familiasquesuman.demo.model.Testimonio
import com.familiasquesuman.demo.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import java.util.UUID

/* Reglas de negocio y acceso a datos de la familia. Catálogo: API (/sync) → Room → UI.
 * Escrituras sin red → Outbox (WorkManager) con Idempotency-Key. */

class FamiliasRepository(
    private val almacen: AlmacenLocal,
    private val sesion: SesionStore,
    private val cliente: ApiClient,
    private val db: CatalogoDao,
    private val outbox: Outbox,
    private val scope: CoroutineScope
) {
    private val api get() = cliente.api
    private val host = cliente.host

    private val _ciudad = MutableStateFlow(Ciudad.MONTERREY)
    val ciudad: StateFlow<Ciudad> = _ciudad.asStateFlow()

    private val _ciudadElegida = MutableStateFlow(false)
    val ciudadElegida: StateFlow<Boolean> = _ciudadElegida.asStateFlow()

    val usuario: StateFlow<Usuario?> = sesion.usuario
    val sesionIniciada: StateFlow<Boolean> =
        sesion.usuario.map { it != null }.stateIn(scope, SharingStarted.Eagerly, sesion.sesionActiva)

    private val _listo = MutableStateFlow(false)
    val listo: StateFlow<Boolean> = _listo.asStateFlow()

    private val _modoTema = MutableStateFlow(ModoTema.SISTEMA)
    val modoTema: StateFlow<ModoTema> = _modoTema.asStateFlow()

    private val _avisos = MutableStateFlow<List<String>>(emptyList())
    val avisos: StateFlow<List<String>> = _avisos.asStateFlow()

    private val _fotoPerfilLocal = MutableStateFlow<String?>(null)
    val fotoPerfilLocal: StateFlow<String?> = _fotoPerfilLocal.asStateFlow()

    private val _familia = MutableStateFlow(Familia())
    val familia: StateFlow<Familia> = _familia.asStateFlow()

    private val _inscripcionesServidor = MutableStateFlow<List<Inscripcion>>(emptyList())

    // Lo del servidor + lo que está en la cola: inscripciones "pendiente de confirmar",
    // cancelaciones aún no enviadas y encuestas respondidas sin red
    val inscripciones: StateFlow<List<Inscripcion>> =
        combine(_inscripcionesServidor, outbox.pendientes) { servidor, pendientes ->
            val canceladas = pendientes.filter { it.op.tipo == TipoOperacion.CANCELAR }.mapNotNull { it.op.inscripcionId }.toSet()
            val encuestas = pendientes.filter { it.op.tipo == TipoOperacion.ENCUESTA }
            val nuevas = pendientes.filter { it.op.tipo == TipoOperacion.INSCRIBIR }.map { p ->
                Inscripcion(
                    id = "pendiente-${p.clave}",
                    actividadId = p.op.actividadId ?: "",
                    actividadTitulo = p.op.actividadTitulo ?: "Actividad",
                    centroId = p.op.centroId ?: "",
                    centroNombre = p.op.centroNombre ?: "",
                    fecha = p.op.fecha ?: "",
                    horaInicio = p.op.horaInicio ?: "",
                    horaFin = p.op.horaFin,
                    lugar = p.op.lugar ?: "",
                    nombres = p.op.nombres,
                    comentario = p.op.comentario ?: "",
                    estado = "pendiente"
                )
            }
            nuevas + servidor.filter { it.id !in canceladas }.map { i ->
                i.copy(
                    encuestaAntes = i.encuestaAntes || encuestas.any { it.op.inscripcionId == i.id && it.op.momento == "antes" },
                    encuestaDespues = i.encuestaDespues || encuestas.any { it.op.inscripcionId == i.id && it.op.momento == "despues" }
                )
            }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val pendientesPorEnviar: StateFlow<Int> =
        outbox.pendientes.map { it.size }.stateIn(scope, SharingStarted.Eagerly, 0)

    private val _donaciones = MutableStateFlow<List<Donacion>>(emptyList())
    val donaciones: StateFlow<List<Donacion>> = _donaciones.asStateFlow()

    private val _misTestimonios = MutableStateFlow<List<Testimonio>>(emptyList())
    val misTestimonios: StateFlow<List<Testimonio>> = _misTestimonios.asStateFlow()

    private fun guardar(bloque: suspend () -> Unit) {
        scope.launch { bloque() }
    }

    init {
        scope.launch {
            val g = almacen.leer()
            _ciudad.value = g.ciudad
            _ciudadElegida.value = g.ciudadElegida
            _inscripcionesServidor.value = g.inscripciones
            _donaciones.value = g.donaciones
            _modoTema.value = g.modoTema
            _avisos.value = g.avisos
            _fotoPerfilLocal.value = g.fotoPerfilLocal
            g.familia?.let { _familia.value = it }
            _listo.value = true
            if (sesion.sesionActiva) {
                runCatching { cargarDatosDeFamilia() }
                outbox.programar()
            }
        }
    }

    // ---------- Preferencias ----------

    fun cambiarTema(modo: ModoTema) {
        _modoTema.value = modo
        guardar { almacen.guardarTema(modo) }
    }

    fun seleccionarCiudad(ciudad: Ciudad) {
        _ciudad.value = ciudad
        _ciudadElegida.value = true
        guardar {
            almacen.guardarCiudad(ciudad)
            if (sesion.sesionActiva && usuario.value?.esFamilia == true) {
                runCatching { api.editarFamilia(FamiliaPatch(ciudad = ciudad.clave)) }
            }
        }
    }

    suspend fun avisar(texto: String) {
        _avisos.update { (listOf(texto) + it).take(10) }
        almacen.guardarAvisos(_avisos.value)
    }

    fun descartarAvisos() {
        _avisos.value = emptyList()
        guardar { almacen.guardarAvisos(emptyList()) }
    }

    // ---------- Sesión ----------

    suspend fun iniciarSesion(correo: String, password: String) {
        val c = correo.trim().lowercase()
        require(c.contains("@")) { "Escribe un correo válido" }
        require(password.isNotEmpty()) { "Escribe tu contraseña" }
        alIniciar(llamarApi { api.login(LoginIn(c, password)) })
    }

    suspend fun registrar(nombre: String, nombreFamilia: String, correo: String, password: String, aceptaAviso: Boolean) {
        val n = nombre.trim()
        val nf = nombreFamilia.trim()
        val c = correo.trim().lowercase()
        require(n.length >= 2) { "Escribe tu nombre" }
        require(nf.length >= 2) { "Escribe el nombre de tu familia" }
        require(nf.length <= 40) { "El nombre de la familia es demasiado largo" }
        require(c.contains("@") && c.contains(".")) { "Escribe un correo válido" }
        require(password.length >= 12) { "La contraseña debe tener al menos 12 caracteres" }
        require(aceptaAviso) { "Debes aceptar el aviso de privacidad" }
        alIniciar(llamarApi { api.registro(RegistroIn(c, password, n, nf, _ciudad.value.clave, true)) })
    }

    // Google: el ID token se manda al servidor, que emite su propio JWT. La primera vez pide nombre de familia.
    suspend fun iniciarSesionGoogle(idToken: String, nombreFamilia: String?) {
        val nf = nombreFamilia?.trim()
        if (nf != null) {
            require(nf.length >= 2) { "Escribe el nombre de tu familia" }
            require(nf.length <= 40) { "El nombre de la familia es demasiado largo" }
        }
        alIniciar(llamarApi { api.google(GoogleIn(idToken, nf, _ciudad.value.clave, aceptaAvisoPrivacidad = true)) })
    }

    private suspend fun alIniciar(t: TokensDto) {
        sesion.guardarTokens(t.accessToken, t.refreshToken)
        sesion.guardarUsuario(t.usuario.aModelo(host))
        runCatching { cargarDatosDeFamilia() }
    }

    private suspend fun cargarDatosDeFamilia() {
        val u = sesion.usuario.value ?: return
        runCatching { llamarApi { api.perfil() }.aModelo(host) }.getOrNull()?.let { sesion.guardarUsuario(it) }
        if (!u.esFamilia) {
            _familia.value = Familia(nombre = u.nombre, correo = u.correo)
            return
        }
        val familia = runCatching { llamarApi { api.familia() }.aModelo(u.correo) }
            .getOrElse { Familia(nombre = u.nombre, correo = u.correo) }
        _familia.value = familia
        almacen.guardarFamilia(familia)
        runCatching { obtenerInscripciones() }
        runCatching { obtenerDonaciones() }
    }

    fun cerrarSesion() {
        val access = sesion.accessToken
        val refresh = sesion.refreshToken
        sesion.limpiar()
        limpiarDatosDeUsuario()
        guardar {
            if (access != null && refresh != null) runCatching { api.logout("Bearer $access", RefreshIn(refresh)) }
            almacen.borrarDatosDeUsuario()
            outbox.borrarTodo()
        }
    }

    // Borra la cuenta en el servidor (RF-15) y deja la app como recién instalada
    suspend fun cerrarCuenta() {
        llamarApi { api.eliminarCuenta() }
        sesion.limpiar()
        limpiarDatosDeUsuario()
        _ciudadElegida.value = false
        almacen.borrarTodo()
        db.borrarTodo()
        outbox.borrarTodo()
    }

    private fun limpiarDatosDeUsuario() {
        _inscripcionesServidor.value = emptyList()
        _donaciones.value = emptyList()
        _misTestimonios.value = emptyList()
        _avisos.value = emptyList()
        _fotoPerfilLocal.value = null
        _familia.value = Familia()
    }

    // ---------- Perfil (RF-18) ----------

    suspend fun actualizarPerfil(nombre: String, descripcion: String) {
        val n = nombre.trim()
        require(n.length >= 2) { "Escribe tu nombre" }
        require(descripcion.length <= 300) { "La descripción es demasiado larga" }
        val dto = llamarApi { api.editarPerfil(PerfilPatch(nombre = n, descripcion = descripcion.trim())) }
        sesion.guardarUsuario(dto.aModelo(host))
    }

    suspend fun subirFotoPerfil(bytes: ByteArray, rutaLocal: String) {
        val dto = llamarApi { api.subirFoto(Imagenes.parte("archivo", bytes)) }
        sesion.guardarUsuario(dto.aModelo(host))
        _fotoPerfilLocal.value = rutaLocal
        almacen.guardarFotoPerfilLocal(rutaLocal)
    }

    suspend fun obtenerInsignias(): CaminoSolidario = llamarApi { api.insignias() }.aModelo()

    // ---------- Catálogo (API → Room) ----------

    private val sincronizando = Mutex()
    private val ultimaSync = mutableMapOf<Ciudad, Long>()

    suspend fun sincronizar(ciudad: Ciudad, forzar: Boolean = false): Unit = sincronizando.withLock {
        val ahora = System.currentTimeMillis()
        if (!forzar && ahora - (ultimaSync[ciudad] ?: 0L) < 60_000) return@withLock
        try {
            val s = llamarApi { api.sync(ciudad.clave, almacen.marcaSync(ciudad)) }
            val filas = s.centros.map {
                CatalogoEntity(Tipo.CENTRO, it.id, ciudad.clave, it.activo && it.verificado, it.nombre, jsonApi.encodeToString(it))
            } + s.actividades.map {
                CatalogoEntity(Tipo.ACTIVIDAD, it.id, ciudad.clave, it.estado == "activa", it.fecha + it.horaInicio, jsonApi.encodeToString(it))
            } + s.campanas.map {
                CatalogoEntity(Tipo.CAMPANA, it.id, ciudad.clave, it.activa, (it.hasta ?: "9999") + it.titulo, jsonApi.encodeToString(it))
            } + s.proyectos.map {
                CatalogoEntity(Tipo.PROYECTO, it.id, null, it.activo, it.creadoEn, jsonApi.encodeToString(it))
            }
            if (filas.isNotEmpty()) db.guardar(filas)
            almacen.guardarMarcaSync(ciudad, marcaIso(s.marca))
            ultimaSync[ciudad] = ahora
        } catch (e: ApiException) {
            val hayCache = db.contar(Tipo.CENTRO, ciudad.clave) + db.contar(Tipo.ACTIVIDAD, ciudad.clave) > 0
            if (!hayCache) throw e
        }
        Unit
    }

    private inline fun <reified T> CatalogoEntity.decodificar(): T = jsonApi.decodeFromString<T>(json)

    private suspend fun guardarEnCache(tipo: String, id: String, activo: Boolean, orden: String, json: String) {
        val ciudad = if (tipo == Tipo.PROYECTO) null else _ciudad.value.clave
        db.guardar(listOf(CatalogoEntity(tipo, id, ciudad, activo, orden, json)))
    }

    suspend fun obtenerActividades(ciudad: Ciudad): List<Actividad> {
        sincronizar(ciudad)
        val hoy = LocalDate.now().toString()
        return db.listar(Tipo.ACTIVIDAD, ciudad.clave)
            .map { it.decodificar<ActividadDto>().aModelo(host) }
            .filter { it.activa && it.fecha >= hoy }
    }

    suspend fun obtenerActividad(id: String): Actividad {
        try {
            val dto = llamarApi { api.actividad(id) }
            guardarEnCache(Tipo.ACTIVIDAD, dto.id, dto.estado == "activa", dto.fecha + dto.horaInicio, jsonApi.encodeToString(dto))
            return dto.aModelo(host)
        } catch (e: ApiException) {
            if (e.status != 0) throw e
            return db.buscar(Tipo.ACTIVIDAD, id)?.decodificar<ActividadDto>()?.aModelo(host) ?: throw e
        }
    }

    suspend fun obtenerActividadesDeCentro(centroId: String): List<Actividad> {
        runCatching { sincronizar(_ciudad.value) }
        val hoy = LocalDate.now().toString()
        return db.listar(Tipo.ACTIVIDAD, _ciudad.value.clave)
            .map { it.decodificar<ActividadDto>().aModelo(host) }
            .filter { it.centroId == centroId && it.activa && it.fecha >= hoy }
    }

    suspend fun obtenerCentros(ciudad: Ciudad): List<Centro> {
        sincronizar(ciudad)
        return db.listar(Tipo.CENTRO, ciudad.clave).map { it.decodificar<CentroDto>().aModelo(host) }
    }

    suspend fun obtenerCentro(id: String): Centro {
        db.buscar(Tipo.CENTRO, id)?.let { return it.decodificar<CentroDto>().aModelo(host) }
        val dto = llamarApi { api.centro(id) }
        guardarEnCache(Tipo.CENTRO, dto.id, dto.activo && dto.verificado, dto.nombre, jsonApi.encodeToString(dto))
        return dto.aModelo(host)
    }

    suspend fun obtenerCampanas(ciudad: Ciudad): List<Campana> {
        sincronizar(ciudad)
        val apoyadas = _donaciones.value.mapNotNull { it.campanaId }.toSet()
        return db.listar(Tipo.CAMPANA, ciudad.clave)
            .map { it.decodificar<CampanaDto>().aModelo(host) }
            .map { it.copy(yaApoye = it.yaApoye || it.id in apoyadas) }
    }

    // RF-19: ordenados por fecha de creación, los más recientes primero
    suspend fun obtenerProyectos(): List<Proyecto> {
        sincronizar(_ciudad.value)
        return db.listar(Tipo.PROYECTO, _ciudad.value.clave)
            .map { it.decodificar<ProyectoDto>().aModelo(host) }
            .sortedByDescending { it.creadoEn }
    }

    suspend fun obtenerProyecto(id: String): Proyecto {
        try {
            val dto = llamarApi { api.proyecto(id) }
            guardarEnCache(Tipo.PROYECTO, dto.id, dto.activo, dto.creadoEn, jsonApi.encodeToString(dto))
            return dto.aModelo(host)
        } catch (e: ApiException) {
            if (e.status != 0) throw e
            return db.buscar(Tipo.PROYECTO, id)?.decodificar<ProyectoDto>()?.aModelo(host) ?: throw e
        }
    }

    // El servidor solo sabe "marcar todas"; leída/no leída por notificación se lleva en el teléfono
    suspend fun obtenerNotificaciones(): List<Notificacion> {
        val leidasLocal = almacen.notificacionesLeidas()
        val noLeidasLocal = almacen.notificacionesNoLeidas()
        val lista = llamarApi { api.notificaciones() }.map { it.aModelo() }
            .map { n -> n.copy(leida = (n.leida || n.id in leidasLocal) && n.id !in noLeidasLocal) }
        // Limpieza: solo se conservan ids que siguen existiendo
        val ids = lista.map { it.id }.toSet()
        if (!ids.containsAll(leidasLocal)) almacen.guardarNotificacionesLeidas(leidasLocal.intersect(ids))
        if (!ids.containsAll(noLeidasLocal)) almacen.guardarNotificacionesNoLeidas(noLeidasLocal.intersect(ids))
        return lista
    }

    // Deslizar: alterna leída / no leída. Cuando ya no queda ninguna sin leer se avisa al servidor.
    suspend fun marcarNotificacion(id: String, leida: Boolean, quedanSinLeer: Boolean) {
        if (leida) {
            almacen.guardarNotificacionesLeidas(almacen.notificacionesLeidas() + id)
            almacen.guardarNotificacionesNoLeidas(almacen.notificacionesNoLeidas() - id)
            if (!quedanSinLeer) runCatching { llamarApi { api.marcarLeidas() } }
        } else {
            almacen.guardarNotificacionesLeidas(almacen.notificacionesLeidas() - id)
            almacen.guardarNotificacionesNoLeidas(almacen.notificacionesNoLeidas() + id)
        }
    }

    suspend fun marcarTodasLeidas() {
        runCatching { llamarApi { api.marcarLeidas() } }
        almacen.guardarNotificacionesLeidas(emptySet())
        almacen.guardarNotificacionesNoLeidas(emptySet())
    }

    // ---------- Inscripciones ----------

    suspend fun obtenerInscripciones(): List<Inscripcion> {
        try {
            val lista = llamarApi { api.misInscripciones() }.map { it.aModelo() }
            _inscripcionesServidor.value = lista
            almacen.guardarInscripciones(lista)
            return inscripciones.value
        } catch (e: ApiException) {
            if (e.status == 0 && inscripciones.value.isNotEmpty()) return inscripciones.value
            throw e
        }
    }

    fun yaInscrito(actividadId: String): Boolean =
        inscripciones.value.any { it.actividadId == actividadId && !it.pasada && (it.estado == "confirmada" || it.pendiente) }

    fun actividadRealizada(actividadId: String): Boolean =
        inscripciones.value.any { it.actividadId == actividadId && it.pasada && it.estado != "cancelada" }

    fun integrantesElegibles(actividad: Actividad): List<Integrante> =
        _familia.value.integrantes.filter { it.edad >= actividad.edadMinima }

    private fun claveIdempotencia(): String = UUID.randomUUID().toString().replace("-", "")

    private fun esSinRed(e: ApiException) = e.status == 0

    // RF-05/06: el servidor decide el cupo de forma atómica. Sin red → queda "pendiente de confirmar".
    suspend fun inscribir(actividad: Actividad, integrantesIds: List<String>, comentario: String): Inscripcion {
        check(!yaInscrito(actividad.id)) { "Tu familia ya está inscrita en esta actividad" }
        require(integrantesIds.isNotEmpty()) { "Selecciona al menos un integrante" }
        val nombres = _familia.value.integrantes.filter { it.id in integrantesIds }.map { it.nombre }
        val cuerpo = InscripcionIn(integrantesIds, comentario.trim().ifBlank { null })
        try {
            val dto = llamarApi { api.inscribirse(actividad.id, claveIdempotencia(), cuerpo) }
            val nueva = dto.aModelo()
            _inscripcionesServidor.update { listOf(nueva) + it.filter { x -> x.id != nueva.id } }
            almacen.guardarInscripciones(_inscripcionesServidor.value)
            return nueva
        } catch (e: ApiException) {
            if (!esSinRed(e)) throw e
            val clave = outbox.encolar(
                OperacionPendiente(
                    tipo = TipoOperacion.INSCRIBIR, actividadId = actividad.id, integrantesIds = integrantesIds,
                    comentario = cuerpo.comentario, actividadTitulo = actividad.titulo, centroId = actividad.centroId,
                    centroNombre = actividad.centroNombre, fecha = actividad.fecha, horaInicio = actividad.horaInicio,
                    horaFin = actividad.horaFin, lugar = actividad.lugar, nombres = nombres
                ),
                "Inscripción a \"${actividad.titulo}\""
            )
            return Inscripcion(
                id = "pendiente-$clave", actividadId = actividad.id, actividadTitulo = actividad.titulo,
                centroId = actividad.centroId, centroNombre = actividad.centroNombre, fecha = actividad.fecha,
                horaInicio = actividad.horaInicio, horaFin = actividad.horaFin, lugar = actividad.lugar,
                nombres = nombres, comentario = comentario.trim(), estado = "pendiente"
            )
        }
    }

    suspend fun cancelarInscripcion(id: String) {
        if (id.startsWith("pendiente-")) {
            outbox.borrar(id.removePrefix("pendiente-"))
            return
        }
        try {
            llamarApi { api.cancelarInscripcion(id) }
            _inscripcionesServidor.update { lista -> lista.filter { it.id != id } }
            almacen.guardarInscripciones(_inscripcionesServidor.value)
        } catch (e: ApiException) {
            if (!esSinRed(e)) throw e
            outbox.encolar(OperacionPendiente(tipo = TipoOperacion.CANCELAR, inscripcionId = id), "Cancelación de inscripción")
        }
    }

    // ---------- Encuestas (RF-13) ----------

    suspend fun obtenerPreguntas(momento: String): List<Pregunta> =
        llamarApi { api.preguntas(momento) }.preguntas.map { Pregunta(it.clave, it.pregunta, it.tipo) }

    suspend fun obtenerEncuestasRespondidas(inscripcionId: String): List<EncuestaRespondida> {
        val encuestas = llamarApi { api.encuestasRespondidas(inscripcionId) }
        if (encuestas.isEmpty()) return emptyList()
        val preguntas = encuestas.map { it.momento }.distinct()
            .associateWith { m -> runCatching { obtenerPreguntas(m) }.getOrDefault(emptyList()) }
        return encuestas.sortedBy { it.momento != "antes" }.map { e ->
            val definidas = preguntas[e.momento].orEmpty()
            EncuestaRespondida(
                momento = e.momento,
                fecha = e.creadoEn,
                respuestas = e.respuestas.map { (clave, valor) ->
                    val p = definidas.firstOrNull { it.clave == clave }
                    RespuestaEncuesta(p?.pregunta ?: clave, respuestaTexto(valor, p?.tipo))
                }
            )
        }
    }

    suspend fun responderEncuesta(inscripcionId: String, momento: String, respuestas: Map<String, JsonElement>) {
        require(respuestas.isNotEmpty()) { "Responde al menos una pregunta" }
        try {
            llamarApi { api.responderEncuesta(inscripcionId, claveIdempotencia(), EncuestaIn(momento, respuestas)) }
            _inscripcionesServidor.update { lista ->
                lista.map {
                    if (it.id != inscripcionId) it
                    else if (momento == "antes") it.copy(encuestaAntes = true) else it.copy(encuestaDespues = true)
                }
            }
        } catch (e: ApiException) {
            if (!esSinRed(e)) throw e
            outbox.encolar(
                OperacionPendiente(tipo = TipoOperacion.ENCUESTA, inscripcionId = inscripcionId, momento = momento, respuestas = respuestas),
                "Encuesta ${if (momento == "antes") "antes" else "después"} de la actividad"
            )
        }
    }

    // ---------- Donaciones (RF-10, sin pagos) ----------

    suspend fun obtenerDonaciones(): List<Donacion> {
        val lista = llamarApi { api.misDonaciones() }.map { it.aModelo() }
        _donaciones.value = lista
        almacen.guardarDonaciones(lista)
        return lista
    }

    suspend fun ayudarCampana(campana: Campana) {
        check(_donaciones.value.none { it.campanaId == campana.id }) { "Ya registraste tu apoyo a esta campaña" }
        val cuerpo = DonacionIn(campanaId = campana.id, tipo = "Campaña", detalle = campana.titulo.take(200))
        try {
            val dto = llamarApi { api.registrarDonacion(claveIdempotencia(), cuerpo) }
            _donaciones.update { listOf(dto.aModelo()) + it }
        } catch (e: ApiException) {
            if (!esSinRed(e)) throw e
            outbox.encolar(
                OperacionPendiente(tipo = TipoOperacion.DONAR, campanaId = campana.id, detalle = cuerpo.detalle, centroId = campana.centroId),
                "Apoyo a la campaña \"${campana.titulo}\""
            )
            _donaciones.update { listOf(Donacion("pendiente-${campana.id}", "Campaña", cuerpo.detalle, campana.centroId, campana.id)) + it }
        }
        almacen.guardarDonaciones(_donaciones.value)
    }

    // ---------- Testimonios (RF-12) ----------

    suspend fun obtenerMisTestimonios(): List<Testimonio> {
        val lista = llamarApi { api.misTestimonios() }.map { it.aModelo(host) }
        _misTestimonios.value = lista
        return lista
    }

    suspend fun obtenerTestimoniosPublicos(actividadId: String? = null): List<Testimonio> =
        llamarApi { api.testimonios(actividadId) }.items.map { it.aModelo(host) }

    suspend fun crearTestimonio(actividadId: String, texto: String, foto: ByteArray?): Testimonio {
        val t = texto.trim()
        require(t.length >= 10) { "Cuéntanos un poco más (mínimo 10 caracteres)" }
        require(t.length <= 600) { "El testimonio es demasiado largo" }
        val tipoTexto = "text/plain".toMediaType()
        val dto = llamarApi {
            api.crearTestimonio(actividadId.toRequestBody(tipoTexto), t.toRequestBody(tipoTexto), foto?.let { Imagenes.parte("foto", it) })
        }
        val nuevo = dto.aModelo(host)
        _misTestimonios.update { listOf(nuevo) + it }
        return nuevo
    }

    suspend fun borrarTestimonio(id: String) {
        llamarApi { api.borrarTestimonio(id) }
        _misTestimonios.update { lista -> lista.filter { it.id != id } }
    }

    // ---------- Familia (RF-11, RF-18) ----------

    suspend fun actualizarFamilia(nombre: String, zona: String) {
        val n = nombre.trim()
        val z = zona.trim()
        require(n.isNotBlank()) { "El nombre de la familia no puede estar vacío" }
        require(n.length <= 40) { "El nombre de la familia es demasiado largo" }
        val dto = llamarApi { api.editarFamilia(FamiliaPatch(nombre = n, zona = z)) }
        actualizarFamiliaLocal { dto.aModelo(it.correo) }
    }

    private suspend fun actualizarFamiliaLocal(cambio: (Familia) -> Familia) {
        _familia.update(cambio)
        almacen.guardarFamilia(_familia.value)
    }

    private fun validarIntegrante(nombre: String, edad: Int?) {
        require(nombre.isNotBlank()) { "Escribe el nombre del integrante" }
        require(nombre.length <= 30) { "El nombre es demasiado largo" }
        require(edad != null) { "Escribe una edad válida" }
        require(edad in 0..120) { "La edad debe estar entre 0 y 120" }
    }

    suspend fun agregarIntegrante(nombre: String, edad: Int?) {
        val n = nombre.trim()
        validarIntegrante(n, edad)
        require(_familia.value.integrantes.size < 12) { "Una familia puede tener máximo 12 integrantes" }
        require(_familia.value.integrantes.none { it.nombre.equals(n, ignoreCase = true) }) { "Ya hay un integrante con ese nombre" }
        val dto = llamarApi { api.agregarIntegrante(IntegranteIn(n, edad!!)) }
        actualizarFamiliaLocal { f -> f.copy(integrantes = f.integrantes + Integrante(dto.id, dto.nombre, dto.edad)) }
    }

    suspend fun editarIntegrante(id: String, nombre: String, edad: Int?) {
        val n = nombre.trim()
        validarIntegrante(n, edad)
        require(_familia.value.integrantes.none { it.id != id && it.nombre.equals(n, ignoreCase = true) }) { "Ya hay un integrante con ese nombre" }
        val dto = llamarApi { api.editarIntegrante(id, IntegranteIn(n, edad!!)) }
        actualizarFamiliaLocal { f ->
            f.copy(integrantes = f.integrantes.map { if (it.id == id) Integrante(dto.id, dto.nombre, dto.edad) else it })
        }
    }

    suspend fun eliminarIntegrante(id: String) {
        val integrante = _familia.value.integrantes.firstOrNull { it.id == id } ?: return
        check(_familia.value.integrantes.size > 1) { "La familia debe tener al menos un integrante" }
        check(inscripciones.value.none { !it.pasada && integrante.nombre in it.nombres }) {
            "${integrante.nombre} está inscrito en una actividad próxima. Cancela esa inscripción primero."
        }
        llamarApi { api.eliminarIntegrante(id) }
        actualizarFamiliaLocal { f -> f.copy(integrantes = f.integrantes.filter { it.id != id }) }
    }

    // ---------- Cola offline ----------

    // Lo llama SyncWorker con la clave de la cola como Idempotency-Key: reintentar nunca duplica
    suspend fun ejecutarPendiente(clave: String, op: OperacionPendiente) {
        when (op.tipo) {
            TipoOperacion.INSCRIBIR -> llamarApi {
                api.inscribirse(op.actividadId ?: "", clave, InscripcionIn(op.integrantesIds, op.comentario))
            }
            TipoOperacion.CANCELAR -> llamarApi { api.cancelarInscripcion(op.inscripcionId ?: "") }
            TipoOperacion.DONAR -> llamarApi {
                api.registrarDonacion(clave, DonacionIn(campanaId = op.campanaId, tipo = "Campaña", detalle = op.detalle ?: "Campaña"))
            }
            TipoOperacion.ENCUESTA -> llamarApi {
                api.responderEncuesta(op.inscripcionId ?: "", clave, EncuestaIn(op.momento ?: "antes", op.respuestas ?: emptyMap()))
            }
            TipoOperacion.LIKE -> llamarApi {
                if (op.dar) api.darLike(op.objetivoTipo ?: "", op.objetivoId ?: "")
                else api.quitarLike(op.objetivoTipo ?: "", op.objetivoId ?: "")
            }
            TipoOperacion.COMENTAR -> llamarApi { api.comentar(op.objetivoTipo ?: "", op.objetivoId ?: "", ComentarioIn(op.texto ?: "")) }
        }
    }

    suspend fun refrescarTrasSync() {
        if (!sesion.sesionActiva || usuario.value?.esFamilia != true) return
        runCatching { obtenerInscripciones() }
        runCatching { obtenerDonaciones() }
    }

    suspend fun encolar(op: OperacionPendiente, descripcion: String) = outbox.encolar(op, descripcion)

    fun escala(valor: Int): JsonElement = JsonPrimitive(valor)
    fun texto(valor: String): JsonElement = JsonPrimitive(valor)
}
