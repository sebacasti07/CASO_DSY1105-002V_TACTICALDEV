package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Model.Rol
import com.example.caso_dsy1105_002v_tacticaldev.Util.ValidationUtils
import org.junit.Assert.*
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun testValidarRut() {
        assertNull(ValidationUtils.validarRut("12345678-K"))
        assertNull(ValidationUtils.validarRut("1234567-9"))
        assertNotNull(ValidationUtils.validarRut("12345-1"))
        assertNotNull(ValidationUtils.validarRut("abcde-1"))
    }

    @Test
    fun testValidarSoloLetras() {
        assertNull(ValidationUtils.validarSoloLetras("Carlos Andrés", "Nombre"))
        assertNotNull(ValidationUtils.validarSoloLetras("Carlos123", "Nombre"))
        assertNotNull(ValidationUtils.validarSoloLetras("", "Nombre"))
    }

    @Test
    fun testValidarTelefono() {
        assertNull(ValidationUtils.validarTelefono("911112222"))
        assertNotNull(ValidationUtils.validarTelefono("123"))
        assertNotNull(ValidationUtils.validarTelefono("1234567a"))
    }

    @Test
    fun testValidarCorreoPorRol() {
        assertNull(ValidationUtils.validarCorreoPorRol("admin@admindev.cl", Rol.ADMINISTRADOR))
        assertNull(ValidationUtils.validarCorreoPorRol("tecnico@tecdev.cl", Rol.TECNICO))
        assertNull(ValidationUtils.validarCorreoPorRol("vendedor@vendedordev.cl", Rol.VENDEDOR))
        assertNull(ValidationUtils.validarCorreoPorRol("transportista@transportedev.cl", Rol.TRANSPORTISTA))
        assertNull(ValidationUtils.validarCorreoPorRol("cliente@usuariodev.cl", Rol.CLIENTE))

        assertNotNull(ValidationUtils.validarCorreoPorRol("cliente@admindev.cl", Rol.CLIENTE))
        assertNotNull(ValidationUtils.validarCorreoPorRol("invalido", Rol.CLIENTE))
    }

    @Test
    fun testValidarContrasena() {
        assertNull(ValidationUtils.validarContrasena("Admin1234@"))
        assertNotNull(ValidationUtils.validarContrasena("admin1234"))
        assertNotNull(ValidationUtils.validarContrasena("ADMIN1234"))
        assertNotNull(ValidationUtils.validarContrasena("AdminABC"))
        assertNotNull(ValidationUtils.validarContrasena("Ad1"))
    }
}
