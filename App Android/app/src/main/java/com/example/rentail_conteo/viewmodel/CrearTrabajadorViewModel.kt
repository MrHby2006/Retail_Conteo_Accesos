package com.example.rentail_conteo.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.CrearTrabajadorUIState
import com.example.rentail_conteo.util.ValidadorRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CrearTrabajadorViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(
        CrearTrabajadorUIState(rolesDisponibles = db.listarRoles())
    )
    val uiState: StateFlow<CrearTrabajadorUIState> = _uiState.asStateFlow()

    fun onNombreCambia(v: String) = _uiState.update { it.copy(nombre = v, errorNombre = null) }
    fun onRutCambia(v: String) = _uiState.update { it.copy(rut = v, errorRut = null) }
    fun onCorreoCambia(v: String) = _uiState.update { it.copy(correo = v.trim(), errorCorreo = null) }
    fun onContrasenaCambia(v: String) = _uiState.update { it.copy(contrasena = v, errorContrasena = null) }
    fun onCargoCambia(v: String) = _uiState.update { it.copy(cargo = v, errorCargo = null) }
    fun onRolSeleccionado(rol: Pair<Long, String>) = _uiState.update { it.copy(rolSeleccionado = rol, errorRol = null) }
    fun mensajeMostrado() = _uiState.update { it.copy(mensajeExito = null) }

    fun onCrearUsuario(): Boolean {
        val s = _uiState.value

        val errorNombre = when {
            s.nombre.trim().isBlank() -> "El nombre completo es obligatorio"
            s.nombre.trim().length < 3 -> "Escribe al menos 3 caracteres"
            else -> null
        }
        val errorRut = when {
            s.rut.isBlank() -> "El RUT es obligatorio"
            !ValidadorRut.esValido(s.rut) -> "RUT inválido (revisa el dígito verificador)"
            else -> null
        }
        val errorCorreo = when {
            s.correo.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(s.correo).matches() -> "Ingresa un correo corporativo válido"
            db.existeCorreo(s.correo) -> "Este correo ya está registrado"
            else -> null
        }
        val errorContrasena = if (s.contrasena.length < 6) "La contraseña debe tener mínimo 6 caracteres" else null
        val errorCargo = if (s.cargo.trim().isBlank()) "El cargo es obligatorio" else null
        val errorRol = if (s.rolSeleccionado == null) "Selecciona un rol para el trabajador" else null

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorRut = errorRut,
                errorCorreo = errorCorreo,
                errorContrasena = errorContrasena,
                errorCargo = errorCargo,
                errorRol = errorRol
            )
        }

        val hayErrores = listOf(errorNombre, errorRut, errorCorreo, errorContrasena, errorCargo, errorRol).any { it != null }
        if (hayErrores) return false

        val ok = db.insertarUsuario(
            nombre = s.nombre.trim(),
            rut = ValidadorRut.limpiar(s.rut),
            correo = s.correo,
            contrasena = s.contrasena,
            cargo = s.cargo.trim(),
            idRol = s.rolSeleccionado!!.first
        )

        if (ok) {
            _uiState.update { it.copy(mensajeExito = "Trabajador creado exitosamente") }
        }
        return ok
    }
}