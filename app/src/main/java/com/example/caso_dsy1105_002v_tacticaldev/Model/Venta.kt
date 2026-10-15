@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey
    val ventaId: String = "",
    val vendedorId: String = "",
    val vendedorNombre: String = "",
    val clienteNombre: String = "",
    val minimarketId: String = "",
    val repuestoId: String = "",
    val productoNombre: String = "",
    val tipoOperacion: String = "Venta Directa",
    val cantidad: Int = 1,
    val precioUnitario: Double = 0.0,
    val total: Double = 0.0,
    val montoTotal: Double = 0.0,
    val fecha: String = "",
    val estado: String = "Completado",
    val detalle: String = "",
    val descripcion: String = "",
    val fotosUrls: String = ""
)