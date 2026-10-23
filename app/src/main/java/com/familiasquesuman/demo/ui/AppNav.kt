package com.familiasquesuman.demo.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.platform.LocalContext
import com.familiasquesuman.demo.notificaciones.NotificacionesLocales
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.familiasquesuman.demo.data.AppContainer
import com.familiasquesuman.demo.model.Rol
import com.familiasquesuman.demo.model.TipoObjetivo
import com.familiasquesuman.demo.ui.screens.ActividadesScreen
import com.familiasquesuman.demo.ui.screens.AsistentesScreen
import com.familiasquesuman.demo.ui.screens.AvisoPrivacidadScreen
import com.familiasquesuman.demo.ui.screens.ChatScreen
import com.familiasquesuman.demo.ui.screens.CiudadScreen
import com.familiasquesuman.demo.ui.screens.ConfirmacionScreen
import com.familiasquesuman.demo.ui.screens.ConversacionesScreen
import com.familiasquesuman.demo.ui.screens.DetalleActividadScreen
import com.familiasquesuman.demo.ui.screens.DetalleCentroScreen
import com.familiasquesuman.demo.ui.screens.DetalleProyectoScreen
import com.familiasquesuman.demo.ui.screens.DirectorioScreen
import com.familiasquesuman.demo.ui.screens.DonarScreen
import com.familiasquesuman.demo.ui.screens.EncuestaScreen
import com.familiasquesuman.demo.ui.screens.EncuestasRespondidasScreen
import com.familiasquesuman.demo.ui.screens.FormActividadScreen
import com.familiasquesuman.demo.ui.screens.FormCampanaScreen
import com.familiasquesuman.demo.ui.screens.FormProyectoScreen
import com.familiasquesuman.demo.ui.screens.GaleriaScreen
import com.familiasquesuman.demo.ui.screens.GestionAcciones
import com.familiasquesuman.demo.ui.screens.GestionScreen
import com.familiasquesuman.demo.ui.screens.HomeAcciones
import com.familiasquesuman.demo.ui.screens.HomeScreen
import com.familiasquesuman.demo.ui.screens.InscripcionScreen
import com.familiasquesuman.demo.ui.screens.InsigniasScreen
import com.familiasquesuman.demo.ui.screens.LoginScreen
import com.familiasquesuman.demo.ui.screens.MisInscripcionesScreen
import com.familiasquesuman.demo.ui.screens.ModeracionScreen
import com.familiasquesuman.demo.ui.screens.NotificacionesScreen
import com.familiasquesuman.demo.ui.screens.PerfilScreen
import com.familiasquesuman.demo.ui.screens.ProyectosScreen
import com.familiasquesuman.demo.ui.screens.RegistroScreen
import com.familiasquesuman.demo.ui.screens.SplashScreen
import com.familiasquesuman.demo.ui.viewmodel.ActividadesViewModel
import com.familiasquesuman.demo.ui.viewmodel.AsistentesViewModel
import com.familiasquesuman.demo.ui.viewmodel.AvisoPrivacidadViewModel
import com.familiasquesuman.demo.ui.viewmodel.ChatViewModel
import com.familiasquesuman.demo.ui.viewmodel.ConfirmacionViewModel
import com.familiasquesuman.demo.ui.viewmodel.ConversacionesViewModel
import com.familiasquesuman.demo.ui.viewmodel.DestinoInicial
import com.familiasquesuman.demo.ui.viewmodel.DetalleActividadViewModel
import com.familiasquesuman.demo.ui.viewmodel.DetalleCentroViewModel
import com.familiasquesuman.demo.ui.viewmodel.DetalleProyectoViewModel
import com.familiasquesuman.demo.ui.viewmodel.DirectorioViewModel
import com.familiasquesuman.demo.ui.viewmodel.DonarViewModel
import com.familiasquesuman.demo.ui.viewmodel.EncuestaViewModel
import com.familiasquesuman.demo.ui.viewmodel.EncuestasRespondidasViewModel
import com.familiasquesuman.demo.ui.viewmodel.FormActividadViewModel
import com.familiasquesuman.demo.ui.viewmodel.FormCampanaViewModel
import com.familiasquesuman.demo.ui.viewmodel.FormProyectoViewModel
import com.familiasquesuman.demo.ui.viewmodel.GaleriaViewModel
import com.familiasquesuman.demo.ui.viewmodel.GestionViewModel
import com.familiasquesuman.demo.ui.viewmodel.HomeViewModel
import com.familiasquesuman.demo.ui.viewmodel.InscripcionViewModel
import com.familiasquesuman.demo.ui.viewmodel.InsigniasViewModel
import com.familiasquesuman.demo.ui.viewmodel.MisInscripcionesViewModel
import com.familiasquesuman.demo.ui.viewmodel.ModeracionViewModel
import com.familiasquesuman.demo.ui.viewmodel.NotificacionesViewModel
import com.familiasquesuman.demo.ui.viewmodel.PerfilViewModel
import com.familiasquesuman.demo.ui.viewmodel.ProyectosViewModel
import com.familiasquesuman.demo.ui.viewmodel.SesionViewModel
import com.familiasquesuman.demo.ui.viewmodel.SocialViewModel
import com.familiasquesuman.demo.ui.viewmodel.SplashViewModel
import com.familiasquesuman.demo.ui.viewmodel.fabrica

