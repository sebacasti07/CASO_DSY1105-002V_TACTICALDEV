package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_dsy1105_002v_tacticaldev.Database.UsuarioDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Util.SecurityUtils
import com.example.caso_dsy1105_002v_tacticaldev.Util.ValidationUtils
import kotlinx.coroutines.CoroutineDispatcher
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

class AuthViewModel(
    private val usuarioDao: UsuarioDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var usuarioActual: Usuario? = null
        private set

    fun iniciarSesion(correo: String, contrasenaIngresada: String) {
        val correoLimpio = correo.trim().lowercase()
        val passLimpia = contrasenaIngresada.trim()

        val errorCorreo = ValidationUtils.validarCorreoLogin(correoLimpio)
        if (errorCorreo != null) {
            _uiState.value = AuthUiState.Error(errorCorreo)
            return
        }

        if (passLimpia.isBlank()) {
            _uiState.value = AuthUiState.Error("Ingresa tu contraseña.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val usuarioEncontrado = withContext(ioDispatcher) {
                    usuarioDao.obtenerUsuarioPorCorreo(correoLimpio)
                }

                if (usuarioEncontrado != null) {
                    val passHash = SecurityUtils.hashPassword(passLimpia)
                    if (usuarioEncontrado.contrasena == passHash) {
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

    fun cambiarContrasena(contrasenaActual: String, nuevaContrasena: String, onResultado: (Boolean, String) -> Unit) {
        val usuario = usuarioActual
        if (usuario == null) {
            onResultado(false, "No hay usuario activo.")
            return
        }

        val passActualHash = SecurityUtils.hashPassword(contrasenaActual.trim())
        if (usuario.contrasena != passActualHash) {
            onResultado(false, "La contraseña actual no es correcta.")
            return
        }

        val errorNueva = ValidationUtils.validarContrasena(nuevaContrasena)
        if (errorNueva != null) {
            onResultado(false, errorNueva)
            return
        }

        viewModelScope.launch {
            try {
                val nuevoHash = SecurityUtils.hashPassword(nuevaContrasena.trim())
                val usuarioActualizado = usuario.copy(contrasena = nuevoHash)
                withContext(ioDispatcher) {
                    usuarioDao.actualizarUsuario(usuarioActualizado)
                }
                usuarioActual = usuarioActualizado
                onResultado(true, "Contraseña actualizada exitosamente.")
            } catch (e: Exception) {
                onResultado(false, "Error al actualizar contraseña: ${e.localizedMessage}")
            }
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        _uiState.value = AuthUiState.Idle
    }
}
