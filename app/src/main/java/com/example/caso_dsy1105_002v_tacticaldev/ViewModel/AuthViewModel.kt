@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_dsy1105_002v_tacticaldev.Database.UsuarioDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val usuario: Usuario) : AuthUiState()
    data class Error(val mensaje: String) : AuthUiState()
}

class AuthViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var usuarioActual: Usuario? = null
        private set

    fun iniciarSesion(correo: String, contrasenaIngresada: String) {
        val correoLimpio = correo.trim().lowercase()
        val passLimpia = contrasenaIngresada.trim()

        if (correoLimpio.isBlank() || passLimpia.isBlank()) {
            _uiState.value = AuthUiState.Error("Ingresa tu correo y contraseña.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                var usuarioEncontrado = withContext(Dispatchers.IO) {
                    usuarioDao.obtenerUsuarioPorCorreo(correoLimpio)
                }

                // Autocreación de respaldo si intenta ingresar el Administrador principal
                if (usuarioEncontrado == null && correoLimpio == "admin@admindev.cl") {
                    val adminDefault = Usuario(
                        usuarioId = "USR-ADMIN-001",
                        rut = "11111111-1",
                        primerNombre = "Carlos",
                        segundoNombre = "Andrés",
                        apellido = "Mendoza",
                        correo = "admin@admindev.cl",
                        telefono = "911112222",
                        contrasena = "Admin1234@",
                        rol = Rol.ADMINISTRADOR
                    )
                    withContext(Dispatchers.IO) {
                        usuarioDao.insertarUsuario(adminDefault)
                    }
                    usuarioEncontrado = adminDefault
                }

                if (usuarioEncontrado != null) {
                    if (usuarioEncontrado.contrasena == passLimpia) {
                        usuarioActual = usuarioEncontrado
                        _uiState.value = AuthUiState.Success(usuarioEncontrado)
                    } else {
                        _uiState.value = AuthUiState.Error("Contraseña incorrecta.")
                    }
                } else {
                    _uiState.value = AuthUiState.Error("No existe un usuario registrado con este correo.")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Error en base de datos Room: ${e.localizedMessage}")
            }
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        _uiState.value = AuthUiState.Idle
    }
    fun limpiarEstado() {
        _uiState.value = AuthUiState.Idle
    }


}