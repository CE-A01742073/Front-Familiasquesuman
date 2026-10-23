package com.familiasquesuman.demo.ui.viewmodel

import com.familiasquesuman.demo.data.FamiliasRepository
import com.familiasquesuman.demo.data.remote.ApiException
import com.familiasquesuman.demo.model.Ciudad
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModelTest {

    private val repo = mockk<FamiliasRepository>(relaxed = true)

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repo.ciudad } returns MutableStateFlow(Ciudad.MONTERREY)
    }

    @After
    fun limpiar() = Dispatchers.resetMain()

    @Test
    fun `login correcto deja la sesion lista`() = runTest {
        coEvery { repo.iniciarSesion("ana@fqs.mx", "clave-segura-123") } just Runs
        val vm = SesionViewModel(repo)
        vm.cambiarCorreo("ana@fqs.mx")
        vm.cambiarPassword("clave-segura-123")
        vm.iniciarSesion()
        assertTrue(vm.sesionLista.value)
        assertNull(vm.formulario.value.error)
    }

    @Test
    fun `login fallido muestra el mensaje del servidor`() = runTest {
        coEvery { repo.iniciarSesion(any(), any()) } throws ApiException(401, "Correo o contraseña incorrectos")
        val vm = SesionViewModel(repo)
        vm.cambiarCorreo("ana@fqs.mx")
        vm.cambiarPassword("mala")
        vm.iniciarSesion()
        assertFalse(vm.sesionLista.value)
        assertEquals("Correo o contraseña incorrectos", vm.formulario.value.error)
        assertFalse(vm.formulario.value.cargando)
    }

    @Test
    fun `escribir en un campo borra el error anterior`() = runTest {
        coEvery { repo.iniciarSesion(any(), any()) } throws IllegalArgumentException("Escribe tu contraseña")
        val vm = SesionViewModel(repo)
        vm.iniciarSesion()
        assertEquals("Escribe tu contraseña", vm.formulario.value.error)
        vm.cambiarPassword("x")
        assertNull(vm.formulario.value.error)
    }

    @Test
    fun `google con cuenta nueva pide el nombre de la familia y luego entra`() = runTest {
        coEvery { repo.iniciarSesionGoogle("token-google", null) } throws ApiException(422, "Falta nombre de la familia o ciudad")
        coEvery { repo.iniciarSesionGoogle("token-google", "Familia Evans") } just Runs
        val vm = SesionViewModel(repo)

        vm.iniciarSesionGoogle("token-google")
        assertTrue(vm.formulario.value.pedirFamiliaGoogle)
        assertFalse(vm.sesionLista.value)
        assertNull(vm.formulario.value.error)

        vm.cambiarNombreFamilia("Familia Evans")
        vm.confirmarFamiliaGoogle()
        assertTrue(vm.sesionLista.value)
        assertFalse(vm.formulario.value.pedirFamiliaGoogle)
        coVerify { repo.iniciarSesionGoogle("token-google", "Familia Evans") }
    }

    @Test
    fun `google con otro error lo muestra sin pedir familia`() = runTest {
        coEvery { repo.iniciarSesionGoogle(any(), null) } throws ApiException(401, "Token de Google inválido")
        val vm = SesionViewModel(repo)
        vm.iniciarSesionGoogle("token-malo")
        assertFalse(vm.formulario.value.pedirFamiliaGoogle)
        assertEquals("Token de Google inválido", vm.formulario.value.error)
    }

    @Test
    fun `cancelar el dialogo de google limpia el estado`() = runTest {
        coEvery { repo.iniciarSesionGoogle(any(), null) } throws ApiException(422, "Falta nombre de la familia o ciudad")
        val vm = SesionViewModel(repo)
        vm.iniciarSesionGoogle("t")
        vm.cancelarFamiliaGoogle()
        assertFalse(vm.formulario.value.pedirFamiliaGoogle)
        vm.confirmarFamiliaGoogle()
        coVerify(exactly = 1) { repo.iniciarSesionGoogle(any(), any()) }
        assertFalse(vm.sesionLista.value)
    }
}
