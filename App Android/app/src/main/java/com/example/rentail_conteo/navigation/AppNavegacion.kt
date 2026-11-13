package com.example.rentail_conteo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rentail_conteo.model.Rol
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.screens.AdminScreen
import com.example.rentail_conteo.ui.screens.CambiarContrasenaScreen
import com.example.rentail_conteo.ui.screens.CrearTrabajadorScreen
import com.example.rentail_conteo.ui.screens.DivisionScreen
import com.example.rentail_conteo.ui.screens.EmpleadosScreen
import com.example.rentail_conteo.ui.screens.HorarioScreen
import com.example.rentail_conteo.ui.screens.IndicadoresScreen
import com.example.rentail_conteo.ui.screens.InicioTrabajadorScreen
import com.example.rentail_conteo.ui.screens.ListaTrabajadoresScreen
import com.example.rentail_conteo.ui.screens.LoginScreen
import com.example.rentail_conteo.ui.screens.PortonScreen
import com.example.rentail_conteo.ui.screens.RecuperarContrasenaScreen
import com.example.rentail_conteo.ui.screens.RegistrosClientesScreen
import com.example.rentail_conteo.ui.screens.TurnosScreen
import com.example.rentail_conteo.viewmodel.SesionViewModel

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    val sesionViewModel: SesionViewModel = viewModel()
    val usuario by sesionViewModel.usuarioActual.collectAsStateWithLifecycle()

    val inicio = remember { rutaInicial(sesionViewModel.usuarioActual.value) }

    NavHost(navController = navController, startDestination = inicio) {
        composable(Ruta.Login.ruta) {
            LoginScreen(
                onIngreso = { usuarioIngresado ->
                    sesionViewModel.iniciarSesion(usuarioIngresado)
                    navController.navigate(rutaInicial(usuarioIngresado)) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                onRecuperarPasswordClick = { navController.navigate(Ruta.RecuperarContrasena.ruta) }
            )
        }

        composable(Ruta.RecuperarContrasena.ruta) {
            RecuperarContrasenaScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Ruta.CambiarContrasena.ruta) {
            val actual = usuario ?: return@composable
            CambiarContrasenaScreen(
                usuario = actual,
                onListo = { actualizado ->
                    sesionViewModel.iniciarSesion(actualizado)
                    navController.navigate(rutaInicial(actualizado)) { popUpTo(0) }
                },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Admin.ruta) {
            val actual = usuario ?: return@composable
            AdminScreen(
                admin = actual,
                onIndicadores = { navController.navigate(Ruta.Division.ruta) },
                onEmpleados = { navController.navigate(Ruta.Empleados.ruta) },
                onHorario = { navController.navigate(Ruta.Horario.ruta) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Empleados.ruta) {
            val actual = usuario ?: return@composable
            EmpleadosScreen(
                admin = actual,
                onVolver = { navController.popBackStack() },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Horario.ruta) {
            val actual = usuario ?: return@composable
            HorarioScreen(
                usuario = actual,
                onVolver = { navController.popBackStack() },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Division.ruta) {
            DivisionScreen(
                viewModel = viewModel(),
                onConsultarClicked = { idAcceso -> navController.navigate(Ruta.Indicadores.conAcceso(idAcceso)) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) },
                onVerTrabajadoresClicked = { navController.navigate(Ruta.ListaTrabajadores.ruta) },
                onCrearTrabajadorClicked = { navController.navigate(Ruta.CrearTrabajador.ruta) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Ruta.ListaTrabajadores.ruta) {
            ListaTrabajadoresScreen(
                onVolver = { navController.popBackStack() },
                onModificarTrabajador = { }
            )
        }

        composable(Ruta.CrearTrabajador.ruta) {
            CrearTrabajadorScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Ruta.Indicadores.ruta,
            arguments = listOf(navArgument("idAcceso") { type = NavType.IntType })
        ) { entrada ->
            IndicadoresScreen(
                idAcceso = entrada.arguments?.getInt("idAcceso") ?: 0,
                onVolver = { navController.popBackStack() },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Turnos.ruta) {
            val actual = usuario ?: return@composable
            TurnosScreen(
                supervisor = actual,
                onVerHorario = { navController.navigate(Ruta.Horario.ruta) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.InicioTrabajador.ruta) {
            val actual = usuario ?: return@composable
            InicioTrabajadorScreen(
                trabajador = actual,
                onIrAPuerta = { navController.navigate(Ruta.Porton.ruta) },
                onIrACaja = { navController.navigate(Ruta.RegistrosClientes.ruta) },
                onVerHorario = { navController.navigate(Ruta.Horario.ruta) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Porton.ruta) {
            val actual = usuario ?: return@composable
            PortonScreen(
                observador = actual,
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Ruta.RegistrosClientes.ruta) {
            val actual = usuario ?: return@composable
            RegistrosClientesScreen(
                cajero = actual,
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}

private fun rutaInicial(usuario: Usuario?): String = when {
    usuario == null -> Ruta.Login.ruta
    usuario.debeCambiarContrasena -> Ruta.CambiarContrasena.ruta
    usuario.rolId == Rol.ADMINISTRADOR -> Ruta.Admin.ruta
    usuario.rolId == Rol.SUPERVISOR -> Ruta.Turnos.ruta
    else -> Ruta.InicioTrabajador.ruta
}

private fun cerrarSesion(sesion: SesionViewModel, navController: NavHostController) {
    sesion.cerrarSesion()
    navController.navigate(Ruta.Login.ruta) { popUpTo(0) }
}
