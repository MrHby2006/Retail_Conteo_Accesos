package com.example.rentail_conteo.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.ConvertidorImagen
import com.example.rentail_conteo.data.UsuarioRepository
import com.example.rentail_conteo.data.remote.PerfilRequest
import com.example.rentail_conteo.data.remote.RetrofitClient
import com.example.rentail_conteo.model.PerfilUIState
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.Fechas
import com.example.rentail_conteo.util.GeneradorCorreo
import com.example.rentail_conteo.util.Seguridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = UsuarioRepository()

    private val _uiState = MutableStateFlow(PerfilUIState())
    val uiState: StateFlow<PerfilUIState> = _uiState.asStateFlow()

    companion object {
        const val EDAD_MINIMA = 15
        const val EDAD_MAXIMA = 90
    }

    fun cargar(idUsuario: Long) {
        viewModelScope.launch {
            try {
                mostrarUsuario(repositorio.obtener(idUsuario))
                _uiState.update {
                    it.copy(
                        correoPersonal = it.usuario?.correoPersonal ?: "",
                        fechaNacimiento = it.usuario?.fechaNacimiento
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
    }

    private fun mostrarUsuario(usuario: Usuario) {
        val url = RetrofitClient.urlCompleta(usuario.fotoPerfil)?.let { "$it?v=${System.currentTimeMillis()}" }
        _uiState.update { it.copy(usuario = usuario, fotoUrl = url) }
    }

    fun onFotoTomada(foto: Bitmap) = subirFoto(ConvertidorImagen.aBytes(foto))

    fun onImagenElegida(uri: Uri) {
        val foto = ConvertidorImagen.desdeUri(getApplication(), uri)
        if (foto == null) {
            _uiState.update { it.copy(errorFoto = "No se pudo abrir esa imagen. Prueba con otra") }
            return
        }
        subirFoto(ConvertidorImagen.aBytes(foto))
    }

    private fun subirFoto(bytes: ByteArray) {
        val usuario = _uiState.value.usuario ?: return
        _uiState.update { it.copy(subiendoFoto = true, errorFoto = null) }
        viewModelScope.launch {
            try {
                val actualizado = repositorio.subirFoto(usuario.id, bytes)
                mostrarUsuario(actualizado)
                _uiState.update {
                    it.copy(subiendoFoto = false, usuarioActualizado = actualizado, mensaje = "Foto de perfil actualizada")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(subiendoFoto = false, errorFoto = e.message) }
            }
        }
    }

    fun onCorreoCambia(valor: String) = _uiState.update { it.copy(correoPersonal = valor.trim(), errorCorreo = null) }

    fun onAbrirCalendario() = _uiState.update { it.copy(eligiendoFecha = true) }

    fun onCerrarCalendario() = _uiState.update { it.copy(eligiendoFecha = false) }

    fun onFechaElegida(millis: Long?) {
        _uiState.update {
            it.copy(
                eligiendoFecha = false,
                fechaNacimiento = millis?.let { m -> Fechas.desdeMillisUtc(m) } ?: it.fechaNacimiento,
                errorFecha = null
            )
        }
    }

    fun onGuardarDatos() {
        val estado = _uiState.value
        val usuario = estado.usuario ?: return
        val fecha = estado.fechaNacimiento

        val errorCorreo = when {
            estado.correoPersonal.isBlank() -> "El correo personal es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(estado.correoPersonal).matches() -> "Ingresa un correo válido"
            GeneradorCorreo.esDeLaEmpresa(estado.correoPersonal) -> "Debe ser un correo personal, no de la empresa"
            else -> null
        }
        val errorFecha = when {
            fecha == null -> null
            fecha > Fechas.hoy() -> "La fecha no puede ser futura"
            Fechas.edad(fecha) < EDAD_MINIMA -> "Debes tener al menos $EDAD_MINIMA años"
            Fechas.edad(fecha) > EDAD_MAXIMA -> "Revisa el año de nacimiento"
            else -> null
        }
        _uiState.update { it.copy(errorCorreo = errorCorreo, errorFecha = errorFecha) }
        if (errorCorreo != null || errorFecha != null) return

        viewModelScope.launch {
            try {
                val correoOcupado = repositorio.listar().any {
                    it.id != usuario.id && it.correoPersonal.equals(estado.correoPersonal, ignoreCase = true)
                }
                if (correoOcupado) {
                    _uiState.update { it.copy(errorCorreo = "Ese correo ya lo usa otro empleado") }
                    return@launch
                }
                val actualizado = repositorio.actualizarPerfil(
                    usuario.id, PerfilRequest(estado.correoPersonal, usuario.telefono, fecha)
                )
                mostrarUsuario(actualizado)
                _uiState.update { it.copy(usuarioActualizado = actualizado, mensaje = "Datos guardados") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorCorreo = e.message) }
            }
        }
    }

    fun onActualCambia(valor: String) = _uiState.update { it.copy(actual = valor, errorActual = null) }

    fun onNuevaCambia(valor: String) = _uiState.update { it.copy(nueva = valor, errorNueva = null) }

    fun onConfirmarCambia(valor: String) = _uiState.update { it.copy(confirmar = valor, errorConfirmar = null) }

    fun onCambiarContrasena() {
        val estado = _uiState.value
        val usuario = estado.usuario ?: return

        val errorActual = if (estado.actual.isBlank()) "Escribe tu contraseña actual" else null
        val errorNueva = Seguridad.errorContrasenaNueva(estado.nueva)
            ?: if (estado.nueva == estado.actual) "Debe ser distinta a la actual" else null
        val errorConfirmar = when {
            estado.confirmar.isBlank() -> "Repite la contraseña"
            estado.confirmar != estado.nueva -> "Las contraseñas no coinciden"
            else -> null
        }
        _uiState.update { it.copy(errorActual = errorActual, errorNueva = errorNueva, errorConfirmar = errorConfirmar) }
        if (errorActual != null || errorNueva != null || errorConfirmar != null) return

        viewModelScope.launch {
            try {
                repositorio.cambiarContrasena(usuario.id, estado.actual, estado.nueva)
                _uiState.update { it.copy(actual = "", nueva = "", confirmar = "", mensaje = "Contraseña cambiada") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorActual = e.message) }
            }
        }
    }

    fun usuarioActualizadoUsado() = _uiState.update { it.copy(usuarioActualizado = null) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }
}
