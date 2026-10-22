package com.example.rentail_conteo

import com.example.rentail_conteo.util.ValidadorRut
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas unitarias del validador de RUT (se ejecutan en el PC, sin emulador).
class ValidadorRutTest {

    // Los RUT de los colaboradores precargados en la BD tienen que ser válidos
    @Test
    fun rutsDeLosColaboradoresSonValidos() {
        listOf(
            "15.234.871-1", "13.987.442-0", "17.654.320-5", "16.443.019-7",
            "19.876.345-4", "20.145.678-9", "18.322.904-4"
        ).forEach { assertTrue("Debería ser válido: $it", ValidadorRut.esValido(it)) }
    }

    @Test
    fun rutConDigitoMaloNoEsValido() {
        assertFalse(ValidadorRut.esValido("15.234.871-2"))
        assertFalse(ValidadorRut.esValido("abc"))
        assertFalse(ValidadorRut.esValido(""))
    }

    @Test
    fun limpiarQuitaPuntosYGuion() {
        assertEquals("152348711", ValidadorRut.limpiar("15.234.871-1"))
    }
}
