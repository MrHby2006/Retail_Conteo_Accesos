package com.example.rentail_conteo.model

data class HorarioUIState(
    val esProximaSemana: Boolean = false,
    val diasSemana: List<String> = emptyList(),
    val soloMisTurnos: Boolean = false,
    val turnos: List<Turno> = emptyList()
) {
    fun turnosDe(dia: String, idUsuario: Long): List<Turno> =
        turnos.filter { it.fecha == dia && (!soloMisTurnos || it.idUsuario == idUsuario) }
}
