package com.example.rentail_conteo.navigation

// Nombre único de cada pantalla, como las URL de una web.
sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")

    // Jefatura: elige el acceso a consultar
    data object Division : Ruta("division")

    // Jefatura: indicadores de un acceso. {idAcceso} es un ARGUMENTO, como en una URL:
    // "indicadores/4" = acceso A4, "indicadores/0" = todos los accesos
    data object Indicadores : Ruta("indicadores/{idAcceso}") {
        fun conAcceso(idAcceso: Int) = "indicadores/$idAcceso"
    }

    // Supervisor de piso: asigna turnos (puerta o caja) a los trabajadores
    data object Turnos : Ruta("turnos")

    // Trabajador de piso: elige si hoy usa la pantalla de puerta o la de caja
    data object Eleccion : Ruta("eleccion")

    // Observador: registra a las personas que entran por la puerta
    data object Porton : Ruta("porton")

    // Cajero: marca quién compró y adjunta la boleta
    data object RegistrosClientes : Ruta("registroClientes")
}
