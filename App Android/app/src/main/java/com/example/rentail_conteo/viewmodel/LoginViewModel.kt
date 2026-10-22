package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.LoginUiState
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.ValidadorRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Lógica del login: guarda el estado, valida y busca al colaborador en la BD. NO sabe nada de Compose.
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(LoginUiState())       // privado y modificable
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()  // público, solo lectura

    // Cada letra escrita llega aquí. Al escribir se borra el error del campo.
    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor.trim(), errorRut = null) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null) }
    }

    // Botón "Ingresar": valida el formato y después busca en la BD.
    // Devuelve el Usuario si todo está bien, o null si hubo algún error.
    fun onIngresar(): Usuario? {
        val estado = _uiState.value

        // 1) Formato
        val errorRut = when {
            estado.rut.isBlank() -> "El RUT es obligatorio"
            !ValidadorRut.esValido(estado.rut) -> "RUT inválido, revisa el dígito verificador"
            else -> null
        }
        val errorContrasena = if (estado.contrasena.length < 6) "Mínimo 6 caracteres" else null

        _uiState.update { it.copy(errorRut = errorRut, errorContrasena = errorContrasena) }
        if (errorRut != null || errorContrasena != null) return null

        // 2) ¿Existe ese colaborador con esa contraseña?
        val usuario = db.buscarUsuarioParaLogin(ValidadorRut.limpiar(estado.rut), estado.contrasena)
        if (usuario == null) {
            // No se dice cuál de los dos está mal: así nadie puede adivinar qué RUT existen
            _uiState.update { it.copy(errorContrasena = "RUT o contraseña incorrectos") }
        }
        return usuario
    }
}
