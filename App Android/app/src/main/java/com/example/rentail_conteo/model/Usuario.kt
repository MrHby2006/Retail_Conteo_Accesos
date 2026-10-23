package com.example.rentail_conteo.model

// Los 4 perfiles de la app. El número es el id_rol de la tabla "roles".
object Rol {
    const val JEFATURA = 1L        // jefa de tienda / subgerente: ve conteos e indicadores
    const val SUPERVISOR = 2L      // supervisor de piso: asigna turnos
    // Por ahora observadores y cajeros son "trabajadores": los dos pueden elegir la pantalla de
    // puerta o la de caja. Más adelante el turno asignado decidirá cuál ven.
    const val OBSERVADOR = 3L      // anota en la puerta a las personas que entran
    const val CAJERO = 4L          // marca en caja quién compró y adjunta la boleta
}

// Un colaborador de la tienda (una fila de la tabla "usuarios")
data class Usuario(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val rut: String,
    val cargo: String,
    val rolId: Long
) {
    // "Tamara Rojas Espinoza" -> "Tamara"
    fun primerNombre(): String = nombre.trim().substringBefore(" ")

    // "Tamara Rojas Espinoza" -> "Tamara Rojas"
    fun nombreCorto(): String = nombre.trim().split(" ").take(2).joinToString(" ")
}
