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
import com.example.rentail_conteo.ui.screens.DivisionScreen
import com.example.rentail_conteo.ui.screens.EleccionPantallaScreen
import com.example.rentail_conteo.ui.screens.IndicadoresScreen
import com.example.rentail_conteo.ui.screens.LoginScreen
import com.example.rentail_conteo.ui.screens.PortonScreen
import com.example.rentail_conteo.ui.screens.RegistrosClientesScreen
import com.example.rentail_conteo.ui.screens.TurnosScreen
import com.example.rentail_conteo.viewmodel.SesionViewModel

// NavController = sabe en qué pantalla estamos y guarda el historial.
// NavHost = dibuja la pantalla de la ruta actual.
//
//   Login ─┬─ Observador / Cajero ─▶ Eleccion ─┬─▶ Porton (puerta)
//          │                                   └─▶ RegistrosClientes (caja)
//          ├─ Supervisor ─▶ Turnos
//          └─ Jefatura ───▶ Division ──(elige acceso)──▶ Indicadores/{idAcceso}
@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    // Se crea UNA vez aquí: todas las pantallas saben quién inició sesión
    val sesionViewModel: SesionViewModel = viewModel()
    val usuario by sesionViewModel.usuarioActual.collectAsStateWithLifecycle()

    // SharedPreferences: si había una sesión guardada, se parte directo en la pantalla de su rol
    val inicio = remember { rutaInicial(sesionViewModel.usuarioActual.value) }

    NavHost(navController = navController, startDestination = inicio) {

        composable(Ruta.Login.ruta) {
            LoginScreen(
                onIngreso = { usuarioIngresado ->
                    sesionViewModel.iniciarSesion(usuarioIngresado)
                    navController.navigate(rutaInicial(usuarioIngresado)) {
                        // Saca el login del historial: con "atrás" no se vuelve al login
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Ruta.Division.ruta) {
            DivisionScreen(
                viewModel = viewModel(),
                onConsultarClicked = { idAcceso -> navController.navigate(Ruta.Indicadores.conAcceso(idAcceso)) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        // navArgument: se declara que {idAcceso} es un número entero
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
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Eleccion.ruta) {
            // ?: return@composable = si no hay usuario (recién cerró sesión), no dibuja nada
            val actual = usuario ?: return@composable
            EleccionPantallaScreen(
                trabajador = actual,
                onIrAPuerta = { navController.navigate(Ruta.Porton.ruta) },
                onIrACaja = { navController.navigate(Ruta.RegistrosClientes.ruta) },
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

// Cada perfil parte en su propia pantalla. Observadores y cajeros, por ahora, eligen ellos
// entre puerta y caja (a futuro: según el turno que les asignó el supervisor).
private fun rutaInicial(usuario: Usuario?): String = when (usuario?.rolId) {
    null -> Ruta.Login.ruta
    Rol.OBSERVADOR, Rol.CAJERO -> Ruta.Eleccion.ruta
    Rol.SUPERVISOR -> Ruta.Turnos.ruta
    else -> Ruta.Division.ruta
}

// Borra la sesión y vuelve al login sin dejar pantallas atrás (popUpTo(0) = vacía el historial)
private fun cerrarSesion(sesion: SesionViewModel, navController: NavHostController) {
    sesion.cerrarSesion()
    navController.navigate(Ruta.Login.ruta) { popUpTo(0) }
}
