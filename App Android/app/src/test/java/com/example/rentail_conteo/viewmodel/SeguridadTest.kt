package com.example.rentail_conteo

import com.example.rentail_conteo.util.Seguridad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadTest {

    @Test
    fun laMismaContrasenaConLaMismaSalDaElMismoHash() {
        val sal = Seguridad.nuevaSal()
        assertEquals(Seguridad.hash("Clave1234", sal), Seguridad.hash("Clave1234", sal))
    }

    @Test
    fun conSalDistintaElHashCambia() {
        assertNotEquals(
            Seguridad.hash("123456", Seguridad.nuevaSal()),
            Seguridad.hash("123456", Seguridad.nuevaSal())
        )
    }

    @Test
    fun elHashNoGuardaLaContrasenaEnTexto() {
        val hash = Seguridad.hash("123456", Seguridad.nuevaSal())
        assertFalse(hash.contains("123456"))
        assertEquals(64, hash.length)
    }

    @Test
    fun coincideSoloConLaContrasenaCorrecta() {
        val sal = Seguridad.nuevaSal()
        val guardado = Seguridad.hash("Clave1234", sal)
        assertTrue(Seguridad.coincide("Clave1234", sal, guardado))
        assertFalse(Seguridad.coincide("clave1234", sal, guardado))
    }

    @Test
    fun laContrasenaTemporalCumpleLasReglas() {
        repeat(20) { assertNull(Seguridad.errorContrasenaNueva(Seguridad.contrasenaTemporal())) }
    }

    @Test
    fun rechazaContrasenasDebiles() {
        assertEquals("Mínimo 8 caracteres", Seguridad.errorContrasenaNueva("ab12"))
        assertEquals("Debe tener al menos un número", Seguridad.errorContrasenaNueva("abcdefgh"))
        assertEquals("Debe tener al menos una letra", Seguridad.errorContrasenaNueva("12345678"))
    }
}
