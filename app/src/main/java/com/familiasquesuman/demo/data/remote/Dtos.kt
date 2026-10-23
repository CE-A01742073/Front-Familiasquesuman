package com.familiasquesuman.demo.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Formas exactas de la API (snake_case). Las respuestas llevan campos opcionales por seguridad.

@Serializable
data class ErrorDto(val error: DetalleErrorDto? = null)

@Serializable
data class DetalleErrorDto(
    val codigo: String? = null,
    val mensaje: String? = null,
    @SerialName("request_id") val requestId: String? = null
)

@Serializable
data class PaginaDto<T>(
    val items: List<T> = emptyList(),
    val total: Int = 0,
    val limit: Int = 0,
    val offset: Int = 0
)

@Serializable
data class MensajeDto(val mensaje: String? = null)

// ---- Auth ----

@Serializable
data class RegistroIn(
    val email: String,
    val password: String,
    val nombre: String,
    @SerialName("nombre_familia") val nombreFamilia: String,
    val ciudad: String,
    @SerialName("acepta_aviso_privacidad") val aceptaAvisoPrivacidad: Boolean
)

@Serializable
data class LoginIn(val email: String, val password: String)

@Serializable
data class GoogleIn(
    @SerialName("id_token") val idToken: String,
    @SerialName("nombre_familia") val nombreFamilia: String? = null,
    val ciudad: String? = null,
    @SerialName("acepta_aviso_privacidad") val aceptaAvisoPrivacidad: Boolean = false
)

@Serializable
data class RefreshIn(@SerialName("refresh_token") val refreshToken: String)

@Serializable
data class UsuarioDto(
    val id: String,
    val email: String,
    val nombre: String,
    val rol: String,
    val descripcion: String? = null,
    @SerialName("tiene_foto") val tieneFoto: Boolean = false,
    @SerialName("foto_url") val fotoUrl: String? = null,
    @SerialName("centro_id") val centroId: String? = null
)

@Serializable
data class PerfilPatch(val nombre: String? = null, val descripcion: String? = null)

@Serializable
data class TokensDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Int = 0,
    val usuario: UsuarioDto,
    @SerialName("familia_creada") val familiaCreada: Boolean = false
)

// ---- Perfil / familia ----

@Serializable
data class IntegranteIn(val nombre: String, val edad: Int)

@Serializable
data class IntegranteDto(val id: String, val nombre: String, val edad: Int)

@Serializable
data class FamiliaDto(
    val id: String,
    val nombre: String,
    val zona: String? = null,
    @SerialName("ciudad_id") val ciudadId: String? = null,
    val integrantes: List<IntegranteDto> = emptyList()
)

@Serializable
data class FamiliaPatch(
    val nombre: String? = null,
    val zona: String? = null,
    val ciudad: String? = null
)

// ---- Catálogo ----

@Serializable
data class CentroDto(
    val id: String,
    val nombre: String,
    val tipo: String = "",
    val categoria: String = "comunidad",
    val descripcion: String = "",
    val direccion: String = "",
    @SerialName("horario_visitas") val horarioVisitas: String? = null,
    @SerialName("que_necesitan") val queNecesitan: List<String> = emptyList(),
    val acepta: List<String> = emptyList(),
    val telefono: String? = null,
    val whatsapp: String? = null,
    val correo: String? = null,
    val verificado: Boolean = true,
    val activo: Boolean = true,
    @SerialName("ciudad_id") val ciudadId: String? = null,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    @SerialName("actualizado_en") val actualizadoEn: String? = null
)

