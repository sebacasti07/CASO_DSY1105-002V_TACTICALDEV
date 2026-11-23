package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Repository.UsuarioRepository
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AdminUsuariosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminUsuariosViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeUsuarioDao
    private lateinit var fakeApiService: FakeTacticalApiService
    private lateinit var repository: UsuarioRepository
    private lateinit var viewModel: AdminUsuariosViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeUsuarioDao()
        fakeApiService = FakeTacticalApiService()
        repository = UsuarioRepository(fakeDao, fakeApiService)
        viewModel = AdminUsuariosViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAgregarUsuario() = runTest(testDispatcher) {
        val usuario = Usuario(
            usuarioId = "U-ADM-1",
            rut = "12345678-9",
            primerNombre = "Carlos",
            apellido = "Lopez",
            correo = "carlos@test.cl",
            telefono = "911223344",
            contrasena = "Pass1234",
            rol = Rol.ADMINISTRADOR
        )

        viewModel.agregarUsuario(usuario)
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = fakeDao.obtenerUsuarioPorCorreo("carlos@test.cl")
        assertEquals("Carlos", obtenido?.primerNombre)
    }

    @Test
    fun testEliminarUsuario() = runTest(testDispatcher) {
        val usuario = Usuario(
            usuarioId = "U-ADM-2",
            rut = "98765432-1",
            primerNombre = "Ana",
            apellido = "Diaz",
            correo = "ana@test.cl",
            telefono = "955667788",
            contrasena = "Pass1234",
            rol = Rol.CLIENTE
        )
        viewModel.agregarUsuario(usuario)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.eliminarUsuario(usuario)
        testDispatcher.scheduler.advanceUntilIdle()

        val obtenido = fakeDao.obtenerUsuarioPorCorreo("ana@test.cl")
        assertEquals(null, obtenido)
    }
}