object Rutas {
    const val SPLASH = "splash"
    const val CIUDAD = "ciudad"
    const val CAMBIAR_CIUDAD = "cambiar_ciudad"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME = "home"
    const val ACTIVIDADES = "actividades"
    const val DETALLE_ACTIVIDAD = "actividad/{id}"
    const val INSCRIPCION = "inscripcion/{id}"
    const val CONFIRMACION = "confirmacion/{id}"
    const val MIS_INSCRIPCIONES = "mis_inscripciones"
    const val ENCUESTA = "encuesta/{id}/{momento}"
    const val ENCUESTAS_RESPONDIDAS = "encuestas/{id}"
    const val DIRECTORIO = "directorio"
    const val DETALLE_CENTRO = "centro/{id}"
    const val DONAR = "donar"
    const val PROYECTOS = "proyectos"
    const val DETALLE_PROYECTO = "proyecto/{id}"
    const val PERFIL = "perfil"
    const val GALERIA = "galeria"
    const val INSIGNIAS = "insignias"
    const val NOTIFICACIONES = "notificaciones"
    const val AVISO_PRIVACIDAD = "aviso_privacidad"
    const val MENSAJES = "mensajes"
    const val CHAT = "chat?conversacion={conversacion}&centro={centro}&titulo={titulo}"
    const val GESTION = "gestion"
    const val FORM_ACTIVIDAD = "gestion/actividad?centro={centro}&id={id}"
    const val ASISTENTES = "gestion/asistentes/{id}"
    const val FORM_CAMPANA = "gestion/campana?centro={centro}&id={id}"
    const val FORM_PROYECTO = "gestion/proyecto?centro={centro}&id={id}"
    const val MODERACION = "moderacion"

    fun actividad(id: String) = "actividad/$id"
    fun inscripcion(id: String) = "inscripcion/$id"
    fun confirmacion(id: String) = "confirmacion/$id"
    fun encuesta(id: String, momento: String) = "encuesta/$id/$momento"
    fun encuestasRespondidas(id: String) = "encuestas/$id"
    fun centro(id: String) = "centro/$id"
    fun proyecto(id: String) = "proyecto/$id"
    fun chatConversacion(id: String, titulo: String) = "chat?conversacion=$id&titulo=${Uri.encode(titulo)}"
    fun chatCentro(centroId: String, titulo: String) = "chat?centro=$centroId&titulo=${Uri.encode(titulo)}"
    fun formActividad(centroId: String, id: String? = null) = "gestion/actividad?centro=$centroId" + (id?.let { "&id=$it" } ?: "")
    fun asistentes(id: String) = "gestion/asistentes/$id"
    fun formCampana(centroId: String, id: String? = null) = "gestion/campana?centro=$centroId" + (id?.let { "&id=$it" } ?: "")
    fun formProyecto(centroId: String, id: String? = null) = "gestion/proyecto?centro=$centroId" + (id?.let { "&id=$it" } ?: "")
}

