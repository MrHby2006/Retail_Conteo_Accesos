package com.example.rentail_conteo.model

// Un turno que el supervisor le asignó a un trabajador (una fila de la tabla "turnos").
// puesto = "A1".."A4" si le toca una puerta, o "CAJA".
data class Turno(
    val id: Long,
    val idUsuario: Long,
    val nombreTrabajador: String,
    val fecha: String,          // "2026-10-23"
    val franja: String,         // "Mañana" o "Tarde"
    val puesto: String
) {
    // "Puerta A4" o "Caja"
    fun puestoLegible(): String = if (puesto == PUESTO_CAJA) "Caja" else "Puerta $puesto"

    companion object {
        const val PUESTO_CAJA = "CAJA"
    }
}

// Las dos franjas del día. Son fijas, como los rasgos de Opciones.
object Franjas {
    val todas = listOf("Mañana", "Tarde")

    fun horario(franja: String): String = if (franja == "Mañana") "10:00 a 15:30" else "15:30 a 21:00"

    // Hora en que termina la franja: "15:30" o "21:00"
    fun horaFin(franja: String): String = horario(franja).substringAfter(" a ")

    // La franja que viene después (la de la mañana la recibe la tarde; después de la tarde se cierra)
    fun siguiente(franja: String): String? = if (franja == "Mañana") "Tarde" else null
}

// Estado de la pantalla del SUPERVISOR: elegir día → trabajador → franja → puesto, y ver lo asignado.
data class TurnosUIState(
    val diaElegido: String = "",                    // "2026-10-23", elegido en el calendario
    val trabajadores: List<Usuario> = emptyList(),  // los trabajadores de piso
    val trabajadorElegido: Usuario? = null,
    val franjaElegida: String? = null,
    val puestos: List<String> = emptyList(),        // CAJA, A1, A2, A3, A4 (cualquiera puede ir a cualquiera)
    val puestoElegido: String? = null,
    val turnosDelDia: List<Turno> = emptyList(),
    val errorTrabajador: String? = null,
    val errorFranja: String? = null,
    val errorPuesto: String? = null,
    val mensaje: String? = null
)
