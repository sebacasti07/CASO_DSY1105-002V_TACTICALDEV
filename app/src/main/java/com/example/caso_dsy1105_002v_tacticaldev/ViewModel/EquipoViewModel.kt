package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import com.example.caso_dsy1105_002v_tacticaldev.Repository.EquipoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EquipoViewModel(private val repository: EquipoRepository) : ViewModel() {

    val listaEquipos: StateFlow<List<EquipoRefrigeracion>> = repository.obtenerTodosLosEquipos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun obtenerPorMinimarket(minimarketId: String) = repository.obtenerPorMinimarket(minimarketId)

    fun agregarEquipo(equipo: EquipoRefrigeracion) {
        viewModelScope.launch {
            repository.insertarEquipo(equipo)
        }
    }

    fun actualizarEstado(equipoId: String, estado: String) {
        viewModelScope.launch {
            repository.actualizarEstado(equipoId, estado)
        }
    }

    fun eliminarEquipo(equipo: EquipoRefrigeracion) {
        viewModelScope.launch {
            repository.eliminarEquipo(equipo)
        }
    }
}