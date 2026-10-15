package com.example.caso_dsy1105_002v_tacticaldev.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.caso_dsy1105_002v_tacticaldev.Model.EquipoRefrigeracion
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEquipo(equipo: EquipoRefrigeracion)

    @Update
    suspend fun actualizarEquipo(equipo: EquipoRefrigeracion)

    @Delete
    suspend fun eliminarEquipo(equipo: EquipoRefrigeracion)

    @Query("SELECT * FROM equipos_refrigeracion WHERE minimarketId = :minimarketId")
    fun obtenerPorMinimarket(minimarketId: String): Flow<List<EquipoRefrigeracion>>

    @Query("SELECT * FROM equipos_refrigeracion")
    fun obtenerTodosLosEquipos(): Flow<List<EquipoRefrigeracion>>

    @Query("SELECT * FROM equipos_refrigeracion WHERE equipoId = :codigo LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): EquipoRefrigeracion?

    @Query("UPDATE equipos_refrigeracion SET estadoOperativo = :estado WHERE equipoId = :equipoId")
    suspend fun actualizarEstado(equipoId: String, estado: String)
}