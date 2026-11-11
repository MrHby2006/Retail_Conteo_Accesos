package com.example.rentail_conteo.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Franjas
import com.example.rentail_conteo.model.SolicitudCambio
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.permisos.Permisos
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.util.Fechas
import com.example.rentail_conteo.viewmodel.InicioTrabajadorViewModel

@Composable
fun InicioTrabajadorScreen(
    trabajador: Usuario,
    onIrAPuerta: () -> Unit,
    onIrACaja: () -> Unit,
    onVerHorario: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: InicioTrabajadorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(trabajador.id) { viewModel.cargar(trabajador.id) }

    val pedirPermisoNotificaciones = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (!ok) viewModel.avisarSinPermiso()
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Permisos.tiene(context, Permisos.NOTIFICACIONES)) {
            pedirPermisoNotificaciones.launch(Permisos.NOTIFICACIONES)
        }
    }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            if (uiState.mensajeEsDePermiso) {
                val r = snackbarHostState.showSnackbar(it, "Ajustes", duration = SnackbarDuration.Long)
                if (r == SnackbarResult.ActionPerformed) Permisos.abrirAjustes(context)
            } else {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Hola, ${trabajador.primerNombre()}", onCerrarSesion = onCerrarSesion) },
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
            val turno = uiState.turnoHoy
            if (turno != null) {
                TarjetaMiPuesto(
                    turno = turno,
                    onIr = if (turno.puesto == Turno.PUESTO_CAJA) onIrACaja else onIrAPuerta
                )
                TextButton(onClick = viewModel::probarAviso) {
                    Icon(painterResource(R.drawable.ic_campana), contentDescription = null)
                    Text("  Probar aviso de fin de turno")
                }
            } else {
                Text("Hoy no tienes más turnos asignados", style = MaterialTheme.typography.titleMedium)
                Text(
                    uiState.proximoTurno?.let {
                        "Tu próximo turno: ${Fechas.diaCorto(it.fecha)} · ${it.franja} (${Franjas.horario(it.franja)}) · ${it.puestoLegible()}"
                    } ?: "Todavía no tienes turnos publicados para los próximos días.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (uiState.solicitudes.isNotEmpty()) {
                Text("Cambios de turno por confirmar", style = MaterialTheme.typography.titleMedium)
                uiState.solicitudes.forEach { solicitud ->
                    TarjetaSolicitud(
                        solicitud = solicitud,
                        idTrabajador = trabajador.id,
                        onAceptar = { viewModel.responder(solicitud, trabajador.id, acepta = true) },
                        onRechazar = { viewModel.responder(solicitud, trabajador.id, acepta = false) }
                    )
                }
            }

            Boton(texto = "Ver horario de la semana", onClick = onVerHorario, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun TarjetaMiPuesto(turno: Turno, onIr: () -> Unit) {
    val esCaja = turno.puesto == Turno.PUESTO_CAJA
    Card(
        onClick = onIr,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier.padding(Dimens.espacioGrande),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Icon(
                painterResource(if (esCaja) R.drawable.ic_camara else R.drawable.ic_persona),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Column {
                Text("Tu puesto de hoy", style = MaterialTheme.typography.bodySmall)
                Text(turno.puestoLegible(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${turno.franja} (${Franjas.horario(turno.franja)})", style = MaterialTheme.typography.bodyMedium)
                Text("Toca para entrar", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun TarjetaSolicitud(
    solicitud: SolicitudCambio,
    idTrabajador: Long,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.espacioMedio), verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
            Text(solicitud.descripcionPara(idTrabajador), style = MaterialTheme.typography.bodyMedium)
            if (solicitud.cambiaPersona) {
                Text(
                    "El cambio se aplica cuando ambos lo acepten.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                OutlinedButton(onClick = onRechazar) { Text("Rechazar") }
                OutlinedButton(onClick = onAceptar) { Text("Aceptar") }
            }
        }
    }
}
