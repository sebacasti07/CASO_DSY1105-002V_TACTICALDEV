package com.example.caso_dsy1105_002v_tacticaldev.Database

import androidx.room.TypeConverter
import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol

class Converters {
    @TypeConverter
    fun fromRol(rol: Rol?): String {
        return rol?.name ?: Rol.CLIENTE.name
    }

    @TypeConverter
    fun toRol(value: String?): Rol {
        if (value.isNullOrBlank()) return Rol.CLIENTE
        return try {
            Rol.valueOf(value)
        } catch (_: Exception) {
            Rol.CLIENTE
        }
    }
}