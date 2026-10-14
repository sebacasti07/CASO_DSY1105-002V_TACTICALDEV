package com.example.caso_dsy1105_002v_tacticaldev.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val usuarioId: String,        // ID interno o código de usuario (ej: "USR-001")
    val rut: String,                          // RUT chileno del usuario (ej: "12345678-K")
    val primerNombre: String,
    val segundoNombre: String? = null,         // Campo opcional
    val apellido: String,
    val correo: String,
    val telefono: String,
    val contrasena: String,                    // Credencial para inicio de sesión
    val rol: Rol                               // Enum: ADMINISTRADOR, CLIENTE, TECNICO, etc.
)