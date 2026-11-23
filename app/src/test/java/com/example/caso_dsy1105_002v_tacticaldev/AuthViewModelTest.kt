package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Database.UsuarioDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Util.SecurityUtils
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthUiState
import com.example.caso_dsy1105_002v_tacticaldev.ViewModel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeUsuarioDao : UsuarioDao {
    private val usuarios = mutableMapOf<String, Usuario>()

    override suspend fun insertarUsuario(usuario: Usuario) {
        usuarios[usuario.correo] = usuario
    }

    override suspend fun actualizarUsuario(usuario: Usuario) {
        usuarios[usuario.correo] = usuario
    }

    override suspend fun eliminarUsuario(usuario: Usuario) {
        usuarios.remove(usuario.correo)
    }

    override fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> {
        return flowOf(usuarios.values.toList())
    }

    override suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario? {
        return usuarios[correo]
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testDao: FakeUsuarioDao
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        testDao = FakeUsuarioDao()
        viewModel = AuthViewModel(testDao, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoginSuccess() = runTest {
        val usuario = Usuario(
            usuarioId = "1",
            rut = "11111111-1",
            primerNombre = "Admin",
            apellido = "Test",
            correo = "admin@admindev.cl",
            telefono = "911112222",
            contrasena = SecurityUtils.hashPassword("Admin1234@"),
            rol = Rol.ADMINISTRADOR
        )
        testDao.insertarUsuario(usuario)

        viewModel.iniciarSesion("admin@admindev.cl", "Admin1234@")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AuthUiState.Success)
        assertEquals(usuario, viewModel.usuarioActual)
    }

    @Test
    fun testLoginIncorrectPassword() = runTest {
        val usuario = Usuario(
            usuarioId = "1",
            rut = "11111111-1",
            primerNombre = "Admin",
            apellido = "Test",
            correo = "admin@admindev.cl",
            telefono = "911112222",
            contrasena = SecurityUtils.hashPassword("Admin1234@"),
            rol = Rol.ADMINISTRADOR
        )
        testDao.insertarUsuario(usuario)

        viewModel.iniciarSesion("admin@admindev.cl", "WrongPass1@")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun testLoginUserNotFound() = runTest {
        viewModel.iniciarSesion("noexiste@admindev.cl", "Admin1234@")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun testCambiarContrasena() = runTest {
        val usuario = Usuario(
            usuarioId = "1",
            rut = "11111111-1",
            primerNombre = "Admin",
            apellido = "Test",
            correo = "admin@admindev.cl",
            telefono = "911112222",
            contrasena = SecurityUtils.hashPassword("Admin1234@"),
            rol = Rol.ADMINISTRADOR
        )
        testDao.insertarUsuario(usuario)
        viewModel.iniciarSesion("admin@admindev.cl", "Admin1234@")
        testDispatcher.scheduler.advanceUntilIdle()

        var resultadoExito = false
        viewModel.cambiarContrasena("Admin1234@", "NewAdmin1234@") { success, _ ->
            resultadoExito = success
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(resultadoExito)
        assertEquals(SecurityUtils.hashPassword("NewAdmin1234@"), viewModel.usuarioActual?.contrasena)
    }
}
