@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Repository

import com.example.caso_dsy1105_002v_tacticaldev.Database.UsuarioDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.Usuario
import com.example.caso_dsy1105_002v_tacticaldev.Network.RetrofitClient
import com.example.caso_dsy1105_002v_tacticaldev.Network.TacticalApiService
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(
    val usuarioDao: UsuarioDao,
    private val apiService: TacticalApiService = RetrofitClient.springBootService
) {

    val todosLosUsuarios: Flow<List<Usuario>> = usuarioDao.obtenerTodosLosUsuarios()

    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodosLosUsuarios()
    }

    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario? {
        return usuarioDao.obtenerUsuarioPorCorreo(correo)
    }

    suspend fun insertarUsuario(usuario: Usuario) {
        try {
            val response = apiService.insertarUsuario(usuario)
            if (response.isSuccessful) {
                usuarioDao.insertarUsuario(usuario)
                sincronizarConApi()
            } else {
                usuarioDao.insertarUsuario(usuario)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            usuarioDao.insertarUsuario(usuario)
        }
    }

    suspend fun eliminarUsuario(usuario: Usuario) {
        try {
            val response = apiService.eliminarUsuario(usuario.usuarioId)
            if (response.isSuccessful) {
                usuarioDao.eliminarUsuario(usuario)
                sincronizarConApi()
            } else {
                usuarioDao.eliminarUsuario(usuario)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            usuarioDao.eliminarUsuario(usuario)
        }
    }

    suspend fun sincronizarConApi() {
        try {
            val response = apiService.obtenerUsuarios()
            if (response.isSuccessful) {
                response.body()?.forEach { usuarioDao.insertarUsuario(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
