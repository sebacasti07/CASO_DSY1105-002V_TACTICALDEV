package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Repository.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminUsuariosViewModel(private val repository: UsuarioRepository) : ViewModel() {

    val usuarios: StateFlow<List<Usuario>> = repository.todosLosUsuarios
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.insertarUsuario(usuario)
        }
    }

    fun eliminarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.eliminarUsuario(usuario)
        }
    }
}