package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Database.EquipoDao
import com.example.caso_dsy1105_002v_tacticaldev.Database.RepuestoDao
import com.example.caso_dsy1105_002v_tacticaldev.Database.VisitaDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeEquipoDao : EquipoDao {
    val items = MutableStateFlow<List<EquipoRefrigeracion>>(emptyList())

    override suspend fun insertarEquipo(equipo: EquipoRefrigeracion) {
        val list = items.value.toMutableList()
        list.removeAll { it.equipoId == equipo.equipoId }
        list.add(equipo)
        items.value = list
    }

    override suspend fun actualizarEquipo(equipo: EquipoRefrigeracion) {
        insertarEquipo(equipo)
    }

    override suspend fun eliminarEquipo(equipo: EquipoRefrigeracion) {
        val list = items.value.toMutableList()
        list.removeAll { it.equipoId == equipo.equipoId }
        items.value = list
    }

    override fun obtenerPorMinimarket(minimarketId: String): Flow<List<EquipoRefrigeracion>> {
        return items.map { list -> list.filter { it.minimarketId == minimarketId } }
    }

    override fun obtenerTodosLosEquipos(): Flow<List<EquipoRefrigeracion>> {
        return items
    }

    override suspend fun obtenerPorCodigo(codigo: String): EquipoRefrigeracion? {
        return items.value.find { it.equipoId == codigo }
    }

    override suspend fun actualizarEstado(equipoId: String, estado: String) {
        val item = obtenerPorCodigo(equipoId)
        if (item != null) {
            actualizarEquipo(item.copy(estadoOperativo = estado))
        }
    }
}

class FakeRepuestoDao : RepuestoDao {
    val items = MutableStateFlow<List<InventarioRepuesto>>(emptyList())

    override suspend fun insertarInventario(inventario: InventarioRepuesto) {
        val list = items.value.toMutableList()
        list.removeAll { it.inventarioId == inventario.inventarioId }
        list.add(inventario)
        items.value = list
    }

    override suspend fun actualizarInventario(inventario: InventarioRepuesto) {
        insertarInventario(inventario)
    }

    override suspend fun eliminarInventario(inventario: InventarioRepuesto) {
        val list = items.value.toMutableList()
        list.removeAll { it.inventarioId == inventario.inventarioId }
        items.value = list
    }

    override fun obtenerTodosLosInventarios(): Flow<List<InventarioRepuesto>> {
        return items
    }

    override suspend fun obtenerPorId(id: String): InventarioRepuesto? {
        return items.value.find { it.inventarioId == id || it.repuestoId == id }
    }
}

class FakeVisitaDao : VisitaDao {
    val items = MutableStateFlow<List<Visita>>(emptyList())

    override suspend fun insertarVisita(visita: Visita) {
        val list = items.value.toMutableList()
        list.removeAll { it.visitaId == visita.visitaId }
        list.add(visita)
        items.value = list
    }

    override suspend fun actualizarVisita(visita: Visita) {
        insertarVisita(visita)
    }

    override suspend fun eliminarVisita(visita: Visita) {
        val list = items.value.toMutableList()
        list.removeAll { it.visitaId == visita.visitaId }
        items.value = list
    }

    override fun obtenerTodasLasVisitas(): Flow<List<Visita>> {
        return items
    }

    override fun obtenerVisitasPorMinimarket(minimarketId: String): Flow<List<Visita>> {
        return items.map { list -> list.filter { it.minimarketId == minimarketId } }
    }

    override suspend fun obtenerVisitaPorId(visitaId: String): Visita? {
        return items.value.find { it.visitaId == visitaId }
    }
}