package com.example.rentail_conteo.util

object ValidadorRut {
    fun limpiar(rutTexto: String): String =
        rutTexto.replace(".", "").replace("-", "").trim().uppercase()

    fun esValido(rutTexto: String): Boolean {
        val limpio = limpiar(rutTexto)
        if (limpio.length < 8) return false

        val cuerpo = limpio.dropLast(1)
        val dvIngresado = limpio.last()
        if (!cuerpo.all { it.isDigit() }) return false

        var suma = 0
        var multiplicador = 2
        for (digito in cuerpo.reversed()) {
            suma += digito.digitToInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val resultado = 11 - (suma % 11)
        val dvCorrecto = when (resultado) {
            11 -> '0'
            10 -> 'K'
            else -> resultado.digitToChar()
        }
        return dvIngresado == dvCorrecto
    }
}
