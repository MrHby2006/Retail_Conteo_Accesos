package com.example.rentail_conteo.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel

    @Before
    fun preparar() {
        viewModel = LoginViewModel()
    }

    @Test
    fun onIngresar_conCamposVacios_devuelveFalseYMuestraErrores() {
        viewModel.onIngresar{}

        assertEquals("El RUT es obligatorio", viewModel.uiState.value.errorRut)
        assertEquals("Mínimo 6 caracteres", viewModel.uiState.value.errorContrasena)
    }

    @Test
    fun onIngresar_conDatosCompletos_devuelveTrueSinErrores() {
        viewModel.onRutCambia("12.345.678-5")
        viewModel.onContrasenaCambia("123456")

        viewModel.onIngresar{}

        assertNull(viewModel.uiState.value.errorRut)
        assertNull(viewModel.uiState.value.errorContrasena)
    }

    @Test
    fun onIngresar_conDatosIncompletos_devuelveFalseYMuestraErrores() {
        viewModel.onRutCambia("123")
        viewModel.onContrasenaCambia("32")

        viewModel.onIngresar{}

        assertEquals("RUT inválido, revisa el dígito verificador", viewModel.uiState.value.errorRut)
        assertEquals("Mínimo 6 caracteres", viewModel.uiState.value.errorContrasena)
    }
}