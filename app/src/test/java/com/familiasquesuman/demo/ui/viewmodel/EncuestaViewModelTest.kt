package com.familiasquesuman.demo.ui.viewmodel

import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.model.Pregunta
import com.familiasquesuman.demo.ui.UiState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EncuestaViewModelTest {

    private val repo = mockk<FamiliasRepository>(relaxed = true)
    private val preguntas = listOf(
        Pregunta("animo", "¿Qué tan emocionada está la familia? (1–5)", "escala"),
        Pregunta("expectativa", "¿Qué esperan vivir?", "texto"),
        Pregunta("primera_vez", "¿Es la primera vez?", "si_no")
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { repo.obtenerPreguntas("antes") } returns preguntas
    }

    @After
    fun limpiar() = Dispatchers.resetMain()

    @Test
    fun `carga las preguntas del momento`() = runTest {
        val vm = EncuestaViewModel(repo, "insc-1", "antes")
        val estado = vm.estado.value.preguntas
        assertTrue(estado is UiState.Datos && estado.datos.size == 3)
    }

    @Test
    fun `no envia si falta una respuesta`() = runTest {
        val vm = EncuestaViewModel(repo, "insc-1", "antes")
        vm.responder("animo", "4")
        vm.enviar()
        assertEquals("Falta responder: ¿Qué esperan vivir?", vm.estado.value.error)
        assertFalse(vm.estado.value.enviada)
        coVerify(exactly = 0) { repo.responderEncuesta(any(), any(), any()) }
    }

    @Test
    fun `envia las respuestas con los tipos correctos`() = runTest {
        val enviadas = slot<Map<String, JsonElement>>()
        coEvery { repo.responderEncuesta("insc-1", "antes", capture(enviadas)) } just Runs
        val vm = EncuestaViewModel(repo, "insc-1", "antes")
        vm.responder("animo", "5")
        vm.responder("expectativa", "Convivir en familia")
        vm.responder("primera_vez", "si")
        vm.enviar()
        assertTrue(vm.estado.value.enviada)
        assertEquals(JsonPrimitive(5), enviadas.captured["animo"])
        assertEquals(JsonPrimitive("Convivir en familia"), enviadas.captured["expectativa"])
        assertEquals(JsonPrimitive("si"), enviadas.captured["primera_vez"])
    }

    @Test
    fun `muestra el error si el servidor falla`() = runTest {
        coEvery { repo.responderEncuesta(any(), any(), any()) } throws IllegalStateException("Esta encuesta ya fue respondida")
        val vm = EncuestaViewModel(repo, "insc-1", "antes")
        preguntas.forEach { vm.responder(it.clave, "3") }
        vm.enviar()
        assertEquals("Esta encuesta ya fue respondida", vm.estado.value.error)
        assertFalse(vm.estado.value.enviada)
    }
}
