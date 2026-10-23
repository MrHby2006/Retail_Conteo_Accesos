package com.example.rentail_conteo.model

// Una persona que el observador anotó al entrar por un acceso (tabla "personas_ingreso").
// compro = true cuando el cajero la encontró y registró su compra.
data class PersonaIngreso(
    val id: Long = 0,
    val accesoId: Long,
    val accesoCodigo: String = "",
    val observadorId: Long,
    val fechaHora: String,          // "2026-10-22 10:15"
    val rangoEtario: String,
    val altura: String,
    val colorRopa: String,          // parte de arriba
    val colorRopaAbajo: String,     // parte de abajo
    val acompanado: Boolean,
    val detalle: String = "",       // opcional: algo que ayude a reconocerla ("mochila roja")
    val compro: Boolean = false
) {
    // Lo que se muestra en la tarjeta del cajero:
    // "Adulto (26-40) · Altura media · Arriba azul, abajo negro · Acompañado"
    fun resumen(): String {
        val compania = if (acompanado) "Acompañado" else "Solo"
        return "$rangoEtario · Altura ${altura.lowercase()} · " +
                "Arriba ${colorRopa.lowercase()}, abajo ${colorRopaAbajo.lowercase()} · $compania"
    }
}

// Opciones fijas para describir a una persona.
// Son listas cerradas (y no texto libre) por dos razones:
//  1) el observador registra con pocos toques, sin escribir;
//  2) el cajero puede FILTRAR por estas mismas opciones.
object Opciones {
    val rangosEtarios = listOf("Niño (0-12)", "Joven (13-25)", "Adulto (26-40)", "Adulto (41-60)", "Mayor (60+)")
    val alturas = listOf("Baja", "Media", "Alta")
    val coloresRopa = listOf("Negro", "Blanco", "Gris", "Azul", "Rojo", "Verde", "Amarillo", "Café", "Otro")
}
