package com.example.rentail_conteo.util

import java.text.Normalizer

object GeneradorCorreo {
    const val DOMINIO = "rinconsur.cl"

    fun base(nombreCompleto: String): String {
        val partes = limpiar(nombreCompleto).split(" ").filter { it.isNotBlank() }
        return partes.take(2).joinToString(".")
    }

    fun generar(nombreCompleto: String, estaEnUso: (String) -> Boolean): String {
        val base = base(nombreCompleto)
        var correo = "$base@$DOMINIO"
        var numero = 2
        while (estaEnUso(correo)) {
            correo = "$base$numero@$DOMINIO"
            numero++
        }
        return correo
    }

    fun esDeLaEmpresa(correo: String): Boolean = correo.trim().lowercase().endsWith("@$DOMINIO")

    private fun limpiar(texto: String): String =
        Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace(Regex("[^a-z ]"), "")
}
