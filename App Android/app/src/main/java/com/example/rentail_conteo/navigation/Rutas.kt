package com.example.rentail_conteo.navigation

// Nombre único de cada pantalla, como las URL de una web.
sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")
    data object Division : Ruta("division")

    data object Porton : Ruta("porton")

    data object RegistrosClientes : Ruta("registroClientes")
}
