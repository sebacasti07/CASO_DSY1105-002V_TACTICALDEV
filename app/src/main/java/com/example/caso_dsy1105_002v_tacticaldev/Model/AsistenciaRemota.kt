package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistencias_remotas")
data class AsistenciaRemota(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipoId: String,
    val DescripcionFalla: String,
    val herramientasRequeridas: String,

    val repuestoRequeridoId: String? = null,
    val fecha: Long = System.currentTimeMillis()
)