private data class Tab(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val tabsFamilia = listOf(
    Tab(Rutas.HOME, "Inicio", Icons.Default.Home),
    Tab(Rutas.MIS_INSCRIPCIONES, "Actividades", Icons.Default.EventAvailable),
    Tab(Rutas.DONAR, "Donar", Icons.Default.Favorite),
    Tab(Rutas.MENSAJES, "Mensajes", Icons.Default.Mail),
    Tab(Rutas.PERFIL, "Perfil", Icons.Default.Person),
)
private val tabsAsociacion = listOf(
    Tab(Rutas.HOME, "Inicio", Icons.Default.Home),
    Tab(Rutas.GESTION, "Gestión", Icons.Default.Dashboard),
    Tab(Rutas.MENSAJES, "Mensajes", Icons.Default.Mail),
    Tab(Rutas.PERFIL, "Perfil", Icons.Default.Person),
)
private val tabsAdmin = listOf(
    Tab(Rutas.HOME, "Inicio", Icons.Default.Home),
    Tab(Rutas.GESTION, "Gestión", Icons.Default.Dashboard),
    Tab(Rutas.MODERACION, "Moderación", Icons.Default.Flag),
    Tab(Rutas.PERFIL, "Perfil", Icons.Default.Person),
)

private val sinBarra = setOf(
    Rutas.SPLASH, Rutas.CIUDAD, Rutas.CAMBIAR_CIUDAD, Rutas.LOGIN, Rutas.REGISTRO, Rutas.AVISO_PRIVACIDAD,
    Rutas.CHAT, Rutas.ENCUESTA, Rutas.FORM_ACTIVIDAD, Rutas.FORM_CAMPANA, Rutas.FORM_PROYECTO, Rutas.INSCRIPCION, Rutas.CONFIRMACION
)
private val sinSesion = setOf(Rutas.SPLASH, Rutas.CIUDAD, Rutas.CAMBIAR_CIUDAD, Rutas.LOGIN, Rutas.REGISTRO, Rutas.AVISO_PRIVACIDAD)

private val argId = listOf(navArgument("id") { type = NavType.StringType })
private fun NavBackStackEntry.id(): String = arguments?.getString("id") ?: ""
private fun NavBackStackEntry.arg(nombre: String): String? = arguments?.getString(nombre)
private fun argOpcional(nombre: String) = navArgument(nombre) { type = NavType.StringType; nullable = true; defaultValue = null }

@Composable
fun AppNav(abrirAlIniciar: String? = null, onDestinoConsumido: () -> Unit = {}) {
    val nav = rememberNavController()
    val context = LocalContext.current
    val backStack by nav.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route
    val repo = AppContainer.repositorio
    val social = AppContainer.social
    val gestion = AppContainer.gestion
    val sesionIniciada by repo.sesionIniciada.collectAsStateWithLifecycle()
    val usuario by repo.usuario.collectAsStateWithLifecycle()
    val rol = usuario?.rol ?: Rol.FAMILIA
    val esFamilia = rol == Rol.FAMILIA
    val tabs = when (rol) {
        Rol.ADMIN -> tabsAdmin
        Rol.ASOCIACION -> tabsAsociacion
        else -> tabsFamilia
    }

    // Android 13+: permiso para avisos del sistema; se pide una vez que hay sesión
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    LaunchedEffect(sesionIniciada) {
        if (sesionIniciada && Build.VERSION.SDK_INT >= 33 && !NotificacionesLocales.tienePermiso(context)) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Si el servidor rechaza el refresh token (sesión revocada o expirada) se regresa al login
    LaunchedEffect(sesionIniciada, rutaActual) {
        if (!sesionIniciada && rutaActual != null && rutaActual !in sinSesion) {
            nav.navigate(Rutas.LOGIN) { popUpTo(0) { inclusive = true } }
        }
    }

    val irAHome: () -> Unit = { nav.navigate(Rutas.HOME) { popUpTo(0) { inclusive = true } } }
    // Ir a una pestaña de la barra inferior (misma lógica que al tocarla)
    val irATab: (String) -> Unit = { ruta -> nav.irATab(ruta) }

    // Abierta (o traída al frente) desde una notificación del sistema
    LaunchedEffect(abrirAlIniciar, sesionIniciada, rutaActual) {
        val destino = abrirAlIniciar ?: return@LaunchedEffect
        if (!sesionIniciada || rutaActual == null || rutaActual in sinSesion) return@LaunchedEffect
        onDestinoConsumido()
        when (destino) {
            NotificacionesLocales.ABRIR_NOTIFICACIONES -> nav.navigate(Rutas.NOTIFICACIONES) { launchSingleTop = true }
            NotificacionesLocales.ABRIR_ACTIVIDADES -> irATab(Rutas.MIS_INSCRIPCIONES)
        }
    }
    val abrirChatCentro: (String, String) -> Unit = { centroId, nombre -> nav.navigate(Rutas.chatCentro(centroId, nombre)) }

    Scaffold(
        bottomBar = {
            if (rutaActual != null && rutaActual !in sinBarra) {
                BarraInferior(nav, rutaActual, tabs)
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
            enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(260)) { it / 8 } },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(220)) },
            popExitTransition = { fadeOut(tween(160)) + slideOutHorizontally(tween(220)) { it / 8 } }
        ) {
            composable(Rutas.SPLASH) {
                val vm: SplashViewModel = viewModel(factory = fabrica { SplashViewModel(repo) })
                SplashScreen(vm) { destino ->
                    val ruta = when (destino) {
                        DestinoInicial.CIUDAD -> Rutas.CIUDAD
                        DestinoInicial.LOGIN -> Rutas.LOGIN
                        DestinoInicial.HOME -> Rutas.HOME
                    }
                    nav.navigate(ruta) { popUpTo(Rutas.SPLASH) { inclusive = true } }
                }
            }
            composable(Rutas.CIUDAD) {
                val vm: SesionViewModel = viewModel(factory = fabrica { SesionViewModel(repo) })
                CiudadScreen(vm) { nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.CIUDAD) { inclusive = true } } }
            }
            composable(Rutas.CAMBIAR_CIUDAD) {
                val vm: SesionViewModel = viewModel(factory = fabrica { SesionViewModel(repo) })
                CiudadScreen(vm) { nav.popBackStack() }
            }
            composable(Rutas.LOGIN) {
                val vm: SesionViewModel = viewModel(factory = fabrica { SesionViewModel(repo) })
                LoginScreen(
                    vm = vm,
                    onEntrar = irAHome,
                    onRegistro = { nav.navigate(Rutas.REGISTRO) },
                    onAviso = { nav.navigate(Rutas.AVISO_PRIVACIDAD) }
                )
            }
            composable(Rutas.REGISTRO) {
                val vm: SesionViewModel = viewModel(factory = fabrica { SesionViewModel(repo) })
                RegistroScreen(
                    vm = vm,
                    onBack = { nav.popBackStack() },
                    onEntrar = irAHome,
                    onAviso = { nav.navigate(Rutas.AVISO_PRIVACIDAD) }
                )
            }

            composable(Rutas.HOME) {
                val vm: HomeViewModel = viewModel(factory = fabrica { HomeViewModel(repo) })
                HomeScreen(
                    vm = vm,
                    gestiona = !esFamilia,
                    acciones = HomeAcciones(
                        onActividades = { nav.navigate(Rutas.ACTIVIDADES) },
                        onDonar = { irATab(Rutas.DONAR) },
                        onProyectos = { nav.navigate(Rutas.PROYECTOS) },
                        onDirectorio = { nav.navigate(Rutas.DIRECTORIO) },
                        onNotificaciones = { nav.navigate(Rutas.NOTIFICACIONES) },
                        onCambiarCiudad = { nav.navigate(Rutas.CAMBIAR_CIUDAD) },
                        onActividad = { nav.navigate(Rutas.actividad(it)) },
                        onGaleria = { nav.navigate(Rutas.GALERIA) },
                        onGestion = { nav.navigate(Rutas.GESTION) }
                    )
                )
            }

            composable(Rutas.ACTIVIDADES) {
                val vm: ActividadesViewModel = viewModel(factory = fabrica { ActividadesViewModel(repo) })
                ActividadesScreen(vm, onBack = { nav.popBackStack() }, onActividad = { nav.navigate(Rutas.actividad(it)) })
            }
            composable(Rutas.DETALLE_ACTIVIDAD, arguments = argId) { entrada ->
                val id = entrada.id()
                val vm: DetalleActividadViewModel = viewModel(factory = fabrica { DetalleActividadViewModel(repo, id) })
                val socialVm: SocialViewModel = viewModel(factory = fabrica { SocialViewModel(social, TipoObjetivo.ACTIVIDAD, id, 0, false) })
                DetalleActividadScreen(
                    vm = vm,
                    socialVm = socialVm,
                    esFamilia = esFamilia,
                    onBack = { nav.popBackStack() },
                    onInscribir = { nav.navigate(Rutas.inscripcion(id)) },
                    onCentro = { nav.navigate(Rutas.centro(it)) },
                    onMensaje = { c -> abrirChatCentro(c.id, c.nombre) }
                )
            }
            composable(Rutas.INSCRIPCION, arguments = argId) { entrada ->
                val id = entrada.id()
                val vm: InscripcionViewModel = viewModel(factory = fabrica { InscripcionViewModel(repo, id) })
                InscripcionScreen(
                    vm = vm,
                    onBack = { nav.popBackStack() },
                    onConfirmado = {
                        nav.navigate(Rutas.confirmacion(id)) {
                            popUpTo(Rutas.DETALLE_ACTIVIDAD) { inclusive = false }
                        }
                    }
                )
            }
            composable(Rutas.CONFIRMACION, arguments = argId) { entrada ->
                val id = entrada.id()
                val vm: ConfirmacionViewModel = viewModel(factory = fabrica { ConfirmacionViewModel(repo, id) })
                ConfirmacionScreen(
                    vm = vm,
                    onVerInscripciones = { nav.irATab(Rutas.MIS_INSCRIPCIONES) },
                    onInicio = { nav.irATab(Rutas.HOME) }
                )
            }
            composable(Rutas.MIS_INSCRIPCIONES) {
                val vm: MisInscripcionesViewModel = viewModel(factory = fabrica { MisInscripcionesViewModel(repo) })
                MisInscripcionesScreen(
                    vm,
                    onActividad = { nav.navigate(Rutas.actividad(it)) },
                    onEncuesta = { id, momento -> nav.navigate(Rutas.encuesta(id, momento)) },
                    onVerEncuestas = { nav.navigate(Rutas.encuestasRespondidas(it)) }
                )
            }
            composable(Rutas.ENCUESTAS_RESPONDIDAS, arguments = argId) { entrada ->
                val id = entrada.id()
                val vm: EncuestasRespondidasViewModel = viewModel(factory = fabrica { EncuestasRespondidasViewModel(repo, id) })
                EncuestasRespondidasScreen(vm, onBack = { nav.popBackStack() })
            }
            composable(
                Rutas.ENCUESTA,
                arguments = listOf(navArgument("id") { type = NavType.StringType }, navArgument("momento") { type = NavType.StringType })
            ) { entrada ->
                val id = entrada.id()
                val momento = entrada.arg("momento") ?: "antes"
                val vm: EncuestaViewModel = viewModel(factory = fabrica { EncuestaViewModel(repo, id, momento) })
                EncuestaScreen(vm, onBack = { nav.popBackStack() }, onEnviada = { nav.popBackStack() })
            }

            composable(Rutas.DIRECTORIO) {
                val vm: DirectorioViewModel = viewModel(factory = fabrica { DirectorioViewModel(repo) })
                DirectorioScreen(vm, onBack = { nav.popBackStack() }, onCentro = { nav.navigate(Rutas.centro(it)) })
            }
            composable(Rutas.DETALLE_CENTRO, arguments = argId) { entrada ->
                val id = entrada.id()
                val vm: DetalleCentroViewModel = viewModel(factory = fabrica { DetalleCentroViewModel(repo, id) })
                DetalleCentroScreen(
                    vm = vm,
                    esFamilia = esFamilia,
                    onBack = { nav.popBackStack() },
                    onActividad = { nav.navigate(Rutas.actividad(it)) },
                    onDonar = { nav.navigate(Rutas.DONAR) },
                    onMensaje = { c -> abrirChatCentro(c.id, c.nombre) }
                )
            }

            composable(Rutas.DONAR) {
                val vm: DonarViewModel = viewModel(factory = fabrica { DonarViewModel(repo, social) })
                DonarScreen(vm = vm, onCentro = { nav.navigate(Rutas.centro(it)) })
            }

            composable(Rutas.PROYECTOS) {
                val vm: ProyectosViewModel = viewModel(factory = fabrica { ProyectosViewModel(repo) })
                ProyectosScreen(vm, onBack = { nav.popBackStack() }, onProyecto = { nav.navigate(Rutas.proyecto(it)) })
            }
            composable(Rutas.DETALLE_PROYECTO, arguments = argId) { entrada ->
                val id = entrada.id()
                val vm: DetalleProyectoViewModel = viewModel(factory = fabrica { DetalleProyectoViewModel(repo, id) })
                val socialVm: SocialViewModel = viewModel(factory = fabrica { SocialViewModel(social, TipoObjetivo.PROYECTO, id, 0, false) })
                DetalleProyectoScreen(vm, socialVm, onBack = { nav.popBackStack() }, onDonar = { nav.navigate(Rutas.DONAR) })
            }

            composable(Rutas.PERFIL) {
                val vm: PerfilViewModel = viewModel(factory = fabrica { PerfilViewModel(repo) })
                PerfilScreen(
                    vm = vm,
                    onGaleria = { nav.navigate(Rutas.GALERIA) },
                    onInsignias = { nav.navigate(Rutas.INSIGNIAS) },
                    onNotificaciones = { nav.navigate(Rutas.NOTIFICACIONES) },
                    onAviso = { nav.navigate(Rutas.AVISO_PRIVACIDAD) },
                    onCambiarCiudad = { nav.navigate(Rutas.CAMBIAR_CIUDAD) },
                    onCerrarSesion = { nav.navigate(Rutas.LOGIN) { popUpTo(0) { inclusive = true } } }
                )
            }
            composable(Rutas.GALERIA) {
                val vm: GaleriaViewModel = viewModel(factory = fabrica { GaleriaViewModel(repo) })
                GaleriaScreen(vm, onBack = { nav.popBackStack() })
            }
            composable(Rutas.INSIGNIAS) {
                val vm: InsigniasViewModel = viewModel(factory = fabrica { InsigniasViewModel(repo) })
                InsigniasScreen(vm, onBack = { nav.popBackStack() })
            }
            composable(Rutas.AVISO_PRIVACIDAD) {
                val vm: AvisoPrivacidadViewModel = viewModel(factory = fabrica { AvisoPrivacidadViewModel(repo) })
                AvisoPrivacidadScreen(
                    vm = vm,
                    onBack = { nav.popBackStack() },
                    onCuentaCerrada = { nav.navigate(Rutas.CIUDAD) { popUpTo(0) { inclusive = true } } }
                )
            }
            composable(Rutas.NOTIFICACIONES) {
                val vm: NotificacionesViewModel = viewModel(factory = fabrica { NotificacionesViewModel(repo) })
                NotificacionesScreen(vm, onBack = { nav.popBackStack() })
            }

            // Mensajes familia ↔ asociación
            composable(Rutas.MENSAJES) {
                val vm: ConversacionesViewModel = viewModel(factory = fabrica { ConversacionesViewModel(social) })
                ConversacionesScreen(
                    vm = vm,
                    esFamilia = esFamilia,
                    onAbrir = { c -> nav.navigate(Rutas.chatConversacion(c.id, if (esFamilia) c.centroNombre else c.familiaNombre)) },
                    onBuscarCentro = { nav.navigate(Rutas.DIRECTORIO) }
                )
            }
            composable(
                Rutas.CHAT,
                arguments = listOf(argOpcional("conversacion"), argOpcional("centro"), argOpcional("titulo"))
            ) { entrada ->
                val conversacionId = entrada.arg("conversacion")
                val centroId = entrada.arg("centro")
                val titulo = entrada.arg("titulo") ?: "Conversación"
                val vm: ChatViewModel = viewModel(factory = fabrica { ChatViewModel(social, conversacionId, centroId) })
                ChatScreen(vm, titulo = titulo, esFamilia = esFamilia, onBack = { nav.popBackStack() })
            }

            // Gestión (asociación / admin)
            composable(Rutas.GESTION) {
                val vm: GestionViewModel = viewModel(factory = fabrica { GestionViewModel(gestion, repo) })
                GestionScreen(
                    vm = vm,
                    acciones = GestionAcciones(
                        onNuevaActividad = { centroId -> nav.navigate(Rutas.formActividad(centroId)) },
                        onEditarActividad = { centroId, id -> nav.navigate(Rutas.formActividad(centroId, id)) },
                        onAsistentes = { id -> nav.navigate(Rutas.asistentes(id)) },
                        onNuevaCampana = { centroId -> nav.navigate(Rutas.formCampana(centroId)) },
                        onEditarCampana = { centroId, id -> nav.navigate(Rutas.formCampana(centroId, id)) },
                        onNuevoProyecto = { centroId -> nav.navigate(Rutas.formProyecto(centroId)) },
                        onEditarProyecto = { centroId, id -> nav.navigate(Rutas.formProyecto(centroId, id)) },
                        onModeracion = { nav.navigate(Rutas.MODERACION) }
                    )
                )
            }
            composable(Rutas.FORM_ACTIVIDAD, arguments = listOf(argOpcional("centro"), argOpcional("id"))) { entrada ->
                val centroId = entrada.arg("centro") ?: ""
                val id = entrada.arg("id")
                val vm: FormActividadViewModel = viewModel(factory = fabrica { FormActividadViewModel(gestion, centroId, id) })
                FormActividadScreen(vm, onBack = { nav.popBackStack() }, onGuardada = { nav.popBackStack() })
            }
            composable(Rutas.ASISTENTES, arguments = argId) { entrada ->
                val id = entrada.id()
                val vm: AsistentesViewModel = viewModel(factory = fabrica { AsistentesViewModel(gestion, id) })
                AsistentesScreen(vm, onBack = { nav.popBackStack() })
            }
            composable(Rutas.FORM_CAMPANA, arguments = listOf(argOpcional("centro"), argOpcional("id"))) { entrada ->
                val centroId = entrada.arg("centro") ?: ""
                val id = entrada.arg("id")
                val vm: FormCampanaViewModel = viewModel(factory = fabrica { FormCampanaViewModel(gestion, centroId, id) })
                FormCampanaScreen(vm, onBack = { nav.popBackStack() }, onGuardada = { nav.popBackStack() })
            }
            composable(Rutas.FORM_PROYECTO, arguments = listOf(argOpcional("centro"), argOpcional("id"))) { entrada ->
                val centroId = entrada.arg("centro") ?: ""
                val id = entrada.arg("id")
                val vm: FormProyectoViewModel = viewModel(factory = fabrica { FormProyectoViewModel(gestion, centroId, id) })
                FormProyectoScreen(vm, onBack = { nav.popBackStack() }, onGuardada = { nav.popBackStack() })
            }
            composable(Rutas.MODERACION) {
                val vm: ModeracionViewModel = viewModel(factory = fabrica { ModeracionViewModel(gestion) })
                ModeracionScreen(vm, onBack = { nav.popBackStack() })
            }
        }
    }
}

// Inicio es la raíz: se regresa a él sacando todo lo demás. Las otras pestañas se abren frescas encima de Inicio.
private fun NavHostController.irATab(ruta: String) {
    if (ruta == Rutas.HOME) {
        if (!popBackStack(Rutas.HOME, inclusive = false)) navigate(Rutas.HOME) { popUpTo(0) { inclusive = true } }
    } else {
        navigate(ruta) {
            popUpTo(Rutas.HOME)
            launchSingleTop = true
        }
    }
}

@Composable
private fun BarraInferior(nav: NavHostController, rutaActual: String, tabs: List<Tab>) {
    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = rutaActual == tab.ruta,
                onClick = {
                    if (rutaActual == tab.ruta) {
                        // Ya estás en la pestaña: vuelve a su inicio (filtros y scroll limpios)
                        nav.navigate(tab.ruta) { popUpTo(tab.ruta) { inclusive = true } }
                    } else {
                        nav.irATab(tab.ruta)
                    }
                },
                icon = { Icon(tab.icono, contentDescription = tab.etiqueta) },
                label = { Text(tab.etiqueta) }
            )
        }
    }
}
