@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.Repository

import com.example.caso_dsy1105_002v_tacticaldev.Database.VisitaDao
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import kotlinx.coroutines.flow.Flow

class VisitaRepository(private val visitaDao: VisitaDao) {

    fun obtenerTodasLasVisitas(): Flow<List<Visita>> {
        return visitaDao.obtenerTodasLasVisitas()
    }

    fun obtenerVisitasPorMinimarket(minimarketId: String): Flow<List<Visita>> {
        return visitaDao.obtenerVisitasPorMinimarket(minimarketId)
    }

    suspend fun insertarVisita(visita: Visita) {
        visitaDao.insertarVisita(visita)
    }

    suspend fun actualizarVisita(visita: Visita) {
        visitaDao.actualizarVisita(visita)
    }

    suspend fun eliminarVisita(visita: Visita) {
        visitaDao.eliminarVisita(visita)
    }
}