package com.example.rentail_conteo.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.TurnoRepository
import com.example.rentail_conteo.data.UsuarioRepository
import com.example.rentail_conteo.data.remote.EmpleadoRequest
import com.example.rentail_conteo.model.CuentaCreada
import com.example.rentail_conteo.model.EmpleadosUIState
import com.example.rentail_conteo.model.Rol
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.GeneradorCorreo
import com.example.rentail_conteo.util.ValidadorRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmpleadosViewModel : ViewModel() {
    private val repositorio = UsuarioRepository()
    private val turnoRepositorio = TurnoRepository()

    private val _uiState = MutableStateFlow(EmpleadosUIState())
    val uiState: StateFlow<EmpleadosUIState> = _uiState.asStateFlow()

    init {
        cargarEmpleados()
    }

    fun cargarEmpleados() {
        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            try {
                val empleados = repositorio.listar()
                    .sortedWith(compareBy({ it.rolId }, { it.nombre }))
                _uiState.update { it.copy(empleados = empleados, cargando = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, mensaje = e.message) }
            }
        }
    }

    fun onMostrarFormulario() = _uiState.update { EmpleadosUIState(empleados = it.empleados, mostrandoFormulario = true) }

    fun onCancelarFormulario() = _uiState.update { EmpleadosUIState(empleados = it.empleados) }

    fun onEditar(empleado: Usuario) = _uiState.update {
        EmpleadosUIState(
            empleados = it.empleados,
            mostrandoFormulario = true,
            enEdicion = empleado,
            nombres = empleado.nombres,
            apellidoPaterno = empleado.apellidoPaterno,
            apellidoMaterno = empleado.apellidoMaterno ?: "",
            rut = empleado.rutFormateado(),
            cargo = empleado.cargo,
            rolElegido = empleado.rolId,
            correoPersonal = empleado.correoPersonal ?: ""
        )
    }

    fun onNombresCambia(valor: String) = _uiState.update {
        it.copy(nombres = valor, errorNombres = null).conCorreoSugerido()
    }

    fun onApellidoPaternoCambia(valor: String) = _uiState.update {
        it.copy(apellidoPaterno = valor, errorApellidoPaterno = null).conCorreoSugerido()
    }

    fun onApellidoMaternoCambia(valor: String) =
        _uiState.update { it.copy(apellidoMaterno = valor, errorApellidoMaterno = null) }

    private fun EmpleadosUIState.conCorreoSugerido(): EmpleadosUIState {
        val primerNombre = nombres.trim().substringBefore(" ")
        val apellido = apellidoPaterno.trim().substringBefore(" ")
        val sugerido = if (enEdicion == null && primerNombre.isNotBlank() && apellido.isNotBlank()) {
            GeneradorCorreo.base("$primerNombre $apellido") + "@" + GeneradorCorreo.DOMINIO
        } else ""
        return copy(correoEmpresaSugerido = sugerido)
    }

    fun onRutCambia(valor: String) = _uiState.update { it.copy(rut = valor.trim(), errorRut = null) }

    fun onCargoCambia(valor: String) = _uiState.update { it.copy(cargo = valor, errorCargo = null) }

    fun onRolElegido(rolId: Long) = _uiState.update { it.copy(rolElegido = rolId, errorRol = null) }

    fun onCorreoPersonalCambia(valor: String) =
        _uiState.update { it.copy(correoPersonal = valor.trim(), errorCorreoPersonal = null) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun onCerrarCuentaCreada() = _uiState.update { it.copy(cuentaCreada = null) }

    private fun errorTexto(valor: String, campo: String, obligatorio: Boolean): String? = when {
        valor.isBlank() -> if (obligatorio) "$campo es obligatorio" else null
        !valor.all { it.isLetter() || it == ' ' } -> "Solo letras y espacios"
        valor.length > 40 -> "Máximo 40 caracteres"
        else -> null
    }

    fun onGuardar() {
        val estado = _uiState.value
        val enEdicion = estado.enEdicion
        val nombres = estado.nombres.trim().replace(Regex("\\s+"), " ")
        val paterno = estado.apellidoPaterno.trim().replace(Regex("\\s+"), " ")
        val materno = estado.apellidoMaterno.trim().replace(Regex("\\s+"), " ")

        val errorNombres = errorTexto(nombres, "El nombre", true)
        val errorPaterno = errorTexto(paterno, "El apellido paterno", true)
        val errorMaterno = errorTexto(materno, "El apellido materno", false)
        val errorRut = when {
            enEdicion != null -> null
            estado.rut.isBlank() -> "El RUT es obligatorio"
            !ValidadorRut.esValido(estado.rut) -> "RUT inválido, revisa el dígito verificador"
            estado.empleados.any { it.rut.equals(ValidadorRut.limpiar(estado.rut), ignoreCase = true) } ->
                "Ya existe un empleado con ese RUT"
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
            estado.empleados.any { it.id != enEdicion?.id && it.correoPersonal.equals(estado.correoPersonal, ignoreCase = true) } ->
                "Ese correo personal ya lo usa otro empleado"
            else -> null
        }

        _uiState.update {
            it.copy(
                errorNombres = errorNombres, errorApellidoPaterno = errorPaterno, errorApellidoMaterno = errorMaterno,
                errorRut = errorRut, errorCargo = errorCargo, errorRol = errorRol, errorCorreoPersonal = errorCorreo
            )
        }
        val errores = listOf(errorNombres, errorPaterno, errorMaterno, errorRut, errorCargo, errorRol, errorCorreo)
        if (errores.any { it != null }) return

        val datos = EmpleadoRequest(
            nombres = nombres,
            apellidoPaterno = paterno,
            apellidoMaterno = materno.ifBlank { null },
            rut = ValidadorRut.limpiar(estado.rut),
            cargo = estado.cargo.trim(),
            codigoRol = Rol.codigo(estado.rolElegido!!),
            correoPersonal = estado.correoPersonal
        )

        viewModelScope.launch {
            try {
                if (enEdicion != null) {
                    repositorio.editar(enEdicion.id, datos)
                    _uiState.update {
                        EmpleadosUIState(empleados = it.empleados, mensaje = "Datos de $nombres actualizados")
                    }
                } else {
                    val cuenta = repositorio.crear(datos)
                    _uiState.update {
                        EmpleadosUIState(
                            empleados = it.empleados,
                            cuentaCreada = CuentaCreada(
                                titulo = "Cuenta creada",
                                nombre = cuenta.usuario.nombreCompleto,
                                correoEmpresa = cuenta.usuario.correo,
                                contrasenaTemporal = cuenta.contrasenaTemporal,
                                correoPersonal = estado.correoPersonal,
                                correoEnviado = cuenta.correoEnviado
                            )
                        )
                    }
                }
                cargarEmpleados()
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun onPedirClaveNueva(empleado: Usuario) {
        if (empleado.correoPersonal.isNullOrBlank()) {
            _uiState.update { it.copy(mensaje = "Primero agrega el correo personal de ${empleado.primerNombre()} con Editar") }
            return
        }
        _uiState.update { it.copy(porClaveNueva = empleado) }
    }

    fun onCancelarClaveNueva() = _uiState.update { it.copy(porClaveNueva = null) }

    fun onConfirmarClaveNueva() {
        val empleado = _uiState.value.porClaveNueva ?: return
        _uiState.update { it.copy(porClaveNueva = null) }
        viewModelScope.launch {
            try {
                val cuenta = repositorio.contrasenaTemporal(empleado.id)
                _uiState.update {
                    it.copy(
                        cuentaCreada = CuentaCreada(
                            titulo = "Contraseña temporal nueva",
                            nombre = empleado.nombre,
                            correoEmpresa = empleado.correo,
                            contrasenaTemporal = cuenta.contrasenaTemporal,
                            correoPersonal = empleado.correoPersonal ?: "",
                            correoEnviado = cuenta.correoEnviado
                        )
                    )
                }
                cargarEmpleados()
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun onPedirDesactivar(empleado: Usuario, idAdmin: Long) {
        val error = when {
            empleado.id == idAdmin -> "No puedes desactivar tu propia cuenta"
            empleado.rolId == Rol.ADMINISTRADOR -> "Las cuentas de administrador no se desactivan desde aquí"
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
        _uiState.update { it.copy(porDesactivar = null) }
        viewModelScope.launch {
            try {
                val pendientes = turnoRepositorio.contarPendientes(empleado.id)
                if (pendientes > 0) {
                    _uiState.update {
                        it.copy(mensaje = "${empleado.primerNombre()} tiene $pendientes turno(s) desde hoy. Pide al supervisor que los reasigne primero")
                    }
                    return@launch
                }
                repositorio.desactivar(empleado.id)
                _uiState.update { it.copy(mensaje = "${empleado.nombreCorto()} ya no puede ingresar a la app") }
                cargarEmpleados()
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
    }
}
