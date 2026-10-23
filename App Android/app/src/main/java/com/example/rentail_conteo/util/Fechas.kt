package com.example.rentail_conteo.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Fechas guardadas como TEXTO "aaaa-mm-dd HH:mm": así se pueden ordenar y filtrar con SQL.
// SimpleDateFormat funciona desde Android 7 (minSdk 24).
object Fechas {

    fun ahora(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())

    fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    // "2026-10-22 10:15" -> "10:15"
    fun hora(fechaHora: String): String = fechaHora.drop(11).take(5)

    // "2026-10-23 15:30" (hora de Chile, la del teléfono) -> milisegundos, para programar una alarma
    fun aMillisLocal(fechaHora: String): Long =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(fechaHora)?.time ?: 0L

    // El calendario (DatePicker) entrega y recibe el día como milisegundos a las 00:00 en UTC.
    // Por eso estas dos conversiones usan la zona UTC: si usaran la hora de Chile,
    // el día podría correrse en uno.
    private fun formatoUtc() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // "2026-10-23" -> milisegundos
    fun aMillisUtc(fecha: String): Long = formatoUtc().parse(fecha)?.time ?: 0L

    // milisegundos -> "2026-10-23"
    fun desdeMillisUtc(millis: Long): String = formatoUtc().format(Date(millis))

    // "2026-10-23" -> "Vie 23/10"
    fun diaCorto(fecha: String): String {
        val dia = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha) ?: return fecha
        val nombre = SimpleDateFormat("EEE", Locale("es", "CL")).format(dia).replace(".", "")
        return "${nombre.replaceFirstChar { it.uppercase() }} ${fecha.substring(8, 10)}/${fecha.substring(5, 7)}"
    }
}
