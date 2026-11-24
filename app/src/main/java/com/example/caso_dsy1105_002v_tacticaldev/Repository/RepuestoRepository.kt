@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Repository

import com.example.caso_dsy1105_002v_tacticaldev.Database.RepuestoDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import com.example.caso_dsy1105_002v_tacticaldev.Model.Repuesto
import com.example.caso_dsy1105_002v_tacticaldev.Network.RetrofitClient
import com.example.caso_dsy1105_002v_tacticaldev.Network.TacticalApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepuestoRepository(
    private val repuestoDao: RepuestoDao,
    private val apiService: TacticalApiService = RetrofitClient.springBootService
) {

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
        try {
            val response = apiService.insertarInventario(inventario)
            if (response.isSuccessful) {
                repuestoDao.insertarInventario(inventario)
                sincronizarConApi()
            } else {
                repuestoDao.insertarInventario(inventario)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            repuestoDao.insertarInventario(inventario)
        }
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
        insertarRepuesto(inventario)
    }

    suspend fun insertarInventario(inventario: InventarioRepuesto) {
        insertarRepuesto(inventario)
    }

    suspend fun actualizarStock(inventario: InventarioRepuesto) {
        try {
            val response = apiService.actualizarInventario(inventario)
            if (response.isSuccessful) {
                repuestoDao.actualizarInventario(inventario)
                sincronizarConApi()
            } else {
                repuestoDao.actualizarInventario(inventario)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            repuestoDao.actualizarInventario(inventario)
        }
    }

    suspend fun actualizarStock(id: String, nuevoStock: Int) {
        val item = repuestoDao.obtenerPorId(id)
        if (item != null) {
            actualizarStock(
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
        actualizarStock(inventario)
    }

    suspend fun eliminarInventario(inventario: InventarioRepuesto) {
        try {
            val response = apiService.eliminarInventario(inventario.inventarioId)
            if (response.isSuccessful) {
                repuestoDao.eliminarInventario(inventario)
                sincronizarConApi()
            } else {
                repuestoDao.eliminarInventario(inventario)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            repuestoDao.eliminarInventario(inventario)
        }
    }

    suspend fun sincronizarConApi() {
        try {
            val response = apiService.obtenerInventarios()
            if (response.isSuccessful) {
                response.body()?.forEach { repuestoDao.insertarInventario(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
