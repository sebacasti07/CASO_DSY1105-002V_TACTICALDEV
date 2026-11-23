@file:Suppress("PackageName", "PackageDirectoryMismatch")
package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visitas")
data class Visita(
    @PrimaryKey
    val visitaId: String = "",
    val minimarketId: String = "",
    val clienteNombre: String = "",
    val direccion: String = "",
    val fecha: String = "",
    val bloqueHorario: String = "",
    val motivo: String = "",
    val estado: String = "Pendiente",
    val observacionesTecnico: String = "",
    val motivoCancelacion: String = "",
    val motivoReprogramacion: String = "",
    val ubicacionGps: String = "",
    val fotosUrls: String = "" // <-- Evidencia enviada al Técnico
)