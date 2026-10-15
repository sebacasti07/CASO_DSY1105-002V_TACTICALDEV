package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "minimarkets")
data class Minimarket(
    @PrimaryKey
    val minimarketId: String,
    val rutCliente: String = "",
    val rutComercial: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String? = null
)