package com.example.rentail_conteo.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.rentail_conteo.R

// Barra de arriba de cada pantalla: título y botón para cerrar sesión.
// (mismos colores que la barra de DivisionScreen)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onCerrarSesion: () -> Unit) {
    TopAppBar(
        title = { Text(titulo) },
        actions = {
            IconButton(onClick = onCerrarSesion) {
                Icon(painterResource(R.drawable.ic_salir), contentDescription = "Cerrar sesión")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
