package com.familiasquesuman.demo.data

import com.familiasquesuman.demo.data.remote.ActividadDto
import com.familiasquesuman.demo.data.remote.AsistenteListaDto
import com.familiasquesuman.demo.data.remote.CampanaDto
import com.familiasquesuman.demo.data.remote.CentroDto
import com.familiasquesuman.demo.data.remote.ComentarioDto
import com.familiasquesuman.demo.data.remote.ConversacionDto
import com.familiasquesuman.demo.data.remote.DonacionDto
import com.familiasquesuman.demo.data.remote.FamiliaDto
import com.familiasquesuman.demo.data.remote.InscripcionDto
import com.familiasquesuman.demo.data.remote.InsigniasDto
import com.familiasquesuman.demo.data.remote.MensajeChatDto
import com.familiasquesuman.demo.data.remote.NotificacionDto
import com.familiasquesuman.demo.data.remote.ProyectoDto
import com.familiasquesuman.demo.data.remote.ResumenAdminDto
import com.familiasquesuman.demo.data.remote.TestimonioDto
import com.familiasquesuman.demo.data.remote.UsuarioDto
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.AsistenteLista
import com.familiasquesuman.demo.model.CaminoSolidario
import com.familiasquesuman.demo.model.Campana
import com.familiasquesuman.demo.model.Categoria
import com.familiasquesuman.demo.model.CategoriaDonacion
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.model.Comentario
import com.familiasquesuman.demo.model.Conversacion
import com.familiasquesuman.demo.model.Donacion
import com.familiasquesuman.demo.model.Familia
import com.familiasquesuman.demo.model.Inscripcion
import com.familiasquesuman.demo.model.Insignia
import com.familiasquesuman.demo.model.Integrante
import com.familiasquesuman.demo.model.Mensaje
import com.familiasquesuman.demo.model.Notificacion
import com.familiasquesuman.demo.model.Proyecto
import com.familiasquesuman.demo.model.ResumenAdmin
import com.familiasquesuman.demo.model.Testimonio
import com.familiasquesuman.demo.model.Usuario
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun urlAbsoluta(host: String, ruta: String?): String? =
    ruta?.let { if (it.startsWith("http")) it else host + it }

fun CentroDto.aModelo(host: String) = Centro(
    id = id,
    nombre = nombre,
    tipo = tipo,
    categoria = Categoria.desdeClave(categoria),
    descripcion = descripcion,
    direccion = direccion,
    horarioVisitas = horarioVisitas ?: "Consultar",
    queNecesitan = queNecesitan,
    acepta = acepta.mapNotNull { CategoriaDonacion.desdeClave(it) },
    telefono = telefono,
    whatsapp = whatsapp,
    correo = correo,
    verificado = verificado,
    imagenUrl = urlAbsoluta(host, imagenUrl)
)

fun ActividadDto.aModelo(host: String) = Actividad(
    id = id,
    titulo = titulo,
    centroId = centroId,
    centroNombre = centroNombre ?: "",
    categoria = Categoria.desdeClave(categoria),
    fecha = fecha,
    horaInicio = horaInicio,
    horaFin = horaFin,
    lugar = lugar,
    descripcion = descripcion,
    requisitos = requisitos,
    edadMinima = edadMinima,
    aptaNinos = aptaNinos,
    cuposTotales = cuposTotales,
    cuposOcupados = cuposOcupados,
    cuposDisponibles = cuposDisponibles,
    estado = estado,
    likes = likes,
    meGusta = meGusta,
    imagenUrl = urlAbsoluta(host, imagenUrl)
)

fun CampanaDto.aModelo(host: String) = Campana(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    centroId = centroId,
    centroNombre = centroNombre ?: "",
    queLlevar = queLlevar,
    dondeEntregar = dondeEntregar,
    horarioEntrega = horarioEntrega,
    hasta = hasta,
    activa = activa,
    yaApoye = yaApoye,
    likes = likes,
    meGusta = meGusta,
    imagenUrl = urlAbsoluta(host, imagenUrl)
)

fun ProyectoDto.aModelo(host: String) = Proyecto(
    id = id,
    titulo = titulo,
    causa = causa,
    objetivo = objetivo,
    descripcion = descripcion,
    avance = avance.coerceIn(0f, 1f),
    activo = activo,
    centroId = centroId,
    centroNombre = centroNombre ?: "",
    creadoEn = creadoEn,
    actualizadoEn = actualizadoEn,
    likes = likes,
    meGusta = meGusta,
    imagenUrl = urlAbsoluta(host, imagenUrl)
)

fun FamiliaDto.aModelo(correo: String) = Familia(
    id = id,
    nombre = nombre,
    correo = correo,
    zona = zona ?: "",
    integrantes = integrantes.map { Integrante(it.id, it.nombre, it.edad) }
)

