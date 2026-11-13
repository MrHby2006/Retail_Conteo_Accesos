package com.example.rentail_conteo.model

object Rol {
    const val ADMINISTRADOR = 1L
    const val SUPERVISOR = 2L
    const val TRABAJADOR = 3L

    val asignables = listOf(SUPERVISOR, TRABAJADOR)

    fun nombre(rolId: Long): String = when (rolId) {
        ADMINISTRADOR -> "Administrador"
        SUPERVISOR -> "Supervisor de piso"
        else -> "Trabajador"
    }
}

data class Usuario(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val rut: String,
    val correo: String,
    val cargo: String,
    val rolId: Long,
    val correoPersonal: String? = null,
    val debeCambiarContrasena: Boolean = false
) {
    fun primerNombre(): String = nombre.trim().substringBefore(" ")

    fun nombreCorto(): String = nombre.trim().split(" ").take(2).joinToString(" ")
}
