@file:Suppress("PackageName")
package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repuestos")
data class Repuesto(
    @PrimaryKey
    val repuestoId: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val stock: Int = 0,
    val precio: Double = 0.0,
    val descripcion: String = ""
)