package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.TurnoRepository
import com.example.rentail_conteo.model.InicioTrabajadorUIState
import com.example.rentail_conteo.model.SolicitudCambio
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.notificaciones.AvisoTurno
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InicioTrabajadorViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = TurnoRepository()

    private val _uiState = MutableStateFlow(InicioTrabajadorUIState())
    val uiState: StateFlow<InicioTrabajadorUIState> = _uiState.asStateFlow()

    private var turnosDelDia: List<Turno> = emptyList()

    fun cargar(idTrabajador: Long) {
        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            try {
                val hoy = Fechas.hoy()
                turnosDelDia = repositorio.semana(hoy, hoy, soloPublicados = true)
                _uiState.update {
                    it.copy(
                        cargando = false,
                        turnoHoy = repositorio.turnoDeHoy(idTrabajador),
                        proximoTurno = repositorio.proximoTurno(idTrabajador),
                        solicitudes = repositorio.solicitudesPendientes(idTrabajador)
                    )
                }
                AvisoTurno.programarDeHoy(
                    getApplication(), turnosDelDia.filter { it.idUsuario == idTrabajador }, turnosDelDia
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, mensaje = e.message, mensajeEsDePermiso = false) }
            }
        }
    }

    fun responder(solicitud: SolicitudCambio, idTrabajador: Long, acepta: Boolean) {
        viewModelScope.launch {
            try {
                val choque = if (acepta) buscarChoque(solicitud) else null
                val resultado = if (choque != null) {
                    repositorio.responder(solicitud.id, idTrabajador, acepta = false)
                    "No se pudo aplicar el cambio: $choque"
                } else {
                    repositorio.responder(solicitud.id, idTrabajador, acepta)
                }
                _uiState.update { it.copy(mensaje = resultado, mensajeEsDePermiso = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message, mensajeEsDePermiso = false) }
            }
            cargar(idTrabajador)
        }
    }

    private suspend fun buscarChoque(solicitud: SolicitudCambio): String? {
        val otros = repositorio.delDia(solicitud.fecha).filter {
            it.id != solicitud.idTurno && it.franja == solicitud.franjaNueva
        }
        return when {
            otros.any { it.idUsuario == solicitud.idNuevo } ->
                "${solicitud.nombreNuevo.substringBefore(" ")} ya tiene otro turno en esa franja"
            solicitud.puestoNuevo != Turno.PUESTO_CAJA && otros.any { it.puesto == solicitud.puestoNuevo } ->
                "la puerta ${solicitud.puestoNuevo} ya tiene a alguien en esa franja"
            else -> null
        }
    }

    fun probarAviso() {
        val turno = _uiState.value.turnoHoy ?: return
        val ok = AvisoTurno.mostrarAhora(getApplication(), turno, turnosDelDia)
        if (!ok) avisarSinPermiso()
    }

    fun avisarSinPermiso() {
        _uiState.update {
            it.copy(mensaje = "Sin permiso de notificaciones no recibirás el aviso de fin de turno", mensajeEsDePermiso = true)
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }
}
