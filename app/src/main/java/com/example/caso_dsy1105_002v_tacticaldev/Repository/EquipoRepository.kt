@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Repository

import com.example.caso_dsy1105_002v_tacticaldev.Database.EquipoDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import com.example.caso_dsy1105_002v_tacticaldev.Network.RetrofitClient
import com.example.caso_dsy1105_002v_tacticaldev.Network.TacticalApiService
import kotlinx.coroutines.flow.Flow

class EquipoRepository(
    private val equipoDao: EquipoDao,
    private val apiService: TacticalApiService = RetrofitClient.springBootService
) {

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
        try {
            apiService.insertarEquipo(equipo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun actualizarEquipo(equipo: EquipoRefrigeracion) {
        equipoDao.actualizarEquipo(equipo)
        try {
            apiService.actualizarEquipo(equipo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun actualizarEstado(equipoId: String, estado: String) {
        equipoDao.actualizarEstado(equipoId, estado)
        val equipo = equipoDao.obtenerPorCodigo(equipoId)
        if (equipo != null) {
            try {
                apiService.actualizarEquipo(equipo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun eliminarEquipo(equipo: EquipoRefrigeracion) {
        equipoDao.eliminarEquipo(equipo)
        try {
            apiService.eliminarEquipo(equipo.equipoId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun sincronizarConApi() {
        try {
            val response = apiService.obtenerEquipos()
            if (response.isSuccessful) {
                response.body()?.forEach { equipoDao.insertarEquipo(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}