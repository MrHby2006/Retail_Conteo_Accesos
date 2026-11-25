package com.example.rentail_conteo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
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
import com.example.rentail_conteo.ui.screens.DivisionScreen
import com.example.rentail_conteo.ui.screens.EmpleadosScreen
import com.example.rentail_conteo.ui.screens.HorarioScreen
import com.example.rentail_conteo.ui.screens.IndicadoresScreen
import com.example.rentail_conteo.ui.screens.InicioTrabajadorScreen
import com.example.rentail_conteo.ui.screens.LoginScreen
import com.example.rentail_conteo.ui.screens.PerfilScreen
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
    val cargandoSesion by sesionViewModel.cargando.collectAsStateWithLifecycle()

    if (cargandoSesion) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

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
                onVolver = { volver(navController) },
                onListo = { volver(navController) }
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
                onPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Perfil.ruta) {
            val actual = usuario ?: return@composable
            PerfilScreen(
                usuario = actual,
                onUsuarioActualizado = { sesionViewModel.iniciarSesion(it) },
                onVolver = { volver(navController) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Empleados.ruta) {
            val actual = usuario ?: return@composable
            EmpleadosScreen(
                admin = actual,
                onVolver = { volver(navController) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Horario.ruta) {
            val actual = usuario ?: return@composable
            HorarioScreen(
                usuario = actual,
                onVolver = { volver(navController) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Division.ruta) {
            DivisionScreen(
                viewModel = viewModel(),
                onConsultarClicked = { idAcceso -> navController.navigate(Ruta.Indicadores.conAcceso(idAcceso)) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) },
                onVolver = { volver(navController) }
            )
        }

        composable(
            route = Ruta.Indicadores.ruta,
            arguments = listOf(navArgument("idAcceso") { type = NavType.IntType })
        ) { entrada ->
            IndicadoresScreen(
                idAcceso = entrada.arguments?.getInt("idAcceso") ?: 0,
                onVolver = { volver(navController) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Turnos.ruta) {
            val actual = usuario ?: return@composable
            TurnosScreen(
                supervisor = actual,
                onVerHorario = { navController.navigate(Ruta.Horario.ruta) },
                onPerfil = { navController.navigate(Ruta.Perfil.ruta) },
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
                onPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) }
            )
        }

        composable(Ruta.Porton.ruta) {
            val actual = usuario ?: return@composable
            PortonScreen(
                observador = actual,
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) },
                onVolver = { volver(navController) }
            )
        }

        composable(Ruta.RegistrosClientes.ruta) {
            val actual = usuario ?: return@composable
            RegistrosClientesScreen(
                cajero = actual,
                onCerrarSesion = { cerrarSesion(sesionViewModel, navController) },
                onVolver = { volver(navController) }
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

private fun volver(navController: NavHostController) {
    val pantallaLista = navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED
    if (pantallaLista && navController.previousBackStackEntry != null) navController.popBackStack()
}

private fun cerrarSesion(sesion: SesionViewModel, navController: NavHostController) {
    sesion.cerrarSesion()
    navController.navigate(Ruta.Login.ruta) { popUpTo(0) }
}