@Serializable
data class ActividadDto(
    val id: String,
    @SerialName("centro_id") val centroId: String,
    @SerialName("centro_nombre") val centroNombre: String? = null,
    @SerialName("ciudad_id") val ciudadId: String? = null,
    val titulo: String,
    val categoria: String = "comunidad",
    val fecha: String,
    @SerialName("hora_inicio") val horaInicio: String = "",
    @SerialName("hora_fin") val horaFin: String? = null,
    val lugar: String = "",
    val descripcion: String = "",
    val requisitos: List<String> = emptyList(),
    @SerialName("edad_minima") val edadMinima: Int = 0,
    @SerialName("apta_ninos") val aptaNinos: Boolean = true,
    @SerialName("cupos_totales") val cuposTotales: Int = 0,
    @SerialName("cupos_ocupados") val cuposOcupados: Int = 0,
    @SerialName("cupos_disponibles") val cuposDisponibles: Int = 0,
    val estado: String = "activa",
    @SerialName("imagen_url") val imagenUrl: String? = null,
    val likes: Int = 0,
    @SerialName("me_gusta") val meGusta: Boolean = false,
    @SerialName("actualizado_en") val actualizadoEn: String? = null
)

@Serializable
data class CampanaDto(
    val id: String,
    @SerialName("centro_id") val centroId: String,
    @SerialName("centro_nombre") val centroNombre: String? = null,
    @SerialName("ciudad_id") val ciudadId: String? = null,
    val titulo: String,
    val descripcion: String = "",
    @SerialName("que_llevar") val queLlevar: List<String> = emptyList(),
    @SerialName("donde_entregar") val dondeEntregar: String? = null,
    @SerialName("horario_entrega") val horarioEntrega: String? = null,
    val hasta: String? = null,
    val activa: Boolean = true,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    val likes: Int = 0,
    @SerialName("me_gusta") val meGusta: Boolean = false,
    @SerialName("ya_apoye") val yaApoye: Boolean = false,
    @SerialName("actualizado_en") val actualizadoEn: String? = null
)

@Serializable
data class ProyectoDto(
    val id: String,
    @SerialName("centro_id") val centroId: String,
    @SerialName("centro_nombre") val centroNombre: String? = null,
    val titulo: String,
    val causa: String = "",
    val objetivo: String = "",
    val descripcion: String = "",
    val avance: Float = 0f,
    val activo: Boolean = true,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    val likes: Int = 0,
    @SerialName("me_gusta") val meGusta: Boolean = false,
    @SerialName("creado_en") val creadoEn: String = "",
    @SerialName("actualizado_en") val actualizadoEn: String = ""
)

@Serializable
data class SyncDto(
    val marca: String,
    val ciudad: String? = null,
    val centros: List<CentroDto> = emptyList(),
    val actividades: List<ActividadDto> = emptyList(),
    val campanas: List<CampanaDto> = emptyList(),
    val proyectos: List<ProyectoDto> = emptyList()
)

// ---- Inscripciones ----

@Serializable
data class InscripcionIn(
    @SerialName("integrantes_ids") val integrantesIds: List<String>,
    val comentario: String? = null
)

@Serializable
data class AsistenteDto(
    @SerialName("integrante_id") val integranteId: String? = null,
    val nombre: String,
    val edad: Int = 0
)

@Serializable
data class ActividadResumenDto(
    val id: String,
    val titulo: String,
    val fecha: String,
    @SerialName("hora_inicio") val horaInicio: String = "",
    @SerialName("hora_fin") val horaFin: String? = null,
    val lugar: String = "",
    @SerialName("centro_id") val centroId: String,
    @SerialName("centro_nombre") val centroNombre: String? = null,
    val estado: String = "activa"
)

@Serializable
data class InscripcionDto(
    val id: String,
    @SerialName("actividad_id") val actividadId: String,
    @SerialName("familia_id") val familiaId: String? = null,
    val comentario: String? = null,
    val estado: String = "confirmada",
    val personas: Int = 0,
    val asistentes: List<AsistenteDto> = emptyList(),
    val actividad: ActividadResumenDto? = null,
    @SerialName("encuesta_antes") val encuestaAntes: Boolean = false,
    @SerialName("encuesta_despues") val encuestaDespues: Boolean = false,
    @SerialName("creado_en") val creadoEn: String? = null
)

// ---- Donaciones / notificaciones ----

@Serializable
data class DonacionIn(
    @SerialName("campana_id") val campanaId: String? = null,
    @SerialName("centro_id") val centroId: String? = null,
    val tipo: String,
    val detalle: String
)

