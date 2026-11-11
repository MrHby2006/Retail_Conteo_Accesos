package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.InterruptorConTexto
import com.example.rentail_conteo.ui.components.SeleccionChips
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.util.Fechas
import com.example.rentail_conteo.viewmodel.HorarioViewModel

@Composable
fun HorarioScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: HorarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BarraSuperior(titulo = "Horario de turnos", onCerrarSesion = onCerrarSesion, onVolver = onVolver) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            item {
                SeleccionChips(
                    titulo = "Semana",
                    opciones = listOf(false, true),
                    seleccionada = uiState.esProximaSemana,
                    onSeleccionar = viewModel::onSemanaElegida,
                    textoDe = { if (it) "Próxima semana" else "Esta semana" },
                    modifier = Modifier.padding(top = Dimens.espacioMedio)
                )
            }
            item {
                InterruptorConTexto(
                    texto = "Ver solo mis turnos",
                    activado = uiState.soloMisTurnos,
                    onCambio = viewModel::onSoloMisTurnos
                )
            }
            if (uiState.turnos.isEmpty()) {
                item {
                    Text(
                        if (uiState.esProximaSemana) "El supervisor todavía no publica el horario de la próxima semana."
                        else "No hay turnos publicados esta semana.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (uiState.turnos.isNotEmpty()) {
                items(uiState.diasSemana) { dia ->
                    DiaDelHorario(dia = dia, turnos = uiState.turnosDe(dia, usuario.id), idUsuario = usuario.id)
                }
            }
        }
    }
}

@Composable
private fun DiaDelHorario(dia: String, turnos: List<Turno>, idUsuario: Long) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)) {
        Text(
            Fechas.diaCorto(dia) + if (dia == Fechas.hoy()) " · Hoy" else "",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (turnos.isEmpty()) {
            Text("Sin turnos", style = MaterialTheme.typography.bodySmall)
        }
        turnos.forEach { turno ->
            val esMio = turno.idUsuario == idUsuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = if (esMio) {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    CardDefaults.cardColors()
                }
            ) {
                Column(Modifier.padding(Dimens.espacioMedio)) {
                    Text(
                        turno.nombreTrabajador + if (esMio) " (tú)" else "",
                        fontWeight = if (esMio) FontWeight.Bold else FontWeight.Normal
                    )
                    Text("${turno.franja} · ${turno.puestoLegible()}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
