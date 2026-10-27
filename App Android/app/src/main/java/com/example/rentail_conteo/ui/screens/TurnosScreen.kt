package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.model.Franjas
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.components.SeleccionChips
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.util.Fechas
import com.example.rentail_conteo.viewmodel.TurnosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurnosScreen(
    supervisor: Usuario,
    onCerrarSesion: () -> Unit,
    viewModel: TurnosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scroll = rememberScrollState()

    val hoyMillis = remember { Fechas.aMillisUtc(Fechas.hoy()) }
    val calendario = rememberDatePickerState(
        initialSelectedDateMillis = Fechas.aMillisUtc(uiState.diaElegido),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= hoyMillis
        }
    )
    LaunchedEffect(calendario.selectedDateMillis) {
        calendario.selectedDateMillis?.let { viewModel.onDiaElegido(Fechas.desdeMillisUtc(it)) }
    }

    LaunchedEffect(uiState.turnoEnEdicion) {
        if (uiState.turnoEnEdicion != null) scroll.animateScrollTo(0)
    }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Turnos · ${supervisor.primerNombre()}", onCerrarSesion = onCerrarSesion) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scroll)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            val enEdicion = uiState.turnoEnEdicion

            if (enEdicion == null) {
                Text("Día", style = MaterialTheme.typography.titleSmall)
                DatePicker(
                    state = calendario,
                    title = null,
                    headline = null,
                    showModeToggle = false
                )
            } else {
                Text(
                    "Modificando el turno de ${enEdicion.nombreTrabajador} · ${Fechas.diaCorto(enEdicion.fecha)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Si la persona no puede ir, elige a quien la reemplaza.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (enEdicion == null && uiState.esHoy) {
                Text(
                    "Hoy no se pueden asignar turnos nuevos. Si alguien no puede venir, toca Modificar en su turno.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                SeleccionChips(
                    titulo = "Trabajador",
                    opciones = uiState.trabajadores,
                    seleccionada = uiState.trabajadorElegido,
                    onSeleccionar = viewModel::onTrabajadorElegido,
                    textoDe = { it.nombreCorto() },
                    error = uiState.errorTrabajador
                )
                SeleccionChips(
                    titulo = "Horario",
                    opciones = Franjas.todas,
                    seleccionada = uiState.franjaElegida,
                    onSeleccionar = viewModel::onFranjaElegida,
                    textoDe = { "$it (${Franjas.horario(it)})" },
                    error = uiState.errorFranja
                )
                SeleccionChips(
                    titulo = "Puesto",
                    opciones = uiState.puestos,
                    seleccionada = uiState.puestoElegido,
                    onSeleccionar = viewModel::onPuestoElegido,
                    textoDe = { if (it == Turno.PUESTO_CAJA) "Caja" else "Puerta $it" },
                    error = uiState.errorPuesto
                )

                Boton(
                    texto = if (enEdicion == null) "Asignar turno" else "Guardar cambios",
                    onClick = { viewModel.onGuardar(supervisor.id) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (enEdicion != null) {
                    TextButton(
                        onClick = viewModel::onCancelarModificacion,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cancelar") }
                }
            }

            HorizontalDivider()

            Text(
                "Turnos del ${Fechas.diaCorto(uiState.diaElegido)}",
                style = MaterialTheme.typography.titleMedium
            )
            if (uiState.turnosDelDia.isEmpty()) {
                Text("Todavía no hay turnos asignados este día.", style = MaterialTheme.typography.bodyMedium)
            }
            uiState.turnosDelDia.forEach { turno ->
                TarjetaTurno(
                    turno = turno,
                    enEdicion = turno.id == enEdicion?.id,
                    onModificar = { viewModel.onModificar(turno) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaTurno(turno: Turno, enEdicion: Boolean, onModificar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(turno.nombreTrabajador, fontWeight = FontWeight.Bold)
                Text(
                    "${turno.franja} (${Franjas.horario(turno.franja)}) · ${turno.puestoLegible()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            when {
                enEdicion -> Text("Editando", style = MaterialTheme.typography.labelLarge)
                turno.yaTermino() -> Text("Terminado", style = MaterialTheme.typography.labelLarge)
                else -> TextButton(onClick = onModificar) { Text("Modificar") }
            }
        }
    }
}
