package com.example.caso_dsy1105_002v_tacticaldev.Util

import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol

object ValidationUtils {

    fun validarRut(rut: String): String? {
        val trimmed = rut.trim()
        val regex = Regex("^[0-9]{7,8}-[0-9kK]$")
        return if (regex.matches(trimmed)) null else "RUT inválido (ej: 12345678-K)"
    }

    fun validarSoloLetras(texto: String, campo: String): String? {
        val trimmed = texto.trim()
        if (trimmed.isEmpty()) return "El campo $campo es obligatorio"
        val regex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
        return if (regex.matches(trimmed)) null else "El campo $campo solo debe contener letras"
    }

    fun validarTelefono(telefono: String): String? {
        val trimmed = telefono.trim()
        if (trimmed.length < 8 || !trimmed.all { it.isDigit() }) {
            return "El teléfono debe contener solo números (mínimo 8 dígitos)"
        }
        return null
    }

    fun validarCorreoLogin(correo: String): String? {
        val trimmed = correo.trim().lowercase()
        if (trimmed.isEmpty()) return "El correo es obligatorio"
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return if (regex.matches(trimmed)) null else "Formato de correo electrónico inválido"
    }

    fun validarCorreoPorRol(correo: String, rol: Rol): String? {
        val errorFormato = validarCorreoLogin(correo)
        if (errorFormato != null) return errorFormato

        val trimmed = correo.trim().lowercase()
        val dominioEsperado = when (rol) {
            Rol.ADMINISTRADOR -> "@admindev.cl"
            Rol.TECNICO -> "@tecdev.cl"
            Rol.VENDEDOR -> "@vendedordev.cl"
            Rol.TRANSPORTISTA -> "@transportedev.cl"
            Rol.CLIENTE -> "@usuariodev.cl"
        }
        return if (trimmed.endsWith(dominioEsperado)) null else "El correo para el rol ${rol.name} debe finalizar en $dominioEsperado"
    }

    fun validarContrasena(password: String, esEdicion: Boolean = false): String? {
        val passLimpia = password.trim()
        if (esEdicion && passLimpia.isEmpty()) return null
        if (passLimpia.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (!passLimpia.any { it.isUpperCase() }) return "Debe contener al menos 1 letra mayúscula"
        if (!passLimpia.any { it.isLowerCase() }) return "Debe contener al menos 1 letra minúscula"
        if (!passLimpia.any { it.isDigit() }) return "Debe contener al menos 1 número"
        return null
    }

    fun validarConfirmacionContrasena(password: String, confirmacion: String): String? {
        return if (password.trim() == confirmacion.trim()) null else "Las contraseñas no coinciden"
    }
}
