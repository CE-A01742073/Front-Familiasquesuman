package com.familiasquesuman.demo.data

import com.familiasquesuman.demo.data.remote.ActividadDto
import com.familiasquesuman.demo.data.remote.ActividadResumenDto
import com.familiasquesuman.demo.data.remote.AsistenteDto
import com.familiasquesuman.demo.data.remote.InscripcionDto
import com.familiasquesuman.demo.data.remote.UsuarioDto
import com.familiasquesuman.demo.model.Categoria
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapeosTest {
    private val host = "https://api.familiasquesuman.dev"

    @Test
    fun `urlAbsoluta antepone el host a rutas relativas y respeta las absolutas`() {
        assertEquals("$host/api/v1/archivos/perfiles/a.jpg", urlAbsoluta(host, "/api/v1/archivos/perfiles/a.jpg"))
        assertEquals("https://cdn.x/y.jpg", urlAbsoluta(host, "https://cdn.x/y.jpg"))
        assertNull(urlAbsoluta(host, null))
    }

    @Test
    fun `actividad se mapea con categoria conocida e imagen absoluta`() {
        val dto = ActividadDto(
            id = "a1", centroId = "c1", centroNombre = "Regalando Estrellas", titulo = "Visita al hospital",
            categoria = "ninos", fecha = "2026-11-02", horaInicio = "10:00:00", horaFin = "12:00:00",
            cuposTotales = 20, cuposOcupados = 5, cuposDisponibles = 15, imagenUrl = "/api/v1/archivos/actividades/x.jpg"
        )
        val a = dto.aModelo(host)
        assertEquals(Categoria.NINOS, a.categoria)
        assertEquals("10:00–12:00", a.hora)
        assertEquals("$host/api/v1/archivos/actividades/x.jpg", a.imagenUrl)
        assertTrue(a.activa)
    }

    @Test
    fun `categoria desconocida cae en comunidad`() {
        val a = ActividadDto(id = "a2", centroId = "c1", titulo = "X", categoria = "otra", fecha = "2026-11-02").aModelo(host)
        assertEquals(Categoria.COMUNIDAD, a.categoria)
    }

    @Test
    fun `inscripcion sin actividad usa valores por defecto`() {
        val i = InscripcionDto(id = "i1", actividadId = "a1", asistentes = listOf(AsistenteDto(nombre = "Ana", edad = 9))).aModelo()
        assertEquals("Actividad", i.actividadTitulo)
        assertEquals(listOf("Ana"), i.nombres)
        assertEquals("", i.comentario)
        assertFalse(i.encuestaAntes)
    }

    @Test
    fun `inscripcion con actividad copia los datos del resumen`() {
        val i = InscripcionDto(
            id = "i2", actividadId = "a1", estado = "confirmada", encuestaAntes = true,
            actividad = ActividadResumenDto(id = "a1", titulo = "Taller", fecha = "2026-12-01", horaInicio = "09:00:00", centroId = "c1", centroNombre = "Cíclica")
        ).aModelo()
        assertEquals("Taller", i.actividadTitulo)
        assertEquals("Cíclica", i.centroNombre)
        assertTrue(i.encuestaAntes)
    }

    @Test
    fun `usuario expone rol y foto`() {
        val u = UsuarioDto(id = "u1", email = "a@b.c", nombre = "Chris", rol = "admin", fotoUrl = "/api/v1/archivos/perfiles/p.jpg").aModelo(host)
        assertTrue(u.esAdmin)
        assertTrue(u.gestiona)
        assertFalse(u.esFamilia)
        assertEquals("$host/api/v1/archivos/perfiles/p.jpg", u.fotoUrl)
    }

    @Test
    fun `respuestaTexto traduce escala, si_no y texto`() {
        assertEquals("4 de 5", respuestaTexto(JsonPrimitive(4), "escala"))
        assertEquals("Sí", respuestaTexto(JsonPrimitive("si"), "si_no"))
        assertEquals("No", respuestaTexto(JsonPrimitive("no"), "si_no"))
        assertEquals("Muy bonito", respuestaTexto(JsonPrimitive("Muy bonito"), "texto"))
        assertEquals("7", respuestaTexto(JsonPrimitive(7), null))
    }
}
