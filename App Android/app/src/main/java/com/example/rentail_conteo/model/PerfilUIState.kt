package com.example.rentail_conteo.model

data class PerfilUIState(
    val usuario: Usuario? = null,
    val fotoUrl: String? = null,
    val subiendoFoto: Boolean = false,
    val errorFoto: String? = null,
    val fechaNacimiento: String? = null,
    val errorFecha: String? = null,
    val eligiendoFecha: Boolean = false,
    val actual: String = "",
    val errorActual: String? = null,
    val nueva: String = "",
    val errorNueva: String? = null,
    val confirmar: String = "",
    val errorConfirmar: String? = null,
    val usuarioActualizado: Usuario? = null,
    val mensaje: String? = null
)
