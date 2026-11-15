package com.example.rentail_conteo.model

data class RecuperarContrasenaUIState(
    val paso: Int = 1,
    val rut: String = "",
    val errorRut: String? = null,
    val correoOculto: String = "",
    val codigo: String = "",
    val errorCodigo: String? = null,
    val nueva: String = "",
    val errorNueva: String? = null,
    val confirmar: String = "",
    val errorConfirmar: String? = null,
    val enviando: Boolean = false,
    val listo: Boolean = false,
    val mensaje: String? = null
)
