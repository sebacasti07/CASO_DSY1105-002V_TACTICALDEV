package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventario_repuestos")
data class InventarioRepuesto(
    @PrimaryKey val id: String,
    val repuestoId: String,
    val cantidadDisponible: Int,
)