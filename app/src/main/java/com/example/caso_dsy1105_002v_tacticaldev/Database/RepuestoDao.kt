package com.example.caso_dsy1105_002v_tacticaldev.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.caso_dsy1105_002v_tacticaldev.Model.InventarioRepuesto
import kotlinx.coroutines.flow.Flow

@Dao
interface RepuestoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarInventario(inventario: InventarioRepuesto)

    @Update
    suspend fun actualizarInventario(inventario: InventarioRepuesto)

    @Delete
    suspend fun eliminarInventario(inventario: InventarioRepuesto)

    @Query("SELECT * FROM inventario_repuestos")
    fun obtenerTodosLosInventarios(): Flow<List<InventarioRepuesto>>

    @Query("SELECT * FROM inventario_repuestos WHERE inventarioId = :id OR repuestoId = :id LIMIT 1")
    suspend fun obtenerPorId(id: String): InventarioRepuesto?
}