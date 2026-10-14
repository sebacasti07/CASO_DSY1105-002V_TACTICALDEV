package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repuestos")
data class Repuesto(
    @PrimaryKey val repuestoId: String,       // Clave primaria explícita (ej: "REP-MOT-001")
    val nombre: String,                      // Ej: "Motor Ventilador 10W", "Termostato Digital"
    val marca: String,                       // Ej: "Carel", "Embraco", "Danfoss"
    val modelo: String,                      // Ej: "PJEZ-S", "FFU130HAK"
    val tipoEquipoCompatibilidad: TipoEquipo, // VITRINA_FREEZER o CONSERVADORA
    val categoriaProducto: CategoriaProducto, // HELADOS, LACTEOS o BEBIDAS
    val descripcion: String                  // Especificaciones técnicas adicionales
)