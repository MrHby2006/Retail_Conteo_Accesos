package com.example.rentail_conteo.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.AccesoRepository
import com.example.rentail_conteo.data.CompraRepository
import com.example.rentail_conteo.data.ConvertidorImagen
import com.example.rentail_conteo.data.remote.CompraRequest
import com.example.rentail_conteo.model.Opcion
import com.example.rentail_conteo.model.PersonaIngreso
import com.example.rentail_conteo.model.RegistrosClientesUIState
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrosClientesViewModel(application: Application) : AndroidViewModel(application) {
    private val accesoRepositorio = AccesoRepository()
    private val compraRepositorio = CompraRepository()

    private val _uiState = MutableStateFlow(RegistrosClientesUIState())
    val uiState: StateFlow<RegistrosClientesUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val catalogos = accesoRepositorio.catalogos()
                _uiState.update {
                    it.copy(
                        rangos = catalogos.rangos.map { o -> Opcion(o.id, o.nombre) },
                        colores = catalogos.colores.map { o -> Opcion(o.id, o.nombre) }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
        buscar()
    }

    fun onFiltroRango(valor: Opcion) {
        _uiState.update { it.copy(filtroRango = if (it.filtroRango == valor) null else valor) }
        buscar()
    }

    fun onFiltroColor(valor: Opcion) {
        _uiState.update { it.copy(filtroColor = if (it.filtroColor == valor) null else valor) }
        buscar()
    }

    fun onFiltroColorAbajo(valor: Opcion) {
        _uiState.update { it.copy(filtroColorAbajo = if (it.filtroColorAbajo == valor) null else valor) }
        buscar()
    }

    private fun buscar() {
        val estado = _uiState.value
        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            try {
                val candidatos = accesoRepositorio.candidatos(
                    idRango = estado.filtroRango?.id,
                    idColorSuperior = estado.filtroColor?.id,
                    idColorInferior = estado.filtroColorAbajo?.id,
                    fecha = Fechas.hoy()
                )
                _uiState.update { it.copy(candidatos = candidatos, cargando = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, mensaje = e.message) }
            }
        }
    }

    fun onElegirPersona(persona: PersonaIngreso) {
        _uiState.update { it.copy(personaElegida = persona) }
    }

    fun onCancelar() {
        _uiState.update {
            it.copy(
                personaElegida = null, numeroBoleta = "", nombreCliente = "", correoCliente = "", fotoBoleta = null,
                errorBoleta = null, errorNombre = null, errorCorreo = null, errorFoto = null
            )
        }
    }

    fun onBoletaCambia(valor: String) =
        _uiState.update { it.copy(numeroBoleta = valor.trim().uppercase(), errorBoleta = null) }

    fun onNombreCambia(valor: String) = _uiState.update { it.copy(nombreCliente = valor, errorNombre = null) }

    fun onCorreoCambia(valor: String) = _uiState.update { it.copy(correoCliente = valor.trim(), errorCorreo = null) }

    fun onFotoTomada(foto: Bitmap) = _uiState.update { it.copy(fotoBoleta = foto, errorFoto = null) }

    fun onImagenElegida(uri: Uri) {
        val foto = ConvertidorImagen.desdeUri(getApplication(), uri)
        _uiState.update {
            if (foto == null) it.copy(errorFoto = "No se pudo abrir esa imagen. Prueba con otra")
            else it.copy(fotoBoleta = foto, errorFoto = null)
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun onGuardar(idCajero: Long) {
        val estado = _uiState.value
        val persona = estado.personaElegida ?: return
        if (estado.guardando) return
        val nombre = estado.nombreCliente.trim()

        val errorBoleta = when {
            estado.numeroBoleta.isBlank() -> "El número de boleta es obligatorio"
            !estado.numeroBoleta.all { it.isLetterOrDigit() || it == '-' } -> "Solo letras, números y guion"
            estado.numeroBoleta.length > 20 -> "Máximo 20 caracteres"
            else -> null
        }
        val errorNombre = when {
            nombre.isEmpty() -> null
            !nombre.all { it.isLetter() || it == ' ' } -> "El nombre solo puede tener letras"
            nombre.length < 2 -> "Escribe al menos 2 letras"
            else -> null
        }
        val errorCorreo = when {
            estado.correoCliente.isEmpty() -> null
            !Patterns.EMAIL_ADDRESS.matcher(estado.correoCliente).matches() -> "Ingresa un correo válido"
            else -> null
        }
        val errorFoto = if (estado.fotoBoleta == null) "Saca o elige la foto de la boleta" else null

        _uiState.update {
            it.copy(errorBoleta = errorBoleta, errorNombre = errorNombre, errorCorreo = errorCorreo, errorFoto = errorFoto)
        }
        if (errorBoleta != null || errorNombre != null || errorCorreo != null || estado.fotoBoleta == null) return

        val bytes = ConvertidorImagen.aBytes(estado.fotoBoleta)
        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            try {
                if (compraRepositorio.boletaYaRegistrada(estado.numeroBoleta)) {
                    _uiState.update { it.copy(guardando = false, errorBoleta = "Ese número de boleta ya está registrado") }
                    return@launch
                }
                val compra = compraRepositorio.registrar(
                    CompraRequest(
                        idPersona = persona.id,
                        idCajero = idCajero,
                        numeroBoleta = estado.numeroBoleta,
                        nombreCliente = nombre.ifEmpty { null },
                        correoCliente = estado.correoCliente.ifEmpty { null }
                    )
                )
                val mensaje = try {
                    compraRepositorio.subirBoleta(compra.id, bytes)
                    "Compra registrada"
                } catch (e: Exception) {
                    "Compra registrada, pero no se pudo subir la foto de la boleta"
                }
                onCancelar()
                _uiState.update { it.copy(guardando = false, mensaje = mensaje) }
                buscar()
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, mensaje = e.message) }
            }
        }
    }
}
