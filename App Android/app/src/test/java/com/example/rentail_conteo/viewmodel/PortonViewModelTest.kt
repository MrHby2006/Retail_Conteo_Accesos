package com.example.rentail_conteo.viewmodel

import com.example.rentail_conteo.model.Opcion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PortonViewModelTest {
    private lateinit var viewModel: PortonViewModel

    @Before
    fun preparar() {
        viewModel = PortonViewModel(horaActual = { "12:00" })
    }

    @Test
    fun onRegistrar_conCamposVacios_muestraErroresDeValidacionEnFormulario() {
        viewModel.onRegistrar(idObservador = 1L)

        assertEquals("Elige en qué acceso estás", viewModel.uiState.value.errorAcceso)
        assertEquals("Elige un rango de edad", viewModel.uiState.value.errorRango)
        assertEquals("Elige la altura", viewModel.uiState.value.errorAltura)
        assertEquals("Elige el color de arriba", viewModel.uiState.value.errorColor)
        assertEquals("Elige el color de abajo", viewModel.uiState.value.errorColorAbajo)
    }

    @Test
    fun onRegistrar_conLaTiendaCerrada_avisaYNoValidaElFormulario() {
        viewModel = PortonViewModel(horaActual = { "23:00" })

        viewModel.onRegistrar(idObservador = 1L)

        assertTrue(viewModel.uiState.value.mensaje!!.contains("La tienda está cerrada"))
        assertNull(viewModel.uiState.value.errorAcceso)
    }

    @Test
    fun onRangoElegido_actializaElEstadoYBorraSuError() {
        val rangoPrueba = Opcion(id = 1, nombre = "Adulto")

        viewModel.onRangoElegido(rangoPrueba)

        assertEquals(rangoPrueba, viewModel.uiState.value.rangoEtario)
        assertNull(viewModel.uiState.value.errorRango)
    }

    @Test
    fun onDetalleCambia_conTextoDemasiadoLargo_muestraErrorDeMaximoCaracteres() {
        val textoLargo = "Este es un texto excesivamente largo que sobrepasa por completo el límite establecido de sesenta caracteres para el detalle del registro del portón"

        viewModel.onDetalleCambia(textoLargo)

        viewModel.onRegistrar(idObservador = 1L)

        assertEquals("Máximo 60 caracteres", viewModel.uiState.value.errorDetalle)
    }

    @Test
    fun mensajeMostrado_limpiaElMensajeGeneralDelEstado() {
        viewModel.mensajeMostrado()

        assertNull(viewModel.uiState.value.mensaje)
    }
}