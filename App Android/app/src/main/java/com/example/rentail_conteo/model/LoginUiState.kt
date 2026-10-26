package com.example.rentail_conteo.model

data class LoginUiState(
    val rut: String = "",
    val contrasena: String = "",
    val errorRut: String? = null,
    val errorContrasena: String? = null
)
