@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistencias_remotas")
data class AsistenciaRemota(
    @PrimaryKey
    val asistenciaId: String = "",
    val minimarketId: String = "",
    val clienteNombre: String = "",
    val fecha: String = "",
    val bloqueHorario: String = "",
    val motivo: String = "",
    val estado: String = "Pendiente",
    val enlaceReunion: String = "",
    val motivoCancelacion: String = "",
    val motivoReprogramacion: String = "",
    val fotosUrls: String = "" // <-- Evidencia enviada al Técnico
)