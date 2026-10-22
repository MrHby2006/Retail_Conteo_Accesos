package com.example.rentail_conteo.navigation

// Nombre único de cada pantalla, como las URL de una web.
sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")

    // Jefatura (y por ahora también el supervisor): consulta por acceso
    data object Division : Ruta("division")

    // Observador: registra a las personas que entran por la puerta
    data object Porton : Ruta("porton")

    // Cajero: marca quién compró y adjunta la boleta
    data object RegistrosClientes : Ruta("registroClientes")
}
