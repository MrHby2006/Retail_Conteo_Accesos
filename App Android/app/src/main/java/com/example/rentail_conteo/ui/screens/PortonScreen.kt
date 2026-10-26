package com.example.rentail_conteo.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Opciones
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.components.CampoReferencia
import com.example.rentail_conteo.ui.components.InterruptorConTexto
import com.example.rentail_conteo.ui.components.SeleccionChips
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.PortonViewModel

@Composable
fun PortonScreen(
    observador: Usuario,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit,
    viewModel: PortonViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(observador.id) { viewModel.cargarTurno(observador.id) }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(titulo = "Puerta · ${observador.primerNombre()}", onCerrarSesion = onCerrarSesion, onVolver = onVolver)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text(
                "Tu turno de hoy: ${uiState.turnoHoy ?: "sin turno asignado"}",
                style = MaterialTheme.typography.bodyMedium
            )

            SeleccionChips(
                titulo = "¿En qué acceso estás?",
                opciones = uiState.accesos,
                seleccionada = uiState.accesoElegido,
                onSeleccionar = viewModel::onAccesoElegido,
                textoDe = { "${it.codigo} · ${it.nombre}" },
                error = uiState.errorAcceso
            )

            uiState.accesoElegido?.let { acceso ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Registrados hoy en ${acceso.codigo}: ", style = MaterialTheme.typography.bodyLarge)
                    AnimatedContent(
                        targetState = uiState.registradosHoy,
                        transitionSpec = { slideInVertically { it } togetherWith slideOutVertically { -it } },
                        label = "contador"
                    ) { total ->
                        Text("$total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            SeleccionChips(
                titulo = "Rango de edad (a la vista)",
                opciones = Opciones.rangosEtarios,
                seleccionada = uiState.rangoEtario,
                onSeleccionar = viewModel::onRangoElegido,
                textoDe = { it },
                error = uiState.errorRango
            )
            SeleccionChips(
                titulo = "Altura",
                opciones = Opciones.alturas,
                seleccionada = uiState.altura,
                onSeleccionar = viewModel::onAlturaElegida,
                textoDe = { it },
                error = uiState.errorAltura
            )
            SeleccionChips(
                titulo = "Color de la ropa (parte de arriba)",
                opciones = Opciones.coloresRopa,
                seleccionada = uiState.colorRopa,
                onSeleccionar = viewModel::onColorElegido,
                textoDe = { it },
                error = uiState.errorColor
            )
            SeleccionChips(
                titulo = "Color de la ropa (parte de abajo)",
                opciones = Opciones.coloresRopa,
                seleccionada = uiState.colorRopaAbajo,
                onSeleccionar = viewModel::onColorAbajoElegido,
                textoDe = { it },
                error = uiState.errorColorAbajo
            )

            InterruptorConTexto(
                texto = "¿Viene acompañado?",
                activado = uiState.acompanado,
                onCambio = viewModel::onAcompanadoCambia
            )
            CampoReferencia(
                valor = uiState.detalle,
                onValorCambia = viewModel::onDetalleCambia,
                etiqueta = "Detalle para reconocerlo (opcional)",
                icono = R.drawable.ic_persona,
                error = uiState.errorDetalle
            )

            Boton(
                texto = "Registrar ingreso",
                onClick = { viewModel.onRegistrar(observador.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
