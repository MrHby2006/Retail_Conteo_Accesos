package com.example.rentail_conteo.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rentail_conteo.ui.screens.DivisionScreen
import com.example.rentail_conteo.ui.screens.LoginScreen

// NavController = sabe en qué pantalla estamos y guarda el historial.
// NavHost = dibuja la pantalla de la ruta actual.
@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Ruta.Login.ruta) {

        composable(Ruta.Login.ruta) {
            LoginScreen(
                onIngresoExitoso = {
                    navController.navigate(Ruta.Division.ruta) {
                        // Saca el login del historial: con "atrás" no se vuelve al login
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Ruta.Division.ruta) {
            DivisionScreen(viewModel = viewModel())
        }
    }
}
