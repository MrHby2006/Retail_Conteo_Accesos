package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.DivisionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivisionScreen(
    viewModel: DivisionViewModel,
    modifier: Modifier = Modifier,
    onConsultarClicked: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            BarraSuperior(titulo = "Consulta y Análisis por Acceso", onCerrarSesion = onCerrarSesion)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioGrande)
        ) {
            // Sección de selección de Fecha simulando el contexto del calendario
            Text(
                text = "Período seleccionado: ${uiState.diaSeleccionado} de ${uiState.mesSeleccionado}, ${uiState.anioSeleccionado}",
                style = MaterialTheme.typography.titleMedium
            )

            // Selector horizontal de Accesos (A1 a A4)
            Text(
                text = "Seleccione el acceso a consultar:",
                style = MaterialTheme.typography.bodyMedium
            )

            LazyRow(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Dimens.espacioChico)
            ) {
                items(uiState.accesosDisponibles) { acceso ->
                    val esSeleccionado = uiState.accesoSeleccionado?.id == acceso.id
                    FilterChip(
                        selected = esSeleccionado,
                        onClick = { viewModel.seleccionarAcceso(acceso) },
                        label = { Text("${acceso.codigo}: ${acceso.nombre}") }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón principal utilizando tu componente reutilizable Boton.kt
            Boton(
                texto = "Consultar Indicadores",
                onClick = {
                    if (uiState.accesoSeleccionado != null) {
                        onConsultarClicked()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}