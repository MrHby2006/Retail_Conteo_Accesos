package com.example.rentail_conteo.model

data class InicioTrabajadorUIState(
    val turnoHoy: Turno? = null,
    val proximoTurno: Turno? = null,
    val solicitudes: List<SolicitudCambio> = emptyList(),
    val mensaje: String? = null,
    val mensajeEsDePermiso: Boolean = false
)
