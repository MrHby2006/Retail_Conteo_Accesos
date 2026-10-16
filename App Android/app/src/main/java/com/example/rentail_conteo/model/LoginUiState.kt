package com.example.rentail_conteo.model

// Estado de la pantalla de login: lo escrito en cada campo y sus errores.
// null en un error = ese campo está bien.
data class LoginUiState(
    val rut: String = "",
    val contrasena: String = "",
    val errorRut: String? = null,
    val errorContrasena: String? = null
)