@Serializable
data class DonacionDto(
    val id: String,
    @SerialName("centro_id") val centroId: String,
    @SerialName("campana_id") val campanaId: String? = null,
    val tipo: String,
    val detalle: String,
    @SerialName("creado_en") val creadoEn: String? = null
)

@Serializable
data class NotificacionDto(
    val id: String,
    val titulo: String,
    val cuerpo: String,
    val tipo: String = "",
    @SerialName("referencia_id") val referenciaId: String? = null,
    val leida: Boolean = false,
    @SerialName("creado_en") val creadoEn: String = ""
)

// ---- Social ----

@Serializable
data class LikeDto(
    @SerialName("objetivo_tipo") val objetivoTipo: String,
    @SerialName("objetivo_id") val objetivoId: String,
    val likes: Int = 0,
    @SerialName("me_gusta") val meGusta: Boolean = false
)

@Serializable
data class ComentarioIn(val texto: String)

@Serializable
data class ComentarioDto(
    val id: String,
    @SerialName("objetivo_tipo") val objetivoTipo: String = "",
    @SerialName("objetivo_id") val objetivoId: String = "",
    @SerialName("autor_nombre") val autorNombre: String = "",
    @SerialName("es_mio") val esMio: Boolean = false,
    val texto: String,
    val estado: String = "aprobado",
    @SerialName("creado_en") val creadoEn: String = ""
)

@Serializable
data class ReporteIn(
    @SerialName("objetivo_tipo") val objetivoTipo: String,
    @SerialName("objetivo_id") val objetivoId: String,
    val motivo: String
)

@Serializable
data class TestimonioDto(
    val id: String,
    @SerialName("familia_id") val familiaId: String = "",
    @SerialName("familia_nombre") val familiaNombre: String? = null,
    @SerialName("actividad_id") val actividadId: String,
    @SerialName("actividad_titulo") val actividadTitulo: String? = null,
    val texto: String,
    @SerialName("foto_url") val fotoUrl: String? = null,
    val estado: String = "pendiente",
    @SerialName("motivo_rechazo") val motivoRechazo: String? = null,
    val likes: Int = 0,
    @SerialName("me_gusta") val meGusta: Boolean = false,
    @SerialName("creado_en") val creadoEn: String = ""
)

@Serializable
data class ModeracionIn(val aprobar: Boolean, val motivo: String? = null)

// ---- Mensajes ----

@Serializable
data class ConversacionIn(@SerialName("centro_id") val centroId: String, val texto: String)

@Serializable
data class MensajeIn(val texto: String)

@Serializable
data class MensajeChatDto(
    val id: String,
    @SerialName("conversacion_id") val conversacionId: String = "",
    @SerialName("emisor_rol") val emisorRol: String = "",
    @SerialName("es_mio") val esMio: Boolean = false,
    val texto: String,
    val leido: Boolean = false,
    @SerialName("creado_en") val creadoEn: String = ""
)

@Serializable
data class ConversacionDto(
    val id: String,
    @SerialName("familia_id") val familiaId: String = "",
    @SerialName("familia_nombre") val familiaNombre: String? = null,
    @SerialName("centro_id") val centroId: String,
    @SerialName("centro_nombre") val centroNombre: String? = null,
    val bloqueada: Boolean = false,
    @SerialName("no_leidos") val noLeidos: Int = 0,
    @SerialName("ultimo_mensaje") val ultimoMensaje: MensajeChatDto? = null,
    @SerialName("actualizado_en") val actualizadoEn: String = ""
)

// ---- Encuestas e insignias ----

@Serializable
data class PreguntaDto(val clave: String, val pregunta: String, val tipo: String)

@Serializable
data class EncuestaPreguntasDto(val momento: String = "", val preguntas: List<PreguntaDto> = emptyList())

@Serializable
data class EncuestaIn(val momento: String, val respuestas: Map<String, kotlinx.serialization.json.JsonElement>)

