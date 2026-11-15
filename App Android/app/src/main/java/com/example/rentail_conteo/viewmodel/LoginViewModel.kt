package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.UsuarioRepository
import com.example.rentail_conteo.model.LoginUiState
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.ValidadorRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repositorio = UsuarioRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor.trim(), errorRut = null) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null) }
    }

    fun onIngresar(onExito: (Usuario) -> Unit) {
        val estado = _uiState.value
        if (estado.cargando) return

        val errorRut = when {
            estado.rut.isBlank() -> "El RUT es obligatorio"
            !ValidadorRut.esValido(estado.rut) -> "RUT inválido, revisa el dígito verificador"
            else -> null
        }
        val errorContrasena = if (estado.contrasena.length < 6) "Mínimo 6 caracteres" else null

        _uiState.update { it.copy(errorRut = errorRut, errorContrasena = errorContrasena) }
        if (errorRut != null || errorContrasena != null) return

        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            try {
                val usuario = repositorio.iniciarSesion(ValidadorRut.limpiar(estado.rut), estado.contrasena)
                _uiState.update { LoginUiState() }
                onExito(usuario)
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, errorContrasena = e.message) }
            }
        }
    }
}
