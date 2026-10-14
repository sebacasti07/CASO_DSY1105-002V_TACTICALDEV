package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos_refrigeracion")
data class EquipoRefrigeracion(
    @PrimaryKey val codigoId: String,          // Clave primaria escaneada del QR (ej: "EQ-FREEZER-001")
    val qrImagenPath: String? = null,         // Ruta de la foto capturada de la etiqueta (opcional)
    val tipoEquipo: TipoEquipo,               // VITRINA_FREEZER o CONSERVADORA
    val categoriaProducto: CategoriaProducto, // HELADOS, LACTEOS o BEBIDAS
    val minimarketId: String,                 // ID del minimarket al que pertenece
    val estadoActual: EstadoMaquina,          // Enum: OPERATIVO, EN_FALLA, etc.
    val detalleEstado: String? = null         // Observación técnica detallada (opcional)
)