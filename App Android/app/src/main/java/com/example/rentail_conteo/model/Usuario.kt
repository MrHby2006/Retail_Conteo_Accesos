package com.example.rentail_conteo.model

import com.example.rentail_conteo.util.Fechas

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

    fun codigo(rolId: Long): String = when (rolId) {
        ADMINISTRADOR -> "ADMIN"
        SUPERVISOR -> "SUPERVISOR"
        else -> "TRABAJADOR"
    }

    fun desdeCodigo(codigo: String): Long = when (codigo) {
        "ADMIN" -> ADMINISTRADOR
        "SUPERVISOR" -> SUPERVISOR
        else -> TRABAJADOR
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
    val debeCambiarContrasena: Boolean = false,
    val fotoPerfil: String? = null,
    val fechaNacimiento: String? = null,
    val nombres: String = nombre.substringBefore(" "),
    val apellidoPaterno: String = "",
    val apellidoMaterno: String? = null,
    val telefono: String? = null
) {
    fun primerNombre(): String = nombre.trim().substringBefore(" ")

    fun nombreCorto(): String = nombre.trim().split(" ").take(2).joinToString(" ")

    fun rutFormateado(): String = if (rut.length < 2) rut else "${rut.dropLast(1)}-${rut.last()}"

    fun esCumpleanosHoy(): Boolean =
        fechaNacimiento != null && fechaNacimiento.drop(5) == Fechas.hoy().drop(5)
}
