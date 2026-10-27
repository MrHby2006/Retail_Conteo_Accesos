package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.notificaciones.AvisoTurno
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EleccionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _turnoHoy = MutableStateFlow<Turno?>(null)
    val turnoHoy: StateFlow<Turno?> = _turnoHoy.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cargar(idTrabajador: Long) {
        _turnoHoy.value = db.turnoDeHoy(idTrabajador, Fechas.hoy())
        AvisoTurno.programarDeHoy(getApplication(), db.turnosDeHoy(idTrabajador, Fechas.hoy()))
    }

    fun probarAviso() {
        val turno = _turnoHoy.value ?: return
        val ok = AvisoTurno.mostrar(getApplication(), turno.id)
        if (!ok) avisarSinPermiso()
    }

    fun avisarSinPermiso() {
        _mensaje.value = "Sin permiso de notificaciones no recibirás el aviso de fin de turno"
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }
}
