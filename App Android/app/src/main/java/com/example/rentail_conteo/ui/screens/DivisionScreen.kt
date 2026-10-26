package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.*
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
    onConsultarClicked: (idAcceso: Int) -> Unit = {},
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
            Text(
                text = "Seleccione el acceso a consultar:",
                style = MaterialTheme.typography.bodyMedium
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                uiState.accesosDisponibles.forEach { acceso ->
                    val esSeleccionado = uiState.accesoSeleccionado?.id == acceso.id
                    FilterChip(
                        selected = esSeleccionado,
                        onClick = { viewModel.seleccionarAcceso(acceso) },
                        label = {
                            Text(
                                if (acceso.id == 0) acceso.nombre else "${acceso.codigo}: ${acceso.nombre}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )
                }
            }

            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))

            Boton(
                texto = "Consultar Indicadores",
                onClick = {
                    viewModel.validarSeleccion()?.let { acceso -> onConsultarClicked(acceso.id) }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}