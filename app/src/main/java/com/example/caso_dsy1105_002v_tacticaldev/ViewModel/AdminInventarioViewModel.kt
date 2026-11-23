package com.example.caso_dsy1105_002v_tacticaldev.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_dsy1105_002v_tacticaldev.Database.AppDatabase
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminInventarioViewModel(application: Application) : AndroidViewModel(application) {
    private val repuestoDao = AppDatabase.getDatabase(application).repuestoDao()

    private val _inventario = MutableStateFlow<List<InventarioRepuesto>>(emptyList())
    val inventario: StateFlow<List<InventarioRepuesto>> = _inventario.asStateFlow()

    fun agregarInventario(item: InventarioRepuesto) {
        viewModelScope.launch {
            repuestoDao.actualizarInventario(item)
        }
    }
}