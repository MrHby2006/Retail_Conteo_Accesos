package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.PersonaIngreso
import com.example.rentail_conteo.model.PortonUIState
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Lógica del observador en la puerta: valida y guarda a cada persona que entra.
class PortonViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RinconSurDbHelper.obtener(application)

    // Los accesos se leen de la BD (no están escritos a mano en el código)
    private val _uiState = MutableStateFlow(PortonUIState(accesos = db.listarAccesos()))
    val uiState: StateFlow<PortonUIState> = _uiState.asStateFlow()

    fun onAccesoElegido(acceso: AccesoItem) {
        _uiState.update {
            it.copy(
                accesoElegido = acceso,
                errorAcceso = null,
                registradosHoy = db.contarIngresosDeHoy(acceso.id.toLong(), Fechas.hoy())
            )
        }
    }

    fun onRangoElegido(valor: String) = _uiState.update { it.copy(rangoEtario = valor, errorRango = null) }

    fun onAlturaElegida(valor: String) = _uiState.update { it.copy(altura = valor, errorAltura = null) }

    fun onColorElegido(valor: String) = _uiState.update { it.copy(colorRopa = valor, errorColor = null) }

    fun onAcompanadoCambia(valor: Boolean) = _uiState.update { it.copy(acompanado = valor) }

    fun onDetalleCambia(valor: String) = _uiState.update { it.copy(detalle = valor, errorDetalle = null) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    // Botón "Registrar ingreso"
    fun onRegistrar(idObservador: Long) {
        val estado = _uiState.value

        val errorAcceso = if (estado.accesoElegido == null) "Elige en qué acceso estás" else null
        val errorRango = if (estado.rangoEtario == null) "Elige un rango de edad" else null
        val errorAltura = if (estado.altura == null) "Elige la altura" else null
        val errorColor = if (estado.colorRopa == null) "Elige el color de la ropa" else null
        val errorDetalle = if (estado.detalle.trim().length > 60) "Máximo 60 caracteres" else null

        _uiState.update {
            it.copy(
                errorAcceso = errorAcceso, errorRango = errorRango, errorAltura = errorAltura,
                errorColor = errorColor, errorDetalle = errorDetalle
            )
        }
        val hayErrores = listOf(errorAcceso, errorRango, errorAltura, errorColor, errorDetalle).any { it != null }
        if (hayErrores) return

        // Aquí ya se sabe que no son null (si lo fueran, habría error y se habría salido arriba)
        val acceso = estado.accesoElegido!!
        val id = db.insertarPersona(
            PersonaIngreso(
                accesoId = acceso.id.toLong(),
                observadorId = idObservador,
                fechaHora = Fechas.ahora(),
                rangoEtario = estado.rangoEtario!!,
                altura = estado.altura!!,
                colorRopa = estado.colorRopa!!,
                acompanado = estado.acompanado,
                detalle = estado.detalle.trim()
            )
        )
        if (id == -1L) {
            _uiState.update { it.copy(mensaje = "No se pudo guardar. Inténtalo de nuevo") }
            return
        }

        // Se limpian los datos de la persona, pero el ACCESO se mantiene (el observador sigue en su puerta)
        _uiState.update {
            PortonUIState(
                accesos = it.accesos,
                accesoElegido = acceso,
                registradosHoy = db.contarIngresosDeHoy(acceso.id.toLong(), Fechas.hoy()),
                mensaje = "Ingreso registrado en ${acceso.codigo}"
            )
        }
    }
}
