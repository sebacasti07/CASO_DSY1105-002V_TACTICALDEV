@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventario_repuestos")
data class InventarioRepuesto(
    @PrimaryKey
    val inventarioId: String = "",
    val repuestoId: String = "",
    val nombre: String = "",
    val nombreRepuesto: String = "",
    val tipo: String = "",
    val tipoItem: String = "Repuesto", // "Repuesto" o "Equipo"
    val tipoEquipo: String = "", // "Visicooler", "Congeladora", etc.
    val categoria: String = "",
    val cantidadDisponible: Int = 0,
    val stockDisponible: Int = 0,
    val precioUnitario: Double = 0.0, // Precio de Venta
    val precioArriendo: Double = 0.0, // Precio de Arriendo
    val bodegaUbicacion: String = "",
    val descripcion: String = "",
    val fotosUrls: String = "" // URLs separadas por coma
)