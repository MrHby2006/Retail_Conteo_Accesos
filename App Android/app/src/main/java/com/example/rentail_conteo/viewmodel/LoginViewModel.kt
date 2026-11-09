package com.example.rentail_conteo.viewmodel

import android.app.Application
import android.util.Patterns
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

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor.trim(), errorCorreo = null) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null) }
    }

    fun onIngresar(): Usuario? {
        val estado = _uiState.value

        val errorCorreo = when {
            estado.correo.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(estado.correo).matches() -> "Ingresa un correo válido"
            else -> null
        }
        val errorContrasena = if (estado.contrasena.length < 6) "Mínimo 6 caracteres" else null

        _uiState.update { it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena) }
        if (errorCorreo != null || errorContrasena != null) return null

        val usuario = db.buscarUsuarioParaLogin(estado.correo, estado.contrasena)
        if (usuario == null) {
            _uiState.update { it.copy(errorContrasena = "Correo o contraseña incorrectos") }
        }
        return usuario
    }
}
