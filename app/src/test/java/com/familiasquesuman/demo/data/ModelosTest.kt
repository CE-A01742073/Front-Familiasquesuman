package com.familiasquesuman.demo.data

import com.familiasquesuman.demo.model.Ciudad
import com.familiasquesuman.demo.model.EncuestaRespondida
import com.familiasquesuman.demo.model.Inscripcion
import com.familiasquesuman.demo.model.fechaBonita
import com.familiasquesuman.demo.model.horaCorta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ModelosTest {

    private fun inscripcion(fecha: String, estado: String = "confirmada") = Inscripcion(
        id = "i", actividadId = "a", actividadTitulo = "T", centroId = "c", centroNombre = "C",
        fecha = fecha, horaInicio = "10:00:00", horaFin = null, lugar = "L", nombres = emptyList(), comentario = "", estado = estado
    )

    @Test
    fun `fechaBonita muestra dia y mes en espanol`() {
        val texto = fechaBonita("2026-10-20")
        assertTrue(texto, texto.startsWith("Mar") && texto.endsWith("20 de octubre"))
    }

    @Test
    fun `fechaBonita devuelve el texto original si no es una fecha`() {
        assertEquals("pronto", fechaBonita("pronto"))
    }

    @Test
    fun `horaCorta recorta los segundos`() {
        assertEquals("09:30", horaCorta("09:30:00"))
    }

    @Test
    fun `inscripcion pasada por fecha o por estado`() {
        val ayer = LocalDate.now().minusDays(1).toString()
        val manana = LocalDate.now().plusDays(1).toString()
        assertTrue(inscripcion(ayer).pasada)
        assertFalse(inscripcion(manana).pasada)
        assertTrue(inscripcion(manana, estado = "asistio").pasada)
        assertTrue(inscripcion(manana, estado = "pendiente").pendiente)
    }

    @Test
    fun `ciudad desde clave desconocida regresa Monterrey`() {
        assertEquals(Ciudad.HERMOSILLO, Ciudad.desdeClave("hermosillo"))
        assertEquals(Ciudad.MONTERREY, Ciudad.desdeClave("cdmx"))
        assertEquals(Ciudad.MONTERREY, Ciudad.desdeClave(null))
    }

    @Test
    fun `encuesta respondida arma titulo y fecha`() {
        val e = EncuestaRespondida("despues", "2026-10-20T15:04:05+00:00", emptyList())
        assertEquals("Después de la actividad", e.titulo)
        assertTrue(e.fechaTexto, e.fechaTexto.endsWith("20 de octubre"))
    }
}
