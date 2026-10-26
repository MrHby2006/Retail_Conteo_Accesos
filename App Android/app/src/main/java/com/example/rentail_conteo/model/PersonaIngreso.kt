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
    val acompanado: Boolean,
    val detalle: String = "",
    val compro: Boolean = false
) {
    fun resumen(): String {
        val compania = if (acompanado) "Acompañado" else "Solo"
        return "$rangoEtario · Altura ${altura.lowercase()} · " +
                "Arriba ${colorRopa.lowercase()}, abajo ${colorRopaAbajo.lowercase()} · $compania"
    }
}

object Opciones {
    val rangosEtarios = listOf("Niño (0-12)", "Joven (13-25)", "Adulto (26-40)", "Adulto (41-60)", "Mayor (60+)")
    val alturas = listOf("Baja", "Media", "Alta")
    val coloresRopa = listOf("Negro", "Blanco", "Gris", "Azul", "Rojo", "Verde", "Amarillo", "Café", "Otro")
}
