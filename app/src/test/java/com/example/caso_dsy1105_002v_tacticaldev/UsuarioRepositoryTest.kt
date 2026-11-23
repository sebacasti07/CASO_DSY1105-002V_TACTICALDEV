package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeUsuarioDao
    private lateinit var fakeApiService: FakeTacticalApiService
    private lateinit var repository: UsuarioRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeUsuarioDao()
        fakeApiService = FakeTacticalApiService()
        repository = UsuarioRepository(fakeDao, fakeApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInsertarYObtenerUsuarioPorCorreo() = runTest(testDispatcher) {
        val usuario = Usuario(
            usuarioId = "U-1",
            rut = "12345678-9",
            primerNombre = "Juan",
            segundoNombre = "Carlos",
            apellido = "Perez",
            correo = "juan@test.cl",
            telefono = "912345678",
            contrasena = "Pass1234",
            rol = Rol.CLIENTE
        )

        repository.insertarUsuario(usuario)
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = repository.obtenerUsuarioPorCorreo("juan@test.cl")
        assertNotNull(obtenido)
        assertEquals("Juan", obtenido?.primerNombre)
        assertEquals(1, fakeApiService.usuarios.size)
    }

    @Test
    fun testEliminarUsuario() = runTest(testDispatcher) {
        val usuario = Usuario(
            usuarioId = "U-2",
            rut = "98765432-1",
            primerNombre = "Maria",
            apellido = "Gomez",
            correo = "eliminar@test.cl",
            telefono = "987654321",
            contrasena = "Pass1234",
            rol = Rol.TECNICO
        )
        repository.insertarUsuario(usuario)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.eliminarUsuario(usuario)
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = repository.obtenerUsuarioPorCorreo("eliminar@test.cl")
        assertNull(obtenido)
        assertEquals(0, fakeApiService.usuarios.size)
    }

    @Test
    fun testSincronizarConApi() = runTest(testDispatcher) {
        val apiUser = Usuario(
            usuarioId = "U-API-1",
            rut = "11111111-1",
            primerNombre = "Pedro",
            apellido = "Rojas",
            correo = "api@test.cl",
            telefono = "911111111",
            contrasena = "Pass1234",
            rol = Rol.VENDEDOR
        )
        fakeApiService.usuarios.add(apiUser)

        repository.sincronizarConApi()
        testDispatcher.scheduler.advanceUntilIdle()

        val todos = repository.obtenerTodosLosUsuarios().first()
        assertEquals(1, todos.size)
        assertEquals("Pedro", todos[0].primerNombre)
    }
}