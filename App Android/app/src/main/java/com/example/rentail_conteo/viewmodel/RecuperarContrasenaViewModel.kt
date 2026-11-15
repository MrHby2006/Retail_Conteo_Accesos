package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.UsuarioRepository
import com.example.rentail_conteo.model.RecuperarContrasenaUIState
import com.example.rentail_conteo.util.Seguridad
import com.example.rentail_conteo.util.ValidadorRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecuperarContrasenaViewModel : ViewModel() {
    private val repositorio = UsuarioRepository()

    private val _uiState = MutableStateFlow(RecuperarContrasenaUIState())
    val uiState: StateFlow<RecuperarContrasenaUIState> = _uiState.asStateFlow()

    companion object {
        const val MINUTOS_VALIDEZ = 15
    }

    fun onRutCambia(valor: String) = _uiState.update { it.copy(rut = valor.trim(), errorRut = null) }

    fun onCodigoCambia(valor: String) {
        if (valor.length <= 6 && valor.all { it.isDigit() }) {
            _uiState.update { it.copy(codigo = valor, errorCodigo = null) }
        }
    }

    fun onNuevaCambia(valor: String) = _uiState.update { it.copy(nueva = valor, errorNueva = null) }

    fun onConfirmarCambia(valor: String) = _uiState.update { it.copy(confirmar = valor, errorConfirmar = null) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun onVolverAlPaso1() = _uiState.update { RecuperarContrasenaUIState(rut = it.rut) }

    fun onEnviarCodigo() {
        val estado = _uiState.value
        if (estado.enviando) return

        val error = when {
            estado.rut.isBlank() -> "El RUT es obligatorio"
            !ValidadorRut.esValido(estado.rut) -> "RUT inválido, revisa el dígito verificador"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorRut = error) }
            return
        }

        _uiState.update { it.copy(enviando = true) }
        viewModelScope.launch {
            try {
                val respuesta = repositorio.recuperar(ValidadorRut.limpiar(estado.rut))
                _uiState.update {
                    if (respuesta.correoEnviado) {
                        it.copy(enviando = false, paso = 2, codigo = "", correoOculto = respuesta.correoOculto)
                    } else {
                        it.copy(enviando = false, mensaje = "No se pudo enviar el correo a ${respuesta.correoOculto}. Inténtalo más tarde")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(enviando = false, errorRut = e.message) }
            }
        }
    }

    fun onCambiarContrasena() {
        val estado = _uiState.value
        if (estado.enviando) return

        val errorCodigo = if (estado.codigo.length != 6) "El código tiene 6 números" else null
        val errorNueva = Seguridad.errorContrasenaNueva(estado.nueva)
        val errorConfirmar = when {
            estado.confirmar.isBlank() -> "Repite la contraseña"
            estado.confirmar != estado.nueva -> "Las contraseñas no coinciden"
            else -> null
        }
        _uiState.update { it.copy(errorCodigo = errorCodigo, errorNueva = errorNueva, errorConfirmar = errorConfirmar) }
        if (errorCodigo != null || errorNueva != null || errorConfirmar != null) return

        _uiState.update { it.copy(enviando = true) }
        viewModelScope.launch {
            try {
                repositorio.restablecer(ValidadorRut.limpiar(estado.rut), estado.codigo, estado.nueva)
                _uiState.update { it.copy(enviando = false, listo = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(enviando = false, errorCodigo = e.message) }
            }
        }
    }
}
