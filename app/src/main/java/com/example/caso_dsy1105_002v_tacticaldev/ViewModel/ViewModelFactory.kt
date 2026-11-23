@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.caso_dsy1105_002v_tacticaldev.Database.UsuarioDao
import com.example.caso_dsy1105_002v_tacticaldev.Repository.EquipoRepository
import com.example.caso_dsy1105_002v_tacticaldev.Repository.RepuestoRepository
import com.example.caso_dsy1105_002v_tacticaldev.Repository.UsuarioRepository
import com.example.caso_dsy1105_002v_tacticaldev.Repository.VisitaRepository

class ViewModelFactory(
    private val usuarioDao: UsuarioDao? = null,
    private val repuestoRepository: RepuestoRepository? = null,
    private val equipoRepository: EquipoRepository? = null,
    private val usuarioRepository: UsuarioRepository? = null,
    private val visitaRepository: VisitaRepository? = null
) : ViewModelProvider.Factory {

    constructor(
        usuarioRepository: UsuarioRepository,
        repuestoRepository: RepuestoRepository? = null,
        equipoRepository: EquipoRepository? = null,
        visitaRepository: VisitaRepository? = null
    ) : this(
        usuarioDao = usuarioRepository.usuarioDao,
        repuestoRepository = repuestoRepository,
        equipoRepository = equipoRepository,
        usuarioRepository = usuarioRepository,
        visitaRepository = visitaRepository
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
        if (modelClass.isAssignableFrom(AdminUsuariosViewModel::class.java)) {
            requireNotNull(usuarioRepository) { "usuarioRepository es requerido para AdminUsuariosViewModel" }
            return AdminUsuariosViewModel(usuarioRepository) as T
        }
        if (modelClass.isAssignableFrom(VisitaViewModel::class.java)) {
            requireNotNull(visitaRepository) { "visitaRepository es requerido para VisitaViewModel" }
            return VisitaViewModel(visitaRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}