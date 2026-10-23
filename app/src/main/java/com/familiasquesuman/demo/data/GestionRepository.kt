package com.familiasquesuman.demo.data

import com.familiasquesuman.demo.data.remote.ActividadIn
import com.familiasquesuman.demo.data.remote.ActividadPatch
import com.familiasquesuman.demo.data.remote.ApiClient
import com.familiasquesuman.demo.data.remote.AsistenciaIn
import com.familiasquesuman.demo.data.remote.CampanaIn
import com.familiasquesuman.demo.data.remote.CampanaPatch
import com.familiasquesuman.demo.data.remote.ModeracionIn
import com.familiasquesuman.demo.data.remote.ProyectoIn
import com.familiasquesuman.demo.data.remote.ProyectoPatch
import com.familiasquesuman.demo.data.remote.llamarApi
import com.familiasquesuman.demo.model.Actividad
import com.familiasquesuman.demo.model.AsistenteLista
import com.familiasquesuman.demo.model.Campana
import com.familiasquesuman.demo.model.Centro
import com.familiasquesuman.demo.model.Comentario
import com.familiasquesuman.demo.model.Proyecto
import com.familiasquesuman.demo.model.ResumenAdmin
import com.familiasquesuman.demo.model.Testimonio

// Lo que hace una asociación con su centro, y el admin con todo. El servidor re-verifica el rol.
class GestionRepository(private val cliente: ApiClient) {
    private val api get() = cliente.api
    private val host = cliente.host

    suspend fun misCentros(): List<Centro> = llamarApi { api.misCentros() }.map { it.aModelo(host) }

    suspend fun actividadesDeCentro(centroId: String): List<Actividad> =
        llamarApi { api.actividadesDeMiCentro(centroId) }.map { it.aModelo(host) }

    suspend fun crearActividad(centroId: String, datos: ActividadIn): Actividad =
        llamarApi { api.crearActividad(centroId, datos) }.aModelo(host)

    suspend fun editarActividad(id: String, datos: ActividadPatch): Actividad =
        llamarApi { api.editarActividad(id, datos) }.aModelo(host)

    suspend fun cancelarActividad(id: String): Actividad = editarActividad(id, ActividadPatch(estado = "cancelada"))

    suspend fun subirImagenActividad(id: String, bytes: ByteArray): Actividad =
        llamarApi { api.imagenActividad(id, Imagenes.parte("archivo", bytes)) }.aModelo(host)

    suspend fun asistentes(actividadId: String): List<AsistenteLista> =
        llamarApi { api.asistentes(actividadId) }.map { it.aModelo() }

    suspend fun marcarAsistencia(actividadId: String, asistieron: List<String>, noAsistieron: List<String>) {
        require(asistieron.isNotEmpty() || noAsistieron.isNotEmpty()) { "Marca al menos una familia" }
        llamarApi { api.marcarAsistencia(actividadId, AsistenciaIn(asistieron, noAsistieron)) }
    }

    suspend fun campanasDeCentro(centroId: String): List<Campana> =
        llamarApi { api.campanasDeMiCentro(centroId) }.map { it.aModelo(host) }

    suspend fun proyectosDeCentro(centroId: String): List<Proyecto> =
        llamarApi { api.proyectosDeMiCentro(centroId) }.map { it.aModelo(host) }

    suspend fun activarCampana(id: String, activa: Boolean): Campana = editarCampana(id, CampanaPatch(activa = activa))

    suspend fun activarProyecto(id: String, activo: Boolean): Proyecto = editarProyecto(id, ProyectoPatch(activo = activo))

    suspend fun crearCampana(centroId: String, datos: CampanaIn): Campana =
        llamarApi { api.crearCampana(centroId, datos) }.aModelo(host)

    suspend fun editarCampana(id: String, datos: CampanaPatch): Campana =
        llamarApi { api.editarCampana(id, datos) }.aModelo(host)

    suspend fun crearProyecto(centroId: String, datos: ProyectoIn): Proyecto =
        llamarApi { api.crearProyecto(centroId, datos) }.aModelo(host)

    suspend fun editarProyecto(id: String, datos: ProyectoPatch): Proyecto =
        llamarApi { api.editarProyecto(id, datos) }.aModelo(host)

    suspend fun resumenAdmin(): ResumenAdmin = llamarApi { api.resumenAdmin() }.aModelo()

    suspend fun testimoniosPendientes(): List<Testimonio> =
        llamarApi { api.testimoniosPorModerar() }.items.map { it.aModelo(host) }

    suspend fun moderarTestimonio(id: String, aprobar: Boolean, motivo: String?) {
        llamarApi { api.moderarTestimonio(id, ModeracionIn(aprobar, motivo?.trim()?.ifBlank { null })) }
    }

    suspend fun comentariosPendientes(): List<Comentario> =
        llamarApi { api.comentariosPorModerar() }.items.map { it.aModelo() }

    suspend fun moderarComentario(id: String, aprobar: Boolean, motivo: String?) {
        llamarApi { api.moderarComentario(id, ModeracionIn(aprobar, motivo?.trim()?.ifBlank { null })) }
    }
}
