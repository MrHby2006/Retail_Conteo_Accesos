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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Franjas
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.permisos.Permisos
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.EleccionViewModel

// Por ahora TODOS los trabajadores de piso pueden usar las dos pantallas: aquí eligen cuál.
// (Más adelante, el turno que asigna el supervisor decidirá cuál ven, sin preguntar.)
@Composable
fun EleccionPantallaScreen(
    trabajador: Usuario,
    onIrAPuerta: () -> Unit,
    onIrACaja: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: EleccionViewModel = viewModel()
) {
    val turno by viewModel.turnoHoy.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(trabajador.id) { viewModel.cargar(trabajador.id) }

    // PERMISOS paso 3: pedir. Solo existe desde Android 13; en versiones anteriores ya viene concedido.
    // Paso 4: si lo rechaza, se avisa con un botón "Ajustes" (por si ya no se muestra el diálogo).
    val pedirPermisoNotificaciones = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (!ok) viewModel.avisarSinPermiso()
    }
    LaunchedEffect(Unit) {
        // Paso 2: verificar antes de pedir
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Permisos.tiene(context, Permisos.NOTIFICACIONES)) {
            pedirPermisoNotificaciones.launch(Permisos.NOTIFICACIONES)
        }
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            val r = snackbarHostState.showSnackbar(it, "Ajustes", duration = SnackbarDuration.Long)
            if (r == SnackbarResult.ActionPerformed) Permisos.abrirAjustes(context)
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
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text(
                turno?.let { "Tu turno de hoy: ${it.franja} (${Franjas.horario(it.franja)}) · ${it.puestoLegible()}" }
                    ?: "Hoy no tienes turno asignado",
                style = MaterialTheme.typography.bodyLarge
            )
            // El aviso real llega entre 15 y 5 minutos antes del fin del turno. Este botón lo muestra al tiro (para la demo).
            if (turno != null) {
                TextButton(onClick = viewModel::probarAviso) {
                    Icon(painterResource(R.drawable.ic_campana), contentDescription = null)
                    Text("  Probar aviso de fin de turno")
                }
            }
            Text("¿Dónde vas a trabajar?", style = MaterialTheme.typography.titleMedium)

            OpcionPantalla(
                titulo = "Puerta",
                detalle = "Registrar a las personas que entran",
                icono = R.drawable.ic_persona,
                sugerida = turno != null && turno?.puesto != Turno.PUESTO_CAJA,
                onClick = onIrAPuerta
            )
            OpcionPantalla(
                titulo = "Caja",
                detalle = "Marcar quién compró y adjuntar la boleta",
                icono = R.drawable.ic_camara,
                sugerida = turno?.puesto == Turno.PUESTO_CAJA,
                onClick = onIrACaja
            )
        }
    }
}

// Tarjeta grande que se toca. "sugerida" = coincide con su turno: se pinta con el color principal.
@Composable
private fun OpcionPantalla(titulo: String, detalle: String, icono: Int, sugerida: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = if (sugerida) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(Dimens.espacioGrande),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Icon(painterResource(icono), contentDescription = null, modifier = Modifier.size(36.dp))
            Column {
                Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(detalle, style = MaterialTheme.typography.bodyMedium)
                if (sugerida) Text("Según tu turno de hoy", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
