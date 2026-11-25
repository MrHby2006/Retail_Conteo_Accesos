package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.AccesoRepository
import com.example.rentail_conteo.data.TurnoRepository
import com.example.rentail_conteo.data.remote.IngresoRequest
import com.example.rentail_conteo.data.remote.OpcionResponse
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.Franjas
import com.example.rentail_conteo.model.Opcion
import com.example.rentail_conteo.model.PortonUIState
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PortonViewModel(
    private val horaActual: () -> String = { Fechas.hora(Fechas.ahora()) }
) : ViewModel() {
    private val accesoRepositorio = AccesoRepository()
    private val turnoRepositorio = TurnoRepository()

    private val _uiState = MutableStateFlow(PortonUIState())
    val uiState: StateFlow<PortonUIState> = _uiState.asStateFlow()

    fun cargar(idObservador: Long) {
        viewModelScope.launch {
            try {
                val accesos = accesoRepositorio.listar(soloActivos = true)
                val catalogos = accesoRepositorio.catalogos()
                _uiState.update {
                    it.copy(
                        accesos = accesos,
                        rangos = catalogos.rangos.aOpciones(),
                        alturas = catalogos.alturas.aOpciones(),
                        colores = catalogos.colores.aOpciones()
                    )
                }
                val turno = turnoRepositorio.turnoDeHoy(idObservador) ?: return@launch
                _uiState.update { it.copy(turnoHoy = "${turno.franja} · ${turno.puestoLegible()}") }
                accesos.firstOrNull { it.codigo == turno.puesto }?.let {
                    onAccesoElegido(it)
                    _uiState.update { estado -> estado.copy(accesoFijo = true) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
    }

    private fun List<OpcionResponse>.aOpciones() = map { Opcion(it.id, it.nombre) }

    fun onAccesoElegido(acceso: AccesoItem) {
        _uiState.update { it.copy(accesoElegido = acceso, errorAcceso = null) }
        actualizarContador(acceso)
    }

    private fun actualizarContador(acceso: AccesoItem) {
        viewModelScope.launch {
            try {
                val total = accesoRepositorio.contarDelDia(acceso.id.toLong(), Fechas.hoy())
                _uiState.update { it.copy(registradosHoy = total) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun onRangoElegido(valor: Opcion) = _uiState.update { it.copy(rangoEtario = valor, errorRango = null) }

    fun onAlturaElegida(valor: Opcion) = _uiState.update { it.copy(altura = valor, errorAltura = null) }

    fun onColorElegido(valor: Opcion) = _uiState.update { it.copy(colorRopa = valor, errorColor = null) }

    fun onColorAbajoElegido(valor: Opcion) =
        _uiState.update { it.copy(colorRopaAbajo = valor, errorColorAbajo = null) }

    fun onDetalleCambia(valor: String) = _uiState.update { it.copy(detalle = valor, errorDetalle = null) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun onRegistrar(idObservador: Long) {
        val estado = _uiState.value
        if (estado.guardando) return

        if (!Franjas.tiendaAbierta(horaActual())) {
            _uiState.update {
                it.copy(mensaje = "La tienda está cerrada (abre de ${Franjas.APERTURA} a ${Franjas.CIERRE}). No se pueden registrar ingresos ahora")
            }
            return
        }

        val errorAcceso = if (estado.accesoElegido == null) "Elige en qué acceso estás" else null
        val errorRango = if (estado.rangoEtario == null) "Elige un rango de edad" else null
        val errorAltura = if (estado.altura == null) "Elige la altura" else null
        val errorColor = if (estado.colorRopa == null) "Elige el color de arriba" else null
        val errorColorAbajo = if (estado.colorRopaAbajo == null) "Elige el color de abajo" else null
        val errorDetalle = if (estado.detalle.trim().length > 60) "Máximo 60 caracteres" else null

        _uiState.update {
            it.copy(
                errorAcceso = errorAcceso, errorRango = errorRango, errorAltura = errorAltura,
                errorColor = errorColor, errorColorAbajo = errorColorAbajo,
                errorDetalle = errorDetalle
            )
        }
        val hayErrores = listOf(errorAcceso, errorRango, errorAltura, errorColor, errorColorAbajo, errorDetalle)
            .any { it != null }
        if (hayErrores) return

        val acceso = estado.accesoElegido!!
        val datos = IngresoRequest(
            idAcceso = acceso.id.toLong(),
            idObservador = idObservador,
            idRango = estado.rangoEtario!!.id,
            idAltura = estado.altura!!.id,
            idColorSuperior = estado.colorRopa!!.id,
            idColorInferior = estado.colorRopaAbajo!!.id,
            detalle = estado.detalle.trim()
        )
        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            try {
                accesoRepositorio.registrarIngreso(datos)
                _uiState.update {
                    it.copy(
                        rangoEtario = null, altura = null, colorRopa = null, colorRopaAbajo = null,
                        detalle = "", guardando = false,
                        mensaje = "Ingreso registrado en ${acceso.codigo}"
                    )
                }
                actualizarContador(acceso)
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, mensaje = e.message) }
            }
        }
    }
}
