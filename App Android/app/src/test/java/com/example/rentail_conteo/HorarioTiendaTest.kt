package com.example.rentail_conteo

import com.example.rentail_conteo.model.Franjas
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HorarioTiendaTest {
    @Test
    fun dentroDelHorarioEstaAbierta() {
        listOf("10:00", "12:30", "15:30", "20:59").forEach {
            assertTrue("Debería estar abierta a las $it", Franjas.tiendaAbierta(it))
        }
    }

    @Test
    fun fueraDelHorarioEstaCerrada() {
        listOf("00:00", "08:15", "09:59", "21:00", "23:45").forEach {
            assertFalse("Debería estar cerrada a las $it", Franjas.tiendaAbierta(it))
        }
    }
}
