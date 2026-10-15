package com.example.caso_dsy1105_002v_tacticaldev.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.caso_dsy1105_002v_tacticaldev.Model.Minimarket
import kotlinx.coroutines.flow.Flow

@Dao
interface MinimarketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMinimarket(minimarket: Minimarket)

    @Update
    suspend fun actualizarMinimarket(minimarket: Minimarket)

    @Delete
    suspend fun eliminarMinimarket(minimarket: Minimarket)

    @Query("SELECT * FROM minimarkets")
    fun obtenerTodosLosMinimarkets(): Flow<List<Minimarket>>

    @Query("SELECT * FROM minimarkets WHERE rutCliente = :rut OR rutComercial = :rut LIMIT 1")
    fun obtenerMinimarketPorRut(rut: String): Flow<Minimarket?>
}