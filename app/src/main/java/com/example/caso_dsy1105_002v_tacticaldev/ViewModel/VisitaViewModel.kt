@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import com.example.caso_dsy1105_002v_tacticaldev.Repository.VisitaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VisitaViewModel(private val repository: VisitaRepository) : ViewModel() {

    val todasLasVisitas: StateFlow<List<Visita>> = repository.obtenerTodasLasVisitas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun obtenerVisitasPorMinimarket(minimarketId: String): Flow<List<Visita>> {
        return repository.obtenerVisitasPorMinimarket(minimarketId)
    }

    fun registrarVisitaTerreno(visita: Visita) {
        viewModelScope.launch {
            repository.insertarVisita(visita)
        }
    }

    fun insertarVisita(visita: Visita) {
        viewModelScope.launch {
            repository.insertarVisita(visita)
        }
    }

    fun actualizarVisita(visita: Visita) {
        viewModelScope.launch {
            repository.actualizarVisita(visita)
        }
    }

    fun eliminarVisita(visita: Visita) {
        viewModelScope.launch {
            repository.eliminarVisita(visita)
        }
    }
}