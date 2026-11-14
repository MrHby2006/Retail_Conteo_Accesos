package com.example.rentail_conteo

import com.example.rentail_conteo.util.GeneradorCorreo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneradorCorreoTest {

    @Test
    fun usaNombreYPrimerApellidoSinTildes() {
        assertEquals("sebastian.castillo@rinconsur.cl", GeneradorCorreo.generar("Sebastián Castillo Mora") { false })
        assertEquals("matias.nunez@rinconsur.cl", GeneradorCorreo.generar("Matías Núñez") { false })
    }

    @Test
    fun siYaExisteLeAgregaUnNumero() {
        val usados = setOf("ana.rojas@rinconsur.cl", "ana.rojas2@rinconsur.cl")
        assertEquals("ana.rojas3@rinconsur.cl", GeneradorCorreo.generar("Ana Rojas") { it in usados })
    }

    @Test
    fun reconoceLosCorreosDeLaEmpresa() {
        assertTrue(GeneradorCorreo.esDeLaEmpresa("Ana.Rojas@RinconSur.cl"))
        assertFalse(GeneradorCorreo.esDeLaEmpresa("ana.rojas@gmail.com"))
    }
}
