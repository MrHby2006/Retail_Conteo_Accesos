package com.example.rentail_conteo.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")

    data object RecuperarContrasena : Ruta("recuperarContrasena")

    data object Division : Ruta("division")

    data object Indicadores : Ruta("indicadores/{idAcceso}") {
        fun conAcceso(idAcceso: Int) = "indicadores/$idAcceso"
    }

    data object Turnos : Ruta("turnos")

    data object Eleccion : Ruta("eleccion")

    data object Porton : Ruta("porton")

    data object RegistrosClientes : Ruta("registroClientes")

    data object ListaTrabajadores : Ruta("listaTrabajadores")

    data object EditarTrabajador : Ruta("editarTrabajador/{idTrabajador}") {
        fun crearRuta(idTrabajador: Long) = "editarTrabajador/$idTrabajador"
    }

    data object CrearTrabajador : Ruta("crearTrabajador")
}
