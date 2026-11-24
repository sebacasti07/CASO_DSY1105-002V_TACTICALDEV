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
        try {
            val response = apiService.insertarEquipo(equipo)
            if (response.isSuccessful) {
                equipoDao.insertarEquipo(equipo)
                sincronizarConApi()
            } else {
                equipoDao.insertarEquipo(equipo)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            equipoDao.insertarEquipo(equipo)
        }
    }

    suspend fun actualizarEquipo(equipo: EquipoRefrigeracion) {
        try {
            val response = apiService.actualizarEquipo(equipo)
            if (response.isSuccessful) {
                equipoDao.actualizarEquipo(equipo)
                sincronizarConApi()
            } else {
                equipoDao.actualizarEquipo(equipo)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            equipoDao.actualizarEquipo(equipo)
        }
    }

    suspend fun actualizarEstado(equipoId: String, estado: String) {
        val equipo = equipoDao.obtenerPorCodigo(equipoId)
        if (equipo != null) {
            val equipoActualizado = equipo.copy(estadoOperativo = estado)
            try {
                val response = apiService.actualizarEquipo(equipoActualizado)
                if (response.isSuccessful) {
                    equipoDao.actualizarEstado(equipoId, estado)
                    sincronizarConApi()
                } else {
                    equipoDao.actualizarEstado(equipoId, estado)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                equipoDao.actualizarEstado(equipoId, estado)
            }
        }
    }

    suspend fun eliminarEquipo(equipo: EquipoRefrigeracion) {
        try {
            val response = apiService.eliminarEquipo(equipo.equipoId)
            if (response.isSuccessful) {
                equipoDao.eliminarEquipo(equipo)
                sincronizarConApi()
            } else {
                equipoDao.eliminarEquipo(equipo)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            equipoDao.eliminarEquipo(equipo)
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