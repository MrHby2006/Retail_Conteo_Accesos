package com.example.rentail_conteo.model

import com.example.rentail_conteo.util.Fechas

data class Turno(
    val id: Long,
    val idUsuario: Long,
    val nombreTrabajador: String,
    val fecha: String,
    val franja: String,
    val puesto: String
) {
    fun puestoLegible(): String = if (puesto == PUESTO_CAJA) "Caja" else "Puerta $puesto"

    fun yaTermino(): Boolean =
        Fechas.aMillisLocal("$fecha ${Franjas.horaFin(franja)}") <= System.currentTimeMillis()

    companion object {
        const val PUESTO_CAJA = "CAJA"
    }
}

object Franjas {
    const val APERTURA = "10:00"
    const val CIERRE = "21:00"

    val todas = listOf("Mañana", "Tarde")

    fun tiendaAbierta(hora: String): Boolean = hora >= APERTURA && hora < CIERRE

    fun horario(franja: String): String = if (franja == "Mañana") "10:00 a 15:30" else "15:30 a 21:00"

    fun horaFin(franja: String): String = horario(franja).substringAfter(" a ")

    fun siguiente(franja: String): String? = if (franja == "Mañana") "Tarde" else null
}

data class TurnosUIState(
    val diaElegido: String = "",
    val trabajadores: List<Usuario> = emptyList(),
    val trabajadorElegido: Usuario? = null,
    val franjaElegida: String? = null,
    val puestos: List<String> = emptyList(),
    val puestoElegido: String? = null,
    val turnosDelDia: List<Turno> = emptyList(),
    val turnoEnEdicion: Turno? = null,
    val errorTrabajador: String? = null,
    val errorFranja: String? = null,
    val errorPuesto: String? = null,
    val mensaje: String? = null
) {
    val esHoy: Boolean get() = diaElegido == Fechas.hoy()
}
