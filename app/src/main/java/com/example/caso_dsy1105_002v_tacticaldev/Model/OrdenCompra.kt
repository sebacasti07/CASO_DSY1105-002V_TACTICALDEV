package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ordenes_compra")
data class OrdenCompra(
    @PrimaryKey
    val ordenId: String,
    val proveedor: String = "",
    val repuestoId: String = "",
    val nombreRepuesto: String = "",
    val cantidad: Int = 0,
    val precioCosto: Double = 0.0,
    val montoTotal: Double = 0.0,
    val fecha: String = "",
    val estado: String = "Completado",
    val notas: String = ""
)