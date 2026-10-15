package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos_refrigeracion")
data class EquipoRefrigeracion(
    @PrimaryKey
    val equipoId: String = "",
    val minimarketId: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val modelo: String = "",
    val modalidad: String = "Propiedad",
    val estadoOperativo: String = "Operativo",
    val fechaAdquisicion: String = "",
    val descripcion: String = "",
    val fotosUrls: String = ""
)