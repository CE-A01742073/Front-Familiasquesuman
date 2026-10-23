package com.familiasquesuman.demo.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class Ciudad(val clave: String, val etiqueta: String) {
    MONTERREY("monterrey", "Monterrey, N.L."),
    HERMOSILLO("hermosillo", "Hermosillo, Son.");

    companion object {
        fun desdeClave(clave: String?): Ciudad = entries.firstOrNull { it.clave == clave } ?: MONTERREY
    }
}

enum class Categoria(val clave: String, val etiqueta: String) {
    TODAS("", "Todas"),
    NINOS("ninos", "Niños"),
    ADULTOS_MAYORES("adultos_mayores", "Adultos mayores"),
    MEDIO_AMBIENTE("medio_ambiente", "Medio ambiente"),
    ANIMALES("animales", "Animales"),
    COMUNIDAD("comunidad", "Comunidad");

    companion object {
        fun desdeClave(clave: String?): Categoria = entries.firstOrNull { it.clave == clave } ?: COMUNIDAD
    }
}

enum class CategoriaDonacion(val clave: String, val etiqueta: String) {
    TODOS("", "Todos"),
    JUGUETES("juguetes", "Juguetes"),
    ROPA("ropa", "Ropa"),
    ALIMENTOS("alimentos", "Alimentos"),
    HIGIENE("higiene", "Higiene"),
    SILLAS_RUEDAS("sillas_ruedas", "Sillas de ruedas"),
    UTILES("utiles", "Útiles escolares"),
    COBIJAS("cobijas", "Cobijas"),
    CROQUETAS("croquetas", "Croquetas");

    companion object {
        fun desdeClave(clave: String?): CategoriaDonacion? = entries.firstOrNull { it.clave == clave && it != TODOS }
    }
}

enum class ModoTema(val etiqueta: String) { SISTEMA("Sistema"), CLARO("Claro"), OSCURO("Oscuro") }

object Rol {
    const val FAMILIA = "familia"
    const val ASOCIACION = "asociacion"
    const val ADMIN = "admin"
}

@Serializable
data class Usuario(
    val id: String,
    val correo: String,
    val nombre: String,
    val rol: String,
    val descripcion: String = "",
    val tieneFoto: Boolean = false,
    val fotoUrl: String? = null,
    val centroId: String? = null
) {
    val esFamilia: Boolean get() = rol == Rol.FAMILIA
    val esAsociacion: Boolean get() = rol == Rol.ASOCIACION
    val esAdmin: Boolean get() = rol == Rol.ADMIN
    val gestiona: Boolean get() = esAsociacion || esAdmin
}

@Serializable
data class Integrante(
    val id: String,
    val nombre: String,
    val edad: Int
)

@Serializable
data class Familia(
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val zona: String = "",
    val integrantes: List<Integrante> = emptyList()
)

data class Centro(
    val id: String,
    val nombre: String,
    val tipo: String,
    val categoria: Categoria,
    val descripcion: String,
    val direccion: String,
    val horarioVisitas: String,
    val queNecesitan: List<String>,
    val acepta: List<CategoriaDonacion> = emptyList(),
    val telefono: String? = null,
    val whatsapp: String? = null,
    val correo: String? = null,
    val verificado: Boolean = true,
    val imagenUrl: String? = null
)

data class Campana(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val centroId: String,
    val centroNombre: String,
    val queLlevar: List<String> = emptyList(),
    val dondeEntregar: String? = null,
    val horarioEntrega: String? = null,
    val hasta: String? = null,
    val activa: Boolean = true,
    val yaApoye: Boolean = false,
    val likes: Int = 0,
    val meGusta: Boolean = false,
    val imagenUrl: String? = null
) {
    val hastaTexto: String? get() = hasta?.let(::fechaBonita)
}

data class Actividad(
    val id: String,
    val titulo: String,
    val centroId: String,
    val centroNombre: String,
    val categoria: Categoria,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String?,
    val lugar: String,
    val descripcion: String,
    val requisitos: List<String>,
    val edadMinima: Int,
    val aptaNinos: Boolean,
    val cuposTotales: Int,
    val cuposOcupados: Int,
    val cuposDisponibles: Int,
    val estado: String,
    val likes: Int = 0,
    val meGusta: Boolean = false,
    val imagenUrl: String? = null
) {
    val fechaTexto: String get() = fechaBonita(fecha)
    val hora: String get() = if (horaFin != null) "${horaCorta(horaInicio)}–${horaCorta(horaFin)}" else horaCorta(horaInicio)
    val activa: Boolean get() = estado == "activa"
    val yaPaso: Boolean get() = runCatching { LocalDate.parse(fecha).isBefore(LocalDate.now()) }.getOrDefault(false)
}

