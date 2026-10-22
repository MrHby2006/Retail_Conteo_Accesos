package com.example.rentail_conteo.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.ConvertidorImagen
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.PersonaIngreso
import com.example.rentail_conteo.model.RegistrosClientesUIState
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Lógica del cajero:
//  1) buscar entre las personas anotadas en la puerta a la que está comprando (con filtros);
//  2) confirmar la compra: datos opcionales del cliente + foto de la boleta (obligatoria).
class RegistrosClientesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(RegistrosClientesUIState(accesos = db.listarAccesos()))
    val uiState: StateFlow<RegistrosClientesUIState> = _uiState.asStateFlow()

    init {
        buscar()
    }

    // ----------------------------- BUSCAR -----------------------------

    // Tocar un filtro ya elegido lo QUITA (vuelve a null); tocar otro lo cambia.
    fun onFiltroAcceso(acceso: AccesoItem) {
        _uiState.update { it.copy(filtroAcceso = if (it.filtroAcceso == acceso) null else acceso) }
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

    private fun buscar() {
        val estado = _uiState.value
        val candidatos = db.buscarCandidatos(
            idAcceso = estado.filtroAcceso?.id?.toLong(),
            rangoEtario = estado.filtroRango,
            colorRopa = estado.filtroColor
        )
        _uiState.update { it.copy(candidatos = candidatos) }
    }

    // ----------------------------- CONFIRMAR COMPRA -----------------------------

    fun onElegirPersona(persona: PersonaIngreso) {
        _uiState.update { it.copy(personaElegida = persona) }
    }

    // "Cambiar persona": vuelve a la búsqueda y descarta lo que se había escrito
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

    // Cámara: llega un Bitmap
    fun onFotoTomada(foto: Bitmap) = _uiState.update { it.copy(fotoBoleta = foto, errorFoto = null) }

    // Galería: llega un Uri y se convierte a Bitmap
    fun onImagenElegida(uri: Uri) {
        val foto = ConvertidorImagen.desdeUri(getApplication(), uri)
        _uiState.update {
            if (foto == null) it.copy(errorFoto = "No se pudo abrir esa imagen. Prueba con otra")
            else it.copy(fotoBoleta = foto, errorFoto = null)
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    // Botón "Guardar compra"
    fun onGuardar(idCajero: Long) {
        val estado = _uiState.value
        val persona = estado.personaElegida ?: return
        val nombre = estado.nombreCliente.trim()

        // El nombre y el correo son OPCIONALES: solo se validan si el cliente los quiso dar
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

        // 1) La foto se guarda como archivo
        val archivo = ConvertidorImagen.guardar(
            getApplication(), estado.fotoBoleta, "boleta_${persona.id}_${System.currentTimeMillis()}.jpg"
        )
        if (archivo == null) {
            _uiState.update { it.copy(errorFoto = "No se pudo guardar la foto. Inténtalo de nuevo") }
            return
        }

        // 2) La compra va a la BD. ifEmpty { null }: si no escribió nada, se guarda vacío (NULL)
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

        // 3) Se vuelve a la búsqueda: la persona ya no aparece porque ahora compro = 1
        onCancelar()
        _uiState.update { it.copy(mensaje = "Compra registrada") }
        buscar()
    }
}
