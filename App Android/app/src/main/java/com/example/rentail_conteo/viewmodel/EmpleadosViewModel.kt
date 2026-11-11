package com.example.rentail_conteo.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.CuentaCreada
import com.example.rentail_conteo.model.EmpleadosUIState
import com.example.rentail_conteo.model.Rol
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.Fechas
import com.example.rentail_conteo.util.GeneradorCorreo
import com.example.rentail_conteo.util.Seguridad
import com.example.rentail_conteo.util.ValidadorRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EmpleadosViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(EmpleadosUIState(empleados = db.listarEmpleados()))
    val uiState: StateFlow<EmpleadosUIState> = _uiState.asStateFlow()

    fun onMostrarFormulario() = _uiState.update { EmpleadosUIState(empleados = it.empleados, mostrandoFormulario = true) }

    fun onCancelarFormulario() = _uiState.update { EmpleadosUIState(empleados = it.empleados) }

    fun onNombreCambia(valor: String) = _uiState.update {
        it.copy(
            nombre = valor,
            errorNombre = null,
            correoEmpresaSugerido = if (valor.trim().contains(" ")) {
                GeneradorCorreo.generar(valor) { correo -> db.correoEmpresaEnUso(correo) }
            } else ""
        )
    }

    fun onRutCambia(valor: String) = _uiState.update { it.copy(rut = valor.trim(), errorRut = null) }

    fun onCargoCambia(valor: String) = _uiState.update { it.copy(cargo = valor, errorCargo = null) }

    fun onRolElegido(rolId: Long) = _uiState.update { it.copy(rolElegido = rolId, errorRol = null) }

    fun onCorreoPersonalCambia(valor: String) =
        _uiState.update { it.copy(correoPersonal = valor.trim(), errorCorreoPersonal = null) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun onCerrarCuentaCreada() = _uiState.update { it.copy(cuentaCreada = null) }

    fun onGuardar() {
        val estado = _uiState.value
        val nombre = estado.nombre.trim().replace(Regex("\\s+"), " ")

        val errorNombre = when {
            nombre.isBlank() -> "El nombre es obligatorio"
            !nombre.all { it.isLetter() || it == ' ' } -> "Solo letras y espacios"
            nombre.split(" ").size < 2 -> "Escribe al menos nombre y apellido"
            nombre.length > 60 -> "Máximo 60 caracteres"
            else -> null
        }
        val errorRut = when {
            estado.rut.isBlank() -> "El RUT es obligatorio"
            !ValidadorRut.esValido(estado.rut) -> "RUT inválido, revisa el dígito verificador"
            db.rutEnUso(ValidadorRut.limpiar(estado.rut)) -> "Ya existe un empleado con ese RUT"
            else -> null
        }
        val errorCargo = when {
            estado.cargo.isBlank() -> "El cargo es obligatorio"
            estado.cargo.trim().length > 40 -> "Máximo 40 caracteres"
            else -> null
        }
        val errorRol = if (estado.rolElegido == null) "Elige el rol" else null
        val errorCorreo = when {
            estado.correoPersonal.isBlank() -> "El correo personal es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(estado.correoPersonal).matches() -> "Ingresa un correo válido"
            GeneradorCorreo.esDeLaEmpresa(estado.correoPersonal) -> "Debe ser un correo personal, no de la empresa"
            db.correoPersonalEnUso(estado.correoPersonal) -> "Ese correo ya está registrado"
            else -> null
        }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre, errorRut = errorRut, errorCargo = errorCargo,
                errorRol = errorRol, errorCorreoPersonal = errorCorreo
            )
        }
        if (listOf(errorNombre, errorRut, errorCargo, errorRol, errorCorreo).any { it != null }) return

        val correoEmpresa = GeneradorCorreo.generar(nombre) { db.correoEmpresaEnUso(it) }
        val temporal = Seguridad.contrasenaTemporal()
        val id = db.insertarEmpleado(
            nombre = nombre,
            rutLimpio = ValidadorRut.limpiar(estado.rut),
            correoEmpresa = correoEmpresa,
            correoPersonal = estado.correoPersonal,
            cargo = estado.cargo,
            rolId = estado.rolElegido!!,
            contrasenaTemporal = temporal
        )
        if (id == -1L) {
            _uiState.update { it.copy(mensaje = "No se pudo crear la cuenta") }
            return
        }
        _uiState.update {
            EmpleadosUIState(
                empleados = db.listarEmpleados(),
                cuentaCreada = CuentaCreada(nombre, correoEmpresa, temporal)
            )
        }
    }

    fun onPedirDesactivar(empleado: Usuario, idAdmin: Long) {
        val turnos = db.contarTurnosDesde(empleado.id, Fechas.hoy())
        val error = when {
            empleado.id == idAdmin -> "No puedes desactivar tu propia cuenta"
            empleado.rolId == Rol.ADMINISTRADOR -> "Las cuentas de administrador no se desactivan desde aquí"
            turnos > 0 -> "${empleado.primerNombre()} tiene $turnos turno(s) desde hoy. Pide al supervisor que los reasigne primero"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(mensaje = error) }
            return
        }
        _uiState.update { it.copy(porDesactivar = empleado) }
    }

    fun onCancelarDesactivar() = _uiState.update { it.copy(porDesactivar = null) }

    fun onConfirmarDesactivar() {
        val empleado = _uiState.value.porDesactivar ?: return
        val ok = db.desactivarEmpleado(empleado.id)
        _uiState.update {
            it.copy(
                porDesactivar = null,
                empleados = db.listarEmpleados(),
                mensaje = if (ok) "${empleado.nombreCorto()} ya no puede ingresar a la app" else "No se pudo desactivar"
            )
        }
    }
}
