@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.Repository

import com.example.caso_dsy1105_002v_tacticaldev.Database.VisitaDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import com.example.caso_dsy1105_002v_tacticaldev.Network.RetrofitClient
import com.example.caso_dsy1105_002v_tacticaldev.Network.TacticalApiService
import kotlinx.coroutines.flow.Flow

class VisitaRepository(
    private val visitaDao: VisitaDao,
    private val apiService: TacticalApiService = RetrofitClient.springBootService
) {

    val todasLasVisitas: Flow<List<Visita>> = visitaDao.obtenerTodasLasVisitas()

    fun obtenerTodasLasVisitas(): Flow<List<Visita>> {
        return visitaDao.obtenerTodasLasVisitas()
    }

    fun obtenerVisitasPorMinimarket(minimarketId: String): Flow<List<Visita>> {
        return visitaDao.obtenerVisitasPorMinimarket(minimarketId)
    }

    suspend fun insertarVisita(visita: Visita) {
        try {
            val response = apiService.insertarVisita(visita)
            if (response.isSuccessful) {
                visitaDao.insertarVisita(visita)
                sincronizarConApi()
            } else {
                visitaDao.insertarVisita(visita)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            visitaDao.insertarVisita(visita)
        }
    }

    suspend fun actualizarVisita(visita: Visita) {
        try {
            val response = apiService.actualizarVisita(visita)
            if (response.isSuccessful) {
                visitaDao.actualizarVisita(visita)
                sincronizarConApi()
            } else {
                visitaDao.actualizarVisita(visita)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            visitaDao.actualizarVisita(visita)
        }
    }

    suspend fun eliminarVisita(visita: Visita) {
        try {
            val response = apiService.eliminarVisita(visita.visitaId)
            if (response.isSuccessful) {
                visitaDao.eliminarVisita(visita)
                sincronizarConApi()
            } else {
                visitaDao.eliminarVisita(visita)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            visitaDao.eliminarVisita(visita)
        }
    }

    suspend fun sincronizarConApi() {
        try {
            val response = apiService.obtenerVisitas()
            if (response.isSuccessful) {
                response.body()?.forEach { visitaDao.insertarVisita(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
