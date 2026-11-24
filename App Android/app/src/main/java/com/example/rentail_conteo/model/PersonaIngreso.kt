package com.example.rentail_conteo.model

data class PersonaIngreso(
    val id: Long = 0,
    val accesoId: Long,
    val accesoCodigo: String = "",
    val observadorId: Long,
    val fechaHora: String,
    val rangoEtario: String,
    val altura: String,
    val colorRopa: String,
    val colorRopaAbajo: String,
    val compania: String = "",
    val detalle: String = "",
    val compro: Boolean = false
) {
    fun resumen(): String =
        "$rangoEtario · Altura ${altura.lowercase()} · " +
                "Arriba ${colorRopa.lowercase()}, abajo ${colorRopaAbajo.lowercase()}"
}

data class Opcion(
    val id: Long,
    val nombre: String
)
