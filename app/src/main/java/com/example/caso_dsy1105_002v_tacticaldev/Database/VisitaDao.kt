package com.example.caso_dsy1105_002v_tacticaldev.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.caso_dsy1105_002v_tacticaldev.Model.Visita
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVisita(visita: Visita)

    @Update
    suspend fun actualizarVisita(visita: Visita)

    @Delete
    suspend fun eliminarVisita(visita: Visita)

    @Query("SELECT * FROM visitas ORDER BY visitaId DESC")
    fun obtenerTodasLasVisitas(): Flow<List<Visita>>

    @Query("SELECT * FROM visitas WHERE minimarketId = :minimarketId")
    fun obtenerVisitasPorMinimarket(minimarketId: String): Flow<List<Visita>>

    @Query("SELECT * FROM visitas WHERE visitaId = :visitaId LIMIT 1")
    suspend fun obtenerVisitaPorId(visitaId: String): Visita?
}