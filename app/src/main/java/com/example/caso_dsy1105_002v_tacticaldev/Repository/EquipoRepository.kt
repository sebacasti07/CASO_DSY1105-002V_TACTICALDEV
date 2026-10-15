@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Repository

import com.example.caso_dsy1105_002v_tacticaldev.Database.EquipoDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import kotlinx.coroutines.flow.Flow

class EquipoRepository(private val equipoDao: EquipoDao) {

    val todosLosEquipos: Flow<List<EquipoRefrigeracion>> = equipoDao.obtenerTodosLosEquipos()

    fun obtenerTodosLosEquipos(): Flow<List<EquipoRefrigeracion>> {
        return equipoDao.obtenerTodosLosEquipos()
    }

    fun obtenerPorMinimarket(minimarketId: String): Flow<List<EquipoRefrigeracion>> {
        return equipoDao.obtenerPorMinimarket(minimarketId)
    }

    suspend fun obtenerPorCodigo(codigo: String): EquipoRefrigeracion? {
        return equipoDao.obtenerPorCodigo(codigo)
    }

    suspend fun insertarEquipo(equipo: EquipoRefrigeracion) {
        equipoDao.insertarEquipo(equipo)
    }

    suspend fun actualizarEquipo(equipo: EquipoRefrigeracion) {
        equipoDao.actualizarEquipo(equipo)
    }

    suspend fun actualizarEstado(equipoId: String, estado: String) {
        equipoDao.actualizarEstado(equipoId, estado)
    }

    suspend fun eliminarEquipo(equipo: EquipoRefrigeracion) {
        equipoDao.eliminarEquipo(equipo)
    }
}