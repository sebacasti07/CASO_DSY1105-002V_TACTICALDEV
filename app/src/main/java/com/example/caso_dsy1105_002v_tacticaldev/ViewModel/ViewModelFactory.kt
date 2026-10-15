@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.caso_dsy1105_002v_tacticaldev.Database.UsuarioDao
import com.example.caso_dsy1105_002v_tacticaldev.Repository.EquipoRepository
import com.example.caso_dsy1105_002v_tacticaldev.Repository.RepuestoRepository
import com.example.caso_dsy1105_002v_tacticaldev.Repository.UsuarioRepository

class ViewModelFactory(
    private val usuarioDao: UsuarioDao? = null,
    private val repuestoRepository: RepuestoRepository? = null,
    private val equipoRepository: EquipoRepository? = null,
    private val usuarioRepository: UsuarioRepository? = null
) : ViewModelProvider.Factory {

    constructor(
        usuarioRepository: UsuarioRepository,
        repuestoRepository: RepuestoRepository? = null,
        equipoRepository: EquipoRepository? = null
    ) : this(
        usuarioDao = usuarioRepository.usuarioDao,
        repuestoRepository = repuestoRepository,
        equipoRepository = equipoRepository,
        usuarioRepository = usuarioRepository
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val dao = usuarioDao ?: usuarioRepository?.usuarioDao
            requireNotNull(dao) { "usuarioDao o usuarioRepository es requerido para AuthViewModel" }
            return AuthViewModel(dao) as T
        }
        if (modelClass.isAssignableFrom(RepuestoViewModel::class.java)) {
            requireNotNull(repuestoRepository) { "repuestoRepository es requerido para RepuestoViewModel" }
            return RepuestoViewModel(repuestoRepository) as T
        }
        if (modelClass.isAssignableFrom(EquipoViewModel::class.java)) {
            requireNotNull(equipoRepository) { "equipoRepository es requerido para EquipoViewModel" }
            return EquipoViewModel(equipoRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}