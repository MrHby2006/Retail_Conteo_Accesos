package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rentail_conteo.model.LoginUiState
import com.example.rentail_conteo.util.ValidadorRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Lógica del login: guarda el estado y valida. NO sabe nada de Compose.
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())       // privado y modificable
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()  // público, solo lectura

    // Cada letra escrita llega aquí. Al escribir se borra el error del campo.
    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor.trim(), errorRut = null) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null) }
    }

    // Botón "Ingresar": valida y devuelve true si todo está bien.
    fun onIngresar(): Boolean {
        val estado = _uiState.value

        val errorRut = when {
            estado.rut.isBlank() -> "El RUT es obligatorio"
            !ValidadorRut.esValido(estado.rut) -> "RUT inválido, revisa el dígito verificador"
            else -> null
        }
        val errorContrasena = if (estado.contrasena.length < 6) "Mínimo 6 caracteres" else null

        _uiState.update { it.copy(errorRut = errorRut, errorContrasena = errorContrasena) }
        return errorRut == null && errorContrasena == null
    }
}
