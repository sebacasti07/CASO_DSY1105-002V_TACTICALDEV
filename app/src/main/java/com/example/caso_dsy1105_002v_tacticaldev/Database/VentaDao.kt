package com.example.caso_dsy1105_002v_tacticaldev.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.caso_dsy1105_002v_tacticaldev.Model.Venta
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVenta(venta: Venta)

    @Update
    suspend fun actualizarVenta(venta: Venta)

    @Delete
    suspend fun eliminarVenta(venta: Venta)

    @Query("SELECT * FROM ventas")
    fun obtenerTodasLasVentas(): Flow<List<Venta>>
}