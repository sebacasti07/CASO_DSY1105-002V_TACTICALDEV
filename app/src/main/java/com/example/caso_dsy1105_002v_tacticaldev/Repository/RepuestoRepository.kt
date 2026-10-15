@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Repository

import com.example.caso_dsy1105_002v_tacticaldev.Database.RepuestoDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Repuesto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepuestoRepository(private val repuestoDao: RepuestoDao) {

    val todosLosInventarios: Flow<List<InventarioRepuesto>> = repuestoDao.obtenerTodosLosInventarios()

    fun obtenerRepuestos(): Flow<List<InventarioRepuesto>> {
        return repuestoDao.obtenerTodosLosInventarios()
    }

    fun obtenerStock(): Flow<List<InventarioRepuesto>> {
        return repuestoDao.obtenerTodosLosInventarios()
    }

    fun obtenerStock(param: String): Flow<List<InventarioRepuesto>> {
        return repuestoDao.obtenerTodosLosInventarios().map { lista ->
            if (param.isBlank()) lista
            else lista.filter {
                it.inventarioId.contains(param, ignoreCase = true) ||
                        it.repuestoId.contains(param, ignoreCase = true) ||
                        it.nombreRepuesto.contains(param, ignoreCase = true) ||
                        it.nombre.contains(param, ignoreCase = true) ||
                        it.categoria.contains(param, ignoreCase = true)
            }
        }
    }

    fun obtenerTodosLosInventarios(): Flow<List<InventarioRepuesto>> {
        return repuestoDao.obtenerTodosLosInventarios()
    }

    suspend fun obtenerPorId(id: String): InventarioRepuesto? {
        return repuestoDao.obtenerPorId(id)
    }

    suspend fun insertarRepuesto(inventario: InventarioRepuesto) {
        repuestoDao.insertarInventario(inventario)
    }

    suspend fun insertarRepuesto(repuesto: Repuesto) {
        val inventario = InventarioRepuesto(
            inventarioId = if (repuesto.repuestoId.isNotBlank()) repuesto.repuestoId else "INV-" + System.currentTimeMillis(),
            repuestoId = repuesto.repuestoId,
            nombre = repuesto.nombre,
            nombreRepuesto = repuesto.nombre,
            tipo = repuesto.categoria,
            categoria = repuesto.categoria,
            cantidadDisponible = repuesto.stock,
            stockDisponible = repuesto.stock,
            precioUnitario = repuesto.precio,
            bodegaUbicacion = "Bodega Central"
        )
        repuestoDao.insertarInventario(inventario)
    }

    suspend fun insertarInventario(inventario: InventarioRepuesto) {
        repuestoDao.insertarInventario(inventario)
    }

    suspend fun actualizarStock(inventario: InventarioRepuesto) {
        repuestoDao.actualizarInventario(inventario)
    }

    suspend fun actualizarStock(id: String, nuevoStock: Int) {
        val item = repuestoDao.obtenerPorId(id)
        if (item != null) {
            repuestoDao.actualizarInventario(
                item.copy(
                    stockDisponible = nuevoStock,
                    cantidadDisponible = nuevoStock
                )
            )
        }
    }

    suspend fun actualizarStock(id: String, nuevoStock: Any) {
        val stockInt = when (nuevoStock) {
            is Int -> nuevoStock
            is Number -> nuevoStock.toInt()
            is String -> nuevoStock.toIntOrNull() ?: 0
            else -> 0
        }
        actualizarStock(id, stockInt)
    }

    suspend fun actualizarInventario(inventario: InventarioRepuesto) {
        repuestoDao.actualizarInventario(inventario)
    }

    suspend fun eliminarInventario(inventario: InventarioRepuesto) {
        repuestoDao.eliminarInventario(inventario)
    }
}