package com.example.rentail_conteo.model

object Rol {
    const val JEFATURA = 1L
    const val SUPERVISOR = 2L
    const val OBSERVADOR = 3L
    const val CAJERO = 4L
}

data class Usuario(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val rut: String,
    val cargo: String,
    val rolId: Long
) {
    fun primerNombre(): String = nombre.trim().substringBefore(" ")

    fun nombreCorto(): String = nombre.trim().split(" ").take(2).joinToString(" ")
}
