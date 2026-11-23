package com.example.caso_dsy1105_002v_tacticaldev.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.caso_dsy1105_002v_tacticaldev.Model.OrdenCompra
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdenCompraDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOrdenCompra(orden: OrdenCompra)

    @Update
    suspend fun actualizarOrdenCompra(orden: OrdenCompra)

    @Delete
    suspend fun eliminarOrdenCompra(orden: OrdenCompra)

    @Query("SELECT * FROM ordenes_compra")
    fun obtenerTodasLasOrdenes(): Flow<List<OrdenCompra>>
}