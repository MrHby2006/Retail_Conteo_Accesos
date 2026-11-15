package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.UsuarioRepository
import com.example.rentail_conteo.model.CambiarContrasenaUIState
import com.example.rentail_conteo.util.Seguridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CambiarContrasenaViewModel : ViewModel() {
    private val repositorio = UsuarioRepository()

    private val _uiState = MutableStateFlow(CambiarContrasenaUIState())
    val uiState: StateFlow<CambiarContrasenaUIState> = _uiState.asStateFlow()

    fun onNuevaCambia(valor: String) = _uiState.update { it.copy(nueva = valor, errorNueva = null) }

    fun onConfirmarCambia(valor: String) = _uiState.update { it.copy(confirmar = valor, errorConfirmar = null) }

    fun onGuardar(idUsuario: Long) {
        val estado = _uiState.value
        val errorNueva = Seguridad.errorContrasenaNueva(estado.nueva)
        val errorConfirmar = when {
            estado.confirmar.isBlank() -> "Repite la contraseña"
            estado.confirmar != estado.nueva -> "Las contraseñas no coinciden"
            else -> null
        }
        _uiState.update { it.copy(errorNueva = errorNueva, errorConfirmar = errorConfirmar) }
        if (errorNueva != null || errorConfirmar != null) return

        viewModelScope.launch {
            try {
                val actualizado = repositorio.cambiarContrasena(idUsuario, "", estado.nueva)
                _uiState.update { it.copy(usuarioActualizado = actualizado) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorNueva = e.message) }
            }
        }
    }
}
