package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.theme.Dimens

@Composable
fun AdminScreen(
    admin: Usuario,
    onIndicadores: () -> Unit,
    onEmpleados: () -> Unit,
    onHorario: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        topBar = { BarraSuperior(titulo = "Administración", onCerrarSesion = onCerrarSesion) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text("Hola, ${admin.primerNombre()}", style = MaterialTheme.typography.headlineSmall)
            Text("¿Qué quieres revisar?", style = MaterialTheme.typography.bodyMedium)

            OpcionAdmin(
                titulo = "Indicadores y accesos",
                detalle = "Tránsito vs compra por acceso y activar o desactivar puertas",
                icono = R.drawable.ic_persona,
                onClick = onIndicadores
            )
            OpcionAdmin(
                titulo = "Empleados",
                detalle = "Crear cuentas nuevas y desactivar empleados",
                icono = R.drawable.ic_correo,
                onClick = onEmpleados
            )
            OpcionAdmin(
                titulo = "Horario de turnos",
                detalle = "Ver los turnos publicados por el supervisor",
                icono = R.drawable.ic_campana,
                onClick = onHorario
            )
        }
    }
}

@Composable
private fun OpcionAdmin(titulo: String, detalle: String, icono: Int, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Dimens.espacioGrande),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Icon(painterResource(icono), contentDescription = null, modifier = Modifier.size(32.dp))
            Column {
                Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(detalle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
