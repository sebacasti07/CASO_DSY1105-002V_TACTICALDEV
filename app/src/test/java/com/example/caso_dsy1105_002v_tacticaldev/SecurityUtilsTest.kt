package com.example.caso_dsy1105_002v_tacticaldev

import com.example.caso_dsy1105_002v_tacticaldev.Util.SecurityUtils
import org.junit.Assert.*
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun testHashPasswordConsistency() {
        val pass = "Admin1234@"
        val hash1 = SecurityUtils.hashPassword(pass)
        val hash2 = SecurityUtils.hashPassword(pass)

        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length)
        assertNotEquals(pass, hash1)
    }
}
