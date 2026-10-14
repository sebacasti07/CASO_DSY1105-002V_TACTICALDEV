package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "minimarkets")
data class Minimarket(
    @PrimaryKey val minimarketId: String, // Clave primaria explícita (ej: "MM-SANTIAGO-01")
    val nombre: String,
    val direccion: String,
    val ciudad: String,
    val clienteId: String
)