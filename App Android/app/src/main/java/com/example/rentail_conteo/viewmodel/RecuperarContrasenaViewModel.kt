package com.example.rentail_conteo.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.RecuperarContrasenaUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecuperarContrasenaViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(RecuperarContrasenaUIState())
    val uiState: StateFlow<RecuperarContrasenaUIState> = _uiState.asStateFlow()

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor.trim(), errorCorreo = null, mensajeExito = false) }
    }

    fun validarYEnviar() {
        val estado = _uiState.value
        val error = when {
            estado.correo.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(estado.correo).matches() -> "Ingrese un correo válido"
            !db.existeCorreo(estado.correo) -> "Este correo no se encuentra registrado"
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(errorCorreo = error) }
        }else {
            _uiState.update { it.copy(mensajeExito = true) }
        }
    }
}