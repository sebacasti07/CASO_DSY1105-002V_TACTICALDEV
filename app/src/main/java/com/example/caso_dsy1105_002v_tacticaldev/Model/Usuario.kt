package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios") // <-- ESTA LÍNEA ES LA QUE FALTA
data class Usuario(
    @PrimaryKey
    val usuarioId: String,
    val rut: String,
    val primerNombre: String,
    val segundoNombre: String? = null,
    val apellido: String,
    val correo: String,
    val telefono: String,
    val contrasena: String,
    val rol: Rol
)