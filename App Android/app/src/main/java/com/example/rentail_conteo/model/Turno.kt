package com.example.rentail_conteo.model

import com.example.rentail_conteo.util.Fechas

data class Turno(
    val id: Long,
    val idUsuario: Long,
    val nombreTrabajador: String,
    val fecha: String,
    val franja: String,
    val puesto: String,
    val estado: String = ESTADO_PUBLICADO,
    val cambioPendiente: Boolean = false
) {
    fun puestoLegible(): String = if (puesto == PUESTO_CAJA) "Caja" else "Puerta $puesto"

    fun yaTermino(): Boolean =
        Fechas.aMillisLocal("$fecha ${Franjas.horaFin(franja)}") <= System.currentTimeMillis()

    fun yaEmpezo(): Boolean =
        Fechas.aMillisLocal("$fecha ${Franjas.horaInicio(franja)}") <= System.currentTimeMillis()

    val esBorrador: Boolean get() = estado == ESTADO_BORRADOR

    companion object {
        const val PUESTO_CAJA = "CAJA"
        const val ESTADO_BORRADOR = "BORRADOR"
        const val ESTADO_PUBLICADO = "PUBLICADO"
    }
}

object Franjas {
    const val APERTURA = "10:00"
    const val CIERRE = "21:00"
    const val CAMBIO = "15:30"
    const val MANANA = "Mañana"
    const val TARDE = "Tarde"

    val todas = listOf(MANANA, TARDE)

    fun tiendaAbierta(hora: String): Boolean = hora >= APERTURA && hora < CIERRE

    fun horario(franja: String): String = if (franja == "Mañana") "10:00 a 15:30" else "15:30 a 21:00"

    fun horaInicio(franja: String): String = horario(franja).substringBefore(" a ")

    fun horaFin(franja: String): String = horario(franja).substringAfter(" a ")

    fun siguiente(franja: String): String? = if (franja == "Mañana") "Tarde" else null
}

data class TurnosUIState(
    val esProximaSemana: Boolean = true,
    val diasSemana: List<String> = emptyList(),
    val diaElegido: String = "",
    val trabajadores: List<Usuario> = emptyList(),
    val trabajadorElegido: Usuario? = null,
    val franjaElegida: String? = null,
    val puestos: List<String> = emptyList(),
    val puestoElegido: String? = null,
    val turnosDelDia: List<Turno> = emptyList(),
    val turnosSemana: List<Turno> = emptyList(),
    val turnoEnEdicion: Turno? = null,
    val errorTrabajador: String? = null,
    val errorFranja: String? = null,
    val errorPuesto: String? = null,
    val pidiendoPublicar: Boolean = false,
    val mensaje: String? = null
) {
    val borradoresSemana: Int get() = turnosSemana.count { it.esBorrador }

    val diasCompletos: Int get() = diasSemana.count { faltaEn(it).isEmpty() }

    val semanaCompleta: Boolean get() = diasSemana.isNotEmpty() && diasCompletos == diasSemana.size

    fun faltaEn(dia: String): List<String> = Franjas.todas.flatMap { franja ->
        val turnos = turnosSemana.filter { it.fecha == dia && it.franja == franja }
        buildList {
            if (turnos.none { it.puesto == Turno.PUESTO_CAJA }) add("caja en la ${franja.lowercase()}")
            if (turnos.none { it.puesto != Turno.PUESTO_CAJA }) add("una puerta en la ${franja.lowercase()}")
        }
    }
}
