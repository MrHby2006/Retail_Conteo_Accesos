package com.example.rentail_conteo.util

// Valida un RUT chileno con su dígito verificador (RN-18).
// Está separado del ViewModel para poder reutilizarlo y probarlo solo.
object ValidadorRut {

    // "15.234.871-1" -> "152348711" (así se guarda en la BD)
    fun limpiar(rutTexto: String): String =
        rutTexto.replace(".", "").replace("-", "").trim().uppercase()

    fun esValido(rutTexto: String): Boolean {
        val limpio = limpiar(rutTexto)
        if (limpio.length < 8) return false

        val cuerpo = limpio.dropLast(1)      // "15234871"
        val dvIngresado = limpio.last()      // '1'
        if (!cuerpo.all { it.isDigit() }) return false

        // Recorre de derecha a izquierda multiplicando por 2,3,4,5,6,7,2,3...
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