@Serializable
data class EncuestaDto(
    @SerialName("inscripcion_id") val inscripcionId: String = "",
    val momento: String = "",
    val respuestas: Map<String, kotlinx.serialization.json.JsonElement> = emptyMap(),
    @SerialName("creado_en") val creadoEn: String = ""
)

@Serializable
data class InsigniaDto(
    val clave: String,
    val nombre: String,
    val descripcion: String = "",
    val minimo: Int = 0,
    val obtenida: Boolean = false
)

@Serializable
data class InsigniasDto(
    @SerialName("actividades_completadas") val actividadesCompletadas: Int = 0,
    val nivel: String = "",
    @SerialName("siguiente_nivel") val siguienteNivel: String? = null,
    @SerialName("faltan_para_siguiente") val faltanParaSiguiente: Int = 0,
    val insignias: List<InsigniaDto> = emptyList()
)

// ---- Gestión ----

@Serializable
data class ActividadIn(
    val titulo: String,
    val categoria: String,
    val fecha: String,
    @SerialName("hora_inicio") val horaInicio: String,
    @SerialName("hora_fin") val horaFin: String? = null,
    val lugar: String,
    val descripcion: String,
    val requisitos: List<String> = emptyList(),
    @SerialName("edad_minima") val edadMinima: Int = 0,
    @SerialName("apta_ninos") val aptaNinos: Boolean = true,
    @SerialName("cupos_totales") val cuposTotales: Int
)

@Serializable
data class ActividadPatch(
    val titulo: String? = null,
    val categoria: String? = null,
    val fecha: String? = null,
    @SerialName("hora_inicio") val horaInicio: String? = null,
    @SerialName("hora_fin") val horaFin: String? = null,
    val lugar: String? = null,
    val descripcion: String? = null,
    val requisitos: List<String>? = null,
    @SerialName("edad_minima") val edadMinima: Int? = null,
    @SerialName("apta_ninos") val aptaNinos: Boolean? = null,
    @SerialName("cupos_totales") val cuposTotales: Int? = null,
    val estado: String? = null
)

@Serializable
data class CampanaIn(
    val titulo: String,
    val descripcion: String,
    @SerialName("que_llevar") val queLlevar: List<String> = emptyList(),
    @SerialName("donde_entregar") val dondeEntregar: String? = null,
    @SerialName("horario_entrega") val horarioEntrega: String? = null,
    val hasta: String? = null
)

@Serializable
data class CampanaPatch(
    val titulo: String? = null,
    val descripcion: String? = null,
    @SerialName("que_llevar") val queLlevar: List<String>? = null,
    @SerialName("donde_entregar") val dondeEntregar: String? = null,
    @SerialName("horario_entrega") val horarioEntrega: String? = null,
    val hasta: String? = null,
    val activa: Boolean? = null
)

@Serializable
data class ProyectoIn(
    val titulo: String,
    val causa: String,
    val objetivo: String,
    val descripcion: String,
    val avance: Float = 0f
)

@Serializable
data class ProyectoPatch(
    val titulo: String? = null,
    val causa: String? = null,
    val objetivo: String? = null,
    val descripcion: String? = null,
    val avance: Float? = null,
    val activo: Boolean? = null
)

@Serializable
data class AsistenteListaDto(
    @SerialName("inscripcion_id") val inscripcionId: String,
    @SerialName("familia_nombre") val familiaNombre: String = "",
    @SerialName("contacto_email") val contactoEmail: String = "",
    val comentario: String? = null,
    val estado: String = "confirmada",
    val personas: Int = 0,
    val asistentes: List<AsistenteDto> = emptyList()
)

@Serializable
data class AsistenciaIn(
    val asistieron: List<String> = emptyList(),
    @SerialName("no_asistieron") val noAsistieron: List<String> = emptyList()
)

@Serializable
data class ResumenAdminDto(
    val familias: Int = 0,
    @SerialName("actividades_activas") val actividadesActivas: Int = 0,
    val inscripciones: Int = 0,
    @SerialName("testimonios_pendientes") val testimoniosPendientes: Int = 0,
    @SerialName("comentarios_pendientes") val comentariosPendientes: Int = 0
)
