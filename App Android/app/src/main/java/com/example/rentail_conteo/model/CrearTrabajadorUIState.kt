package com.example.rentail_conteo.model

data class CrearTrabajadorUIState(
    val nombre: String = "",
    val rut: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val cargo: String = "",
    val rolSeleccionado: Pair<Long, String>? = null,
    val rolesDisponibles: List<Pair<Long, String>> = emptyList(),

    val errorNombre: String? = null,
    val errorRut: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorCargo: String? = null,
    val errorRol: String? = null,

    val mensajeExito: String? = null
)