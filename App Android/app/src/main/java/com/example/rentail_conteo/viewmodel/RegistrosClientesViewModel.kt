package com.example.rentail_conteo.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.ConvertidorImagen
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.PersonaIngreso
import com.example.rentail_conteo.model.RegistrosClientesUIState
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrosClientesViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(RegistrosClientesUIState())
    val uiState: StateFlow<RegistrosClientesUIState> = _uiState.asStateFlow()

    init {
        buscar()
    }

    fun onFiltroRango(valor: String) {
        _uiState.update { it.copy(filtroRango = if (it.filtroRango == valor) null else valor) }
        buscar()
    }

    fun onFiltroColor(valor: String) {
        _uiState.update { it.copy(filtroColor = if (it.filtroColor == valor) null else valor) }
        buscar()
    }

    fun onFiltroColorAbajo(valor: String) {
        _uiState.update { it.copy(filtroColorAbajo = if (it.filtroColorAbajo == valor) null else valor) }
        buscar()
    }

    private fun buscar() {
        val estado = _uiState.value
        val candidatos = db.buscarCandidatos(
            rangoEtario = estado.filtroRango,
            colorArriba = estado.filtroColor,
            colorAbajo = estado.filtroColorAbajo
        )
        _uiState.update { it.copy(candidatos = candidatos) }
    }

    fun onElegirPersona(persona: PersonaIngreso) {
        _uiState.update { it.copy(personaElegida = persona) }
    }

    fun onCancelar() {
        _uiState.update {
            it.copy(
                personaElegida = null, nombreCliente = "", correoCliente = "", fotoBoleta = null,
                errorNombre = null, errorCorreo = null, errorFoto = null
            )
        }
    }

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
        val nombre = estado.nombreCliente.trim()

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

        _uiState.update { it.copy(errorNombre = errorNombre, errorCorreo = errorCorreo, errorFoto = errorFoto) }
        if (errorNombre != null || errorCorreo != null || estado.fotoBoleta == null) return

        val archivo = ConvertidorImagen.guardar(
            getApplication(), estado.fotoBoleta, "boleta_${persona.id}_${System.currentTimeMillis()}.jpg"
        )
        if (archivo == null) {
            _uiState.update { it.copy(errorFoto = "No se pudo guardar la foto. Inténtalo de nuevo") }
            return
        }

        val ok = db.registrarCompra(
            idPersona = persona.id,
            idCajero = idCajero,
            fechaHora = Fechas.ahora(),
            nombreCliente = nombre.ifEmpty { null },
            correoCliente = estado.correoCliente.ifEmpty { null },
            rutaBoleta = archivo.absolutePath
        )
        if (!ok) {
            archivo.delete()
            _uiState.update { it.copy(mensaje = "Esta persona ya tenía una compra registrada") }
            return
        }

        onCancelar()
        _uiState.update { it.copy(mensaje = "Compra registrada") }
        buscar()
    }
}
