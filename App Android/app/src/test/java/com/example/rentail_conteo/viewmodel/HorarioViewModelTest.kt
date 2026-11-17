package com.example.rentail_conteo.viewmodel

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HorarioViewModelTest {
    private lateinit var viewModel: HorarioViewModel

    @Before
    fun preparar() {
        viewModel = HorarioViewModel()
    }

    @Test
    fun onSemanaElegida_porDefectoSeleccionaLaSemanaActual() {
        assertFalse(viewModel.uiState.value.esProximaSemana)
    }

    @Test
    fun onSemanaElegida_conProximaSemanaTrue_actualizaEstadoAProximaSemana() {
        viewModel.onSemanaElegida(proxima = true)

        assertTrue(viewModel.uiState.value.esProximaSemana)
    }

    @Test
    fun onSoloMisTurnos_cambiaElValorDelFiltroCorrectamente() {
        assertFalse(viewModel.uiState.value.soloMisTurnos)

        viewModel.onSoloMisTurnos(valor = true)

        assertTrue(viewModel.uiState.value.soloMisTurnos)
    }
}