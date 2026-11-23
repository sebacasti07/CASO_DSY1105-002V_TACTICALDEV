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
        visitaDao.insertarVisita(visita)
        try {
            apiService.insertarVisita(visita)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun actualizarVisita(visita: Visita) {
        visitaDao.actualizarVisita(visita)
        try {
            apiService.actualizarVisita(visita)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun eliminarVisita(visita: Visita) {
        visitaDao.eliminarVisita(visita)
        try {
            apiService.eliminarVisita(visita.visitaId)
        } catch (e: Exception) {
            e.printStackTrace()
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
