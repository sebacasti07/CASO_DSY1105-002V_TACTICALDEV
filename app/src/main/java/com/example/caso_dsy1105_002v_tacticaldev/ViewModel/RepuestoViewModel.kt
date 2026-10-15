package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Repuesto
import com.example.caso_dsy1105_002v_tacticaldev.Repository.RepuestoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RepuestoViewModel(private val repository: RepuestoRepository) : ViewModel() {

    val listaInventario: StateFlow<List<InventarioRepuesto>> = repository.obtenerTodosLosInventarios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val listaRepuestos: StateFlow<List<InventarioRepuesto>> = repository.obtenerRepuestos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun obtenerStock() = repository.obtenerStock()

    fun obtenerStock(param: String) = repository.obtenerStock(param)

    fun agregarRepuesto(inventario: InventarioRepuesto) {
        viewModelScope.launch {
            repository.insertarInventario(inventario)
        }
    }

    fun agregarRepuesto(repuesto: Repuesto) {
        viewModelScope.launch {
            repository.insertarRepuesto(repuesto)
        }
    }

    fun insertarRepuesto(inventario: InventarioRepuesto) {
        viewModelScope.launch {
            repository.insertarInventario(inventario)
        }
    }

    fun insertarRepuesto(repuesto: Repuesto) {
        viewModelScope.launch {
            repository.insertarRepuesto(repuesto)
        }
    }

    fun actualizarStock(inventario: InventarioRepuesto) {
        viewModelScope.launch {
            repository.actualizarStock(inventario)
        }
    }

    fun actualizarStock(id: String, nuevoStock: Int) {
        viewModelScope.launch {
            repository.actualizarStock(id, nuevoStock)
        }
    }

    fun actualizarStock(id: String, nuevoStock: Any) {
        viewModelScope.launch {
            repository.actualizarStock(id, nuevoStock)
        }
    }

    fun eliminarRepuesto(inventario: InventarioRepuesto) {
        viewModelScope.launch {
            repository.eliminarInventario(inventario)
        }
    }
}