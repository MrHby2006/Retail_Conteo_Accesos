package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.InicioTrabajadorUIState
import com.example.rentail_conteo.model.SolicitudCambio
import com.example.rentail_conteo.notificaciones.AvisoTurno
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InicioTrabajadorViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(InicioTrabajadorUIState())
    val uiState: StateFlow<InicioTrabajadorUIState> = _uiState.asStateFlow()

    fun cargar(idTrabajador: Long) {
        val hoy = Fechas.hoy()
        _uiState.update {
            it.copy(
                turnoHoy = db.turnoDeHoy(idTrabajador, hoy),
                proximoTurno = db.proximoTurno(idTrabajador, hoy),
                solicitudes = db.solicitudesPendientesPara(idTrabajador, hoy)
            )
        }
        AvisoTurno.programarDeHoy(getApplication(), db.turnosDeHoy(idTrabajador, hoy))
    }

    fun responder(solicitud: SolicitudCambio, idTrabajador: Long, acepta: Boolean) {
        val resultado = db.responderSolicitud(solicitud.id, idTrabajador, acepta)
        _uiState.update { it.copy(mensaje = resultado, mensajeEsDePermiso = false) }
        cargar(idTrabajador)
    }

    fun probarAviso() {
        val turno = _uiState.value.turnoHoy ?: return
        val ok = AvisoTurno.mostrar(getApplication(), turno.id)
        if (!ok) avisarSinPermiso()
    }

    fun avisarSinPermiso() {
        _uiState.update {
            it.copy(mensaje = "Sin permiso de notificaciones no recibirás el aviso de fin de turno", mensajeEsDePermiso = true)
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }
}
