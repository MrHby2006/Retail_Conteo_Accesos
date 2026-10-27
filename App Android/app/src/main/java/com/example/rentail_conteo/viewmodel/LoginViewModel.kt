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

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor.trim(), errorRut = null) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null) }
    }

    fun onIngresar(): Usuario? {
        val estado = _uiState.value

        val errorRut = when {
            estado.rut.isBlank() -> "El RUT es obligatorio"
            !ValidadorRut.esValido(estado.rut) -> "RUT inválido, revisa el dígito verificador"
            else -> null
        }
        val errorContrasena = if (estado.contrasena.length < 6) "Mínimo 6 caracteres" else null

        _uiState.update { it.copy(errorRut = errorRut, errorContrasena = errorContrasena) }
        if (errorRut != null || errorContrasena != null) return null

        val usuario = db.buscarUsuarioParaLogin(ValidadorRut.limpiar(estado.rut), estado.contrasena)
        if (usuario == null) {
            _uiState.update { it.copy(errorContrasena = "RUT o contraseña incorrectos") }
        }
        return usuario
    }
}
