package com.familiasquesuman.demo.data

import com.familiasquesuman.demo.data.remote.ApiException
import com.familiasquesuman.demo.data.remote.llamarApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class LlamarApiTest {

    private fun httpError(codigo: Int, cuerpo: String) =
        HttpException(Response.error<Any>(codigo, cuerpo.toResponseBody("application/json".toMediaType())))

    private suspend fun errorDe(bloque: suspend () -> Unit): ApiException {
        try {
            bloque()
        } catch (e: ApiException) {
            return e
        }
        fail("Se esperaba ApiException")
        throw IllegalStateException()
    }

    @Test
    fun `usa el mensaje del servidor cuando viene en el cuerpo`() = runTest {
        val e = errorDe { llamarApi<Unit> { throw httpError(409, """{"error":{"codigo":"conflicto","mensaje":"Esta encuesta ya fue respondida"}}""") } }
        assertEquals(409, e.status)
        assertEquals("Esta encuesta ya fue respondida", e.message)
    }

    @Test
    fun `traduce codigos conocidos sin cuerpo`() = runTest {
        assertEquals("Tu sesión expiró. Inicia sesión de nuevo.", errorDe { llamarApi<Unit> { throw httpError(401, "") } }.message)
        assertEquals("No tienes permiso para hacer esto.", errorDe { llamarApi<Unit> { throw httpError(403, "no-json") } }.message)
        assertEquals("No encontramos lo que buscas.", errorDe { llamarApi<Unit> { throw httpError(404, "") } }.message)
    }

    @Test
    fun `sin red regresa status 0`() = runTest {
        val e = errorDe { llamarApi<Unit> { throw IOException("timeout") } }
        assertEquals(0, e.status)
        assertEquals("Sin conexión. Revisa tu internet e inténtalo de nuevo.", e.message)
    }

    @Test
    fun `devuelve el resultado cuando no hay error`() = runTest {
        assertEquals(42, llamarApi { 42 })
    }
}
