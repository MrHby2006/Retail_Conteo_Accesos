package com.example.rentail_conteo.model

import com.example.rentail_conteo.util.Fechas

data class SolicitudCambio(
    val id: Long,
    val idTurno: Long,
    val fecha: String,
    val idTitular: Long,
    val nombreTitular: String,
    val franjaActual: String,
    val puestoActual: String,
    val idNuevo: Long,
    val nombreNuevo: String,
    val franjaNueva: String,
    val puestoNuevo: String,
    val aceptaTitular: Boolean,
    val aceptaNuevo: Boolean
) {
    val cambiaPersona: Boolean get() = idTitular != idNuevo

    fun descripcionPara(idUsuario: Long): String {
        val dia = Fechas.diaCorto(fecha)
        val antes = "$franjaActual · ${legible(puestoActual)}"
        val despues = "$franjaNueva · ${legible(puestoNuevo)}"
        return when {
            !cambiaPersona -> "Tu turno del $dia cambia de $antes a $despues"
            idUsuario == idTitular -> "Tu turno del $dia ($antes) pasa a ${primerNombre(nombreNuevo)}"
            else -> "Te asignan el turno de ${primerNombre(nombreTitular)} del $dia: $despues"
        }
    }

    private fun legible(puesto: String) = if (puesto == Turno.PUESTO_CAJA) "Caja" else "Puerta $puesto"

    private fun primerNombre(nombre: String) = nombre.trim().substringBefore(" ")

    companion object {
        const val PENDIENTE = "PENDIENTE"
        const val ACEPTADA = "ACEPTADA"
        const val RECHAZADA = "RECHAZADA"
    }
}
