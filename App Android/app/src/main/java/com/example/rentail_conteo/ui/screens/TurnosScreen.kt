package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun TurnosScreen(
    supervisor: Usuario,
    onVerHorario: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: TurnosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scroll = rememberScrollState()

    LaunchedEffect(uiState.turnoEnEdicion) {
        if (uiState.turnoEnEdicion != null) scroll.animateScrollTo(0)
    }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    if (uiState.pidiendoPublicar) {
        AlertDialog(
            onDismissRequest = viewModel::onCancelarPublicar,
            title = { Text("Publicar horario") },
            text = {
                Text(
                    "Se publicarán ${uiState.borradoresSemana} turno(s) de la semana del " +
                            "${Fechas.diaCorto(uiState.diasSemana.first())}. Los trabajadores podrán verlos y " +
                            "después cualquier cambio necesitará que lo acepten."
                )
            },
            confirmButton = { TextButton(onClick = viewModel::onConfirmarPublicar) { Text("Publicar") } },
            dismissButton = { TextButton(onClick = viewModel::onCancelarPublicar) { Text("Cancelar") } }
        )
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

            TextButton(onClick = onVerHorario) { Text("Ver horario publicado") }

            SeleccionChips(
                titulo = "Semana",
                opciones = listOf(false, true),
                seleccionada = uiState.esProximaSemana,
                onSeleccionar = viewModel::onSemanaElegida,
                textoDe = { if (it) "Próxima semana" else "Esta semana" }
            )
            if (uiState.diasSemana.isNotEmpty()) {
                SeleccionChips(
                    titulo = "Día (${Fechas.diaCorto(uiState.diasSemana.first())} al ${Fechas.diaCorto(uiState.diasSemana.last())})",
                    opciones = uiState.diasSemana,
                    seleccionada = uiState.diaElegido,
                    onSeleccionar = viewModel::onDiaElegido,
                    textoDe = { Fechas.diaCorto(it) }
                )
            }

            if (enEdicion != null) {
                Text(
                    "Modificando el turno de ${enEdicion.nombreTrabajador} · ${Fechas.diaCorto(enEdicion.fecha)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    if (enEdicion.esBorrador) "Es un borrador: el cambio se guarda de inmediato."
                    else "El turno ya está publicado: se enviará una solicitud y el cambio se aplica cuando los trabajadores la acepten.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (enEdicion == null && !uiState.esProximaSemana) {
                Text(
                    "Esta semana ya está publicada. Si alguien no puede venir, toca Modificar en su turno: " +
                            "el cambio necesita que los trabajadores lo acepten.",
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
                    texto = when {
                        enEdicion == null -> "Agregar turno"
                        enEdicion.esBorrador -> "Guardar cambios"
                        else -> "Enviar solicitud de cambio"
                    },
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

            if (uiState.esProximaSemana && enEdicion == null) {
                Boton(
                    texto = "Publicar semana (${uiState.borradoresSemana} en borrador)",
                    onClick = viewModel::onPedirPublicar,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider()

            Text(
                "Turnos del ${Fechas.diaCorto(uiState.diaElegido)}",
                style = MaterialTheme.typography.titleMedium
            )
            if (uiState.turnosDelDia.isEmpty()) {
                Text("Todavía no hay turnos este día.", style = MaterialTheme.typography.bodyMedium)
            }
            uiState.turnosDelDia.forEach { turno ->
                TarjetaTurno(
                    turno = turno,
                    enEdicion = turno.id == enEdicion?.id,
                    onModificar = { viewModel.onModificar(turno) },
                    onQuitar = { viewModel.onQuitarBorrador(turno) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaTurno(turno: Turno, enEdicion: Boolean, onModificar: () -> Unit, onQuitar: () -> Unit) {
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
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            when {
                                turno.cambioPendiente -> "Cambio esperando respuesta"
                                turno.esBorrador -> "Borrador"
                                else -> "Publicado"
                            }
                        )
                    }
                )
            }
            when {
                enEdicion -> Text("Editando", style = MaterialTheme.typography.labelLarge)
                turno.yaTermino() -> Text("Terminado", style = MaterialTheme.typography.labelLarge)
                turno.cambioPendiente -> Unit
                else -> Column(horizontalAlignment = Alignment.End) {
                    TextButton(onClick = onModificar) { Text("Modificar") }
                    if (turno.esBorrador) TextButton(onClick = onQuitar) { Text("Quitar") }
                }
            }
        }
    }
}