data class Proyecto(
    val id: String,
    val titulo: String,
    val causa: String,
    val objetivo: String,
    val descripcion: String,
    val avance: Float,
    val activo: Boolean,
    val centroId: String,
    val centroNombre: String,
    val creadoEn: String,
    val actualizadoEn: String,
    val likes: Int = 0,
    val meGusta: Boolean = false,
    val imagenUrl: String? = null
)

@Serializable
data class Inscripcion(
    val id: String,
    val actividadId: String,
    val actividadTitulo: String,
    val centroId: String,
    val centroNombre: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String?,
    val lugar: String,
    val nombres: List<String>,
    val comentario: String,
    val estado: String,
    val encuestaAntes: Boolean = false,
    val encuestaDespues: Boolean = false
) {
    val pendiente: Boolean get() = estado == "pendiente"
    val pasada: Boolean
        get() = estado == "asistio" || estado == "no_asistio" ||
            runCatching { LocalDate.parse(fecha).isBefore(LocalDate.now()) }.getOrDefault(false)
    val fechaTexto: String get() = fechaBonita(fecha)
    val hora: String get() = if (horaFin != null) "${horaCorta(horaInicio)}–${horaCorta(horaFin)}" else horaCorta(horaInicio)
}

data class Notificacion(
    val id: String,
    val titulo: String,
    val cuerpo: String,
    val hace: String,
    val leida: Boolean
)

@Serializable
data class Donacion(
    val id: String,
    val tipo: String,
    val detalle: String,
    val centroId: String,
    val campanaId: String? = null
)

// ---- Social ----

object TipoObjetivo {
    const val ACTIVIDAD = "actividad"
    const val CAMPANA = "campana"
    const val PROYECTO = "proyecto"
    const val TESTIMONIO = "testimonio"
}

data class Comentario(
    val id: String,
    val autorNombre: String,
    val esMio: Boolean,
    val texto: String,
    val estado: String,
    val hace: String
)

data class Testimonio(
    val id: String,
    val familiaNombre: String,
    val actividadId: String,
    val actividadTitulo: String,
    val texto: String,
    val fotoUrl: String?,
    val estado: String,
    val motivoRechazo: String?,
    val likes: Int,
    val meGusta: Boolean,
    val hace: String
)

data class Conversacion(
    val id: String,
    val familiaNombre: String,
    val centroId: String,
    val centroNombre: String,
    val bloqueada: Boolean,
    val noLeidos: Int,
    val ultimoMensaje: String,
    val actualizadoEn: String
)

data class Mensaje(
    val id: String,
    val esMio: Boolean,
    val emisorRol: String,
    val texto: String,
    val creadoEn: String,
    val hora: String
)

// ---- Encuestas e insignias ----

data class Pregunta(val clave: String, val pregunta: String, val tipo: String)

data class RespuestaEncuesta(val pregunta: String, val respuesta: String)

data class EncuestaRespondida(val momento: String, val fecha: String, val respuestas: List<RespuestaEncuesta>) {
    val titulo: String get() = if (momento == "antes") "Antes de la actividad" else "Después de la actividad"
    val fechaTexto: String get() = fechaBonita(fecha.take(10))
}

data class Insignia(val clave: String, val nombre: String, val descripcion: String, val minimo: Int, val obtenida: Boolean)

data class CaminoSolidario(
    val actividadesCompletadas: Int,
    val nivel: String,
    val siguienteNivel: String?,
    val faltanParaSiguiente: Int,
    val insignias: List<Insignia>
)

// ---- Gestión (asociación / admin) ----

data class AsistenteLista(
    val inscripcionId: String,
    val familiaNombre: String,
    val contactoEmail: String,
    val comentario: String?,
    val estado: String,
    val personas: Int,
    val asistentes: List<String>
)

data class ResumenAdmin(
    val familias: Int,
    val actividadesActivas: Int,
    val inscripciones: Int,
    val testimoniosPendientes: Int,
    val comentariosPendientes: Int
)

private val formatoFecha = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "MX"))

fun fechaBonita(iso: String): String = runCatching {
    val f = LocalDate.parse(iso)
    val dia = f.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "MX")).replaceFirstChar { it.uppercase() }
    "$dia ${f.format(formatoFecha)}"
}.getOrDefault(iso)

fun horaCorta(hora: String): String = hora.take(5)
