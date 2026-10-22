package com.example.rentail_conteo.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Fechas guardadas como TEXTO "aaaa-mm-dd HH:mm": así se pueden ordenar y filtrar con SQL.
// SimpleDateFormat funciona desde Android 7 (minSdk 24).
object Fechas {

    fun ahora(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())

    fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    // "2026-10-22 10:15" -> "10:15"
    fun hora(fechaHora: String): String = fechaHora.drop(11).take(5)
}
