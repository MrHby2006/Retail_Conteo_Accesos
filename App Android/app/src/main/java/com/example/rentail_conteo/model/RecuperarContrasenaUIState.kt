package com.example.rentail_conteo.model

data class RecuperarContrasenaUIState(
    val correo: String = "",
    val errorCorreo: String? = null,
    val mensajeExito: Boolean = false
)
