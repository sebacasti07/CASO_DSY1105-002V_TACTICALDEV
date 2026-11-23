package com.example.caso_dsy1105_002v_tacticaldev.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.caso_dsy1105_002v_tacticaldev.Model.AsistenciaRemota
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenciaRemotaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAsistencia(asistencia: AsistenciaRemota)

    @Update
    suspend fun actualizarAsistencia(asistencia: AsistenciaRemota)

    @Delete
    suspend fun eliminarAsistencia(asistencia: AsistenciaRemota)

    @Query("SELECT * FROM asistencias_remotas")
    fun obtenerTodasLasAsistencias(): Flow<List<AsistenciaRemota>>
}