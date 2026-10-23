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
// onVolver: si viene, aparece una flecha a la izquierda (null = pantalla principal del rol, sin flecha).
// (mismos colores que la barra de DivisionScreen)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onCerrarSesion: () -> Unit, onVolver: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(painterResource(R.drawable.ic_volver), contentDescription = "Volver")
                }
            }
        },
        actions = {
            IconButton(onClick = onCerrarSesion) {
                Icon(painterResource(R.drawable.ic_salir), contentDescription = "Cerrar sesión")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
