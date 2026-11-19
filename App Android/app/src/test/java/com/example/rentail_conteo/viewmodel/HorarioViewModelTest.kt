package com.example.rentail_conteo.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HorarioViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HorarioViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HorarioViewModel()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
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