fun UsuarioDto.aModelo(host: String) = Usuario(
    id = id,
    correo = email,
    nombre = nombre,
    rol = rol,
    descripcion = descripcion ?: "",
    tieneFoto = tieneFoto,
    fotoUrl = urlAbsoluta(host, fotoUrl),
    centroId = centroId
)

fun InscripcionDto.aModelo() = Inscripcion(
    id = id,
    actividadId = actividadId,
    actividadTitulo = actividad?.titulo ?: "Actividad",
    centroId = actividad?.centroId ?: "",
    centroNombre = actividad?.centroNombre ?: "",
    fecha = actividad?.fecha ?: "",
    horaInicio = actividad?.horaInicio ?: "",
    horaFin = actividad?.horaFin,
    lugar = actividad?.lugar ?: "",
    nombres = asistentes.map { it.nombre },
    comentario = comentario ?: "",
    estado = estado,
    encuestaAntes = encuestaAntes,
    encuestaDespues = encuestaDespues
)

fun DonacionDto.aModelo() = Donacion(id = id, tipo = tipo, detalle = detalle, centroId = centroId, campanaId = campanaId)

fun NotificacionDto.aModelo() = Notificacion(
    id = id,
    titulo = titulo,
    cuerpo = cuerpo,
    hace = haceTexto(creadoEn),
    leida = leida
)

fun ComentarioDto.aModelo() = Comentario(
    id = id,
    autorNombre = autorNombre.ifBlank { "Usuario" },
    esMio = esMio,
    texto = texto,
    estado = estado,
    hace = haceTexto(creadoEn)
)

fun TestimonioDto.aModelo(host: String) = Testimonio(
    id = id,
    familiaNombre = familiaNombre ?: "Una familia",
    actividadId = actividadId,
    actividadTitulo = actividadTitulo ?: "",
    texto = texto,
    fotoUrl = urlAbsoluta(host, fotoUrl),
    estado = estado,
    motivoRechazo = motivoRechazo,
    likes = likes,
    meGusta = meGusta,
    hace = haceTexto(creadoEn)
)

fun ConversacionDto.aModelo() = Conversacion(
    id = id,
    familiaNombre = familiaNombre ?: "Familia",
    centroId = centroId,
    centroNombre = centroNombre ?: "Asociación",
    bloqueada = bloqueada,
    noLeidos = noLeidos,
    ultimoMensaje = ultimoMensaje?.texto ?: "",
    actualizadoEn = actualizadoEn
)

fun MensajeChatDto.aModelo() = Mensaje(
    id = id,
    esMio = esMio,
    emisorRol = emisorRol,
    texto = texto,
    creadoEn = creadoEn,
    hora = horaLocal(creadoEn)
)

fun InsigniasDto.aModelo() = CaminoSolidario(
    actividadesCompletadas = actividadesCompletadas,
    nivel = nivel,
    siguienteNivel = siguienteNivel,
    faltanParaSiguiente = faltanParaSiguiente,
    insignias = insignias.map { Insignia(it.clave, it.nombre, it.descripcion, it.minimo, it.obtenida) }
)

fun AsistenteListaDto.aModelo() = AsistenteLista(
    inscripcionId = inscripcionId,
    familiaNombre = familiaNombre,
    contactoEmail = contactoEmail,
    comentario = comentario,
    estado = estado,
    personas = personas,
    asistentes = asistentes.map { "${it.nombre} (${it.edad})" }
)

// Respuesta cruda de la encuesta (1-5, "si"/"no" o texto) a algo legible
fun respuestaTexto(valor: JsonElement, tipo: String?): String {
    val crudo = (valor as? JsonPrimitive)?.content ?: valor.toString()
    return when (tipo) {
        "escala" -> "$crudo de 5"
        "si_no" -> if (crudo.equals("si", ignoreCase = true)) "Sí" else "No"
        else -> crudo
    }
}

fun ResumenAdminDto.aModelo() = ResumenAdmin(familias, actividadesActivas, inscripciones, testimoniosPendientes, comentariosPendientes)

// La marca de /sync se guarda en UTC con "Z" para que viaje limpia en la query (sin "+")
fun marcaIso(marca: String): String =
    runCatching { OffsetDateTime.parse(marca).toInstant().toString() }.getOrDefault(marca)

private val formatoHora = DateTimeFormatter.ofPattern("HH:mm")

private fun horaLocal(iso: String): String = runCatching {
    OffsetDateTime.parse(iso).atZoneSameInstant(ZoneId.systemDefault()).format(formatoHora)
}.getOrDefault("")

private fun haceTexto(iso: String): String = runCatching {
    val d = Duration.between(OffsetDateTime.parse(iso), OffsetDateTime.now())
    when {
        d.toMinutes() < 1 -> "ahora"
        d.toMinutes() < 60 -> "hace ${d.toMinutes()} min"
        d.toHours() < 24 -> "hace ${d.toHours()} h"
        d.toDays() < 7 -> "hace ${d.toDays()} d"
        else -> "hace ${d.toDays() / 7} sem"
    }
}.getOrDefault("")
