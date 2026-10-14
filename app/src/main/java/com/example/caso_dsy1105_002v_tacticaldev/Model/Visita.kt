package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visitas")
data class Visita(
    @PrimaryKey(autoGenerate = true) val visitaId: Long = 0,
    val equipoId: String,                  // Referencia a EquipoRefrigeracion.codigoId
    val usuarioId: String,                 // Referencia al técnico/operario
    val fechaHora: Long = System.currentTimeMillis(),
    val localCerrado: Boolean = false,     // Indica si el minimarket estaba cerrado
    val estadoMaquina: EstadoMaquina,     // Enum con el estado actual
    val observaciones: String,
    val fotografiaMinimarket: String?,    // Ruta de la foto del local/minimarket
    val latitud: Double?,                  // Coordenada GPS
    val longitud: Double?                  // Coordenada GPS
)