package com.example.rentail_conteo.model

data class Turno(
    val id: Long,
    val idUsuario: Long,
    val nombreTrabajador: String,
    val fecha: String,
    val franja: String,
    val puesto: String
) {
    fun puestoLegible(): String = if (puesto == PUESTO_CAJA) "Caja" else "Puerta $puesto"

    companion object {
        const val PUESTO_CAJA = "CAJA"
    }
}

object Franjas {
    val todas = listOf("Mañana", "Tarde")

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
    val errorTrabajador: String? = null,
    val errorFranja: String? = null,
    val errorPuesto: String? = null,
    val mensaje: String? = null
)
