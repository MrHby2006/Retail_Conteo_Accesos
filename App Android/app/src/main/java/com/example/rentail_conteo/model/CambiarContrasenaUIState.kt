package com.example.rentail_conteo.model

data class CambiarContrasenaUIState(
    val nueva: String = "",
    val confirmar: String = "",
    val errorNueva: String? = null,
    val errorConfirmar: String? = null,
    val usuarioActualizado: Usuario? = null
)
