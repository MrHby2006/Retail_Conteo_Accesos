package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.CambiarContrasenaUIState
import com.example.rentail_conteo.util.Seguridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CambiarContrasenaViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(CambiarContrasenaUIState())
    val uiState: StateFlow<CambiarContrasenaUIState> = _uiState.asStateFlow()

    fun onNuevaCambia(valor: String) = _uiState.update { it.copy(nueva = valor, errorNueva = null) }

    fun onConfirmarCambia(valor: String) = _uiState.update { it.copy(confirmar = valor, errorConfirmar = null) }

    fun onGuardar(idUsuario: Long) {
        val estado = _uiState.value
        val errorNueva = Seguridad.errorContrasenaNueva(estado.nueva)
            ?: if (db.contrasenaCoincide(idUsuario, estado.nueva)) "Debe ser distinta a la contraseña temporal" else null
        val errorConfirmar = when {
            estado.confirmar.isBlank() -> "Repite la contraseña"
            estado.confirmar != estado.nueva -> "Las contraseñas no coinciden"
            else -> null
        }
        _uiState.update { it.copy(errorNueva = errorNueva, errorConfirmar = errorConfirmar) }
        if (errorNueva != null || errorConfirmar != null) return

        if (!db.cambiarContrasena(idUsuario, estado.nueva)) {
            _uiState.update { it.copy(errorNueva = "No se pudo guardar. Inténtalo de nuevo") }
            return
        }
        _uiState.update { it.copy(usuarioActualizado = db.buscarUsuarioPorId(idUsuario)) }
    }
}
