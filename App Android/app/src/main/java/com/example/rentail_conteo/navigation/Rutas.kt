package com.example.rentail_conteo.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")

    data object RecuperarContrasena : Ruta("recuperarContrasena")

    data object Division : Ruta("division")

    data object Indicadores : Ruta("indicadores/{idAcceso}") {
        fun conAcceso(idAcceso: Int) = "indicadores/$idAcceso"
    }

    data object CambiarContrasena : Ruta("cambiarContrasena")

    data object Admin : Ruta("admin")

    data object Empleados : Ruta("empleados")

    data object Turnos : Ruta("turnos")

    data object Horario : Ruta("horario")

    data object InicioTrabajador : Ruta("inicioTrabajador")

    data object Porton : Ruta("porton")

    data object RegistrosClientes : Ruta("registroClientes")

    data object ListaTrabajadores : Ruta("listaTrabajadores")

    data object EditarTrabajador : Ruta("editarTrabajador/{idTrabajador}") {
        fun crearRuta(idTrabajador: Long) = "editarTrabajador/$idTrabajador"
    }

    data object CrearTrabajador : Ruta("crearTrabajador")
}
