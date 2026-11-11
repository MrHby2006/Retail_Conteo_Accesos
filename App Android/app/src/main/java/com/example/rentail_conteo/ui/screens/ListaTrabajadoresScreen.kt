package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.ListaTrabajadoresViewModel

@Composable
fun ListaTrabajadoresScreen(
    onVolver: () -> Unit,
    onModificarTrabajador: (idTrabajador: Long) -> Unit,
    viewModel: ListaTrabajadoresViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.cargarTrabajadores()
    }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    uiState.trabajadorAEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = viewModel::cancelarEliminacion,
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de deshabilitar a ${usuario.nombre}? Ya no podrá acceder a la aplicación.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmarEliminacion) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelarEliminacion) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Lista de Trabajadores",
                onCerrarSesion = {},
                onVolver = onVolver
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla)
        ) {
            Text(
                text = "${uiState.trabajadores.size} colaborador(es) registrado(s)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = Dimens.espacioMedio)
            )

            if (uiState.trabajadores.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay trabajadores registrados", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.trabajadores, key = { it.id }) { trabajador ->
                        TarjetaTrabajador(
                            trabajador = trabajador,
                            onModificar = { onModificarTrabajador(trabajador.id) },
                            onEliminar = { viewModel.solicitarEliminacion(trabajador) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaTrabajador(
    trabajador: Usuario,
    onModificar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trabajador.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = trabajador.codigo,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text("RUT: ${trabajador.rut}", style = MaterialTheme.typography.bodySmall)
            Text("Correo: ${trabajador.correo}", style = MaterialTheme.typography.bodySmall)
            Text("Cargo: ${trabajador.cargo}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(Dimens.espacioChico))

            // Botones Modificar y Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onModificar,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_volver), contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Modificar")
                }

                Spacer(Modifier.width(Dimens.espacioChico))

                TextButton(
                    onClick = onEliminar,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}