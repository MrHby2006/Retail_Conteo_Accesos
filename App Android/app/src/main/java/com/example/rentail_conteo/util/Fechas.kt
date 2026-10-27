package com.example.rentail_conteo.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Fechas {
    fun ahora(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())

    fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun manana(): String {
        val calendario = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendario.time)
    }

    fun hora(fechaHora: String): String = fechaHora.drop(11).take(5)

    fun aMillisLocal(fechaHora: String): Long =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(fechaHora)?.time ?: 0L

    private fun formatoUtc() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun aMillisUtc(fecha: String): Long = formatoUtc().parse(fecha)?.time ?: 0L

    fun desdeMillisUtc(millis: Long): String = formatoUtc().format(Date(millis))

    fun diaCorto(fecha: String): String {
        val dia = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha) ?: return fecha
        val nombre = SimpleDateFormat("EEE", Locale("es", "CL")).format(dia).replace(".", "")
        return "${nombre.replaceFirstChar { it.uppercase() }} ${fecha.substring(8, 10)}/${fecha.substring(5, 7)}"
    }
}
