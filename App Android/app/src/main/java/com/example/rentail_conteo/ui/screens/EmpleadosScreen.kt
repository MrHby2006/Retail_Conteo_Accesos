package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.EmpleadosUIState
import com.example.rentail_conteo.model.Rol
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.components.SeleccionChips
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.EmpleadosViewModel

@Composable
fun EmpleadosScreen(
    admin: Usuario,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: EmpleadosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    uiState.cuentaCreada?.let { cuenta ->
        AlertDialog(
            onDismissRequest = viewModel::onCerrarCuentaCreada,
            title = { Text("Cuenta creada") },
            text = {
                Text(
                    "${cuenta.nombre}\n\nCorreo de empresa: ${cuenta.correoEmpresa}\n" +
                            "Contraseña temporal: ${cuenta.contrasenaTemporal}\n\n" +
                            "Al entrar por primera vez deberá cambiar la contraseña."
                )
            },
            confirmButton = { TextButton(onClick = viewModel::onCerrarCuentaCreada) { Text("Entendido") } }
        )
    }

    uiState.porDesactivar?.let { empleado ->
        AlertDialog(
            onDismissRequest = viewModel::onCancelarDesactivar,
            title = { Text("Desactivar empleado") },
            text = { Text("${empleado.nombre} ya no podrá ingresar a la app. Sus registros anteriores se mantienen.") },
            confirmButton = { TextButton(onClick = viewModel::onConfirmarDesactivar) { Text("Desactivar") } },
            dismissButton = { TextButton(onClick = viewModel::onCancelarDesactivar) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Empleados", onCerrarSesion = onCerrarSesion, onVolver = onVolver) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            item {
                if (uiState.mostrandoFormulario) {
                    FormularioEmpleado(uiState, viewModel)
                } else {
                    Boton(
                        texto = "Nuevo empleado",
                        onClick = viewModel::onMostrarFormulario,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.espacioMedio)
                    )
                }
            }
            item {
                Text(
                    "${uiState.empleados.size} empleado(s) activos",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = Dimens.espacioMedio)
                )
            }
            items(uiState.empleados, key = { it.id }) { empleado ->
                TarjetaEmpleado(empleado = empleado, onDesactivar = { viewModel.onPedirDesactivar(empleado, admin.id) })
            }
        }
    }
}

@Composable
private fun FormularioEmpleado(uiState: EmpleadosUIState, viewModel: EmpleadosViewModel) {
    Column(
        modifier = Modifier.padding(top = Dimens.espacioMedio),
        verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
    ) {
        Text("Nuevo empleado", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = viewModel::onNombreCambia,
            label = { Text("Nombre completo") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
            isError = uiState.errorNombre != null,
            supportingText = {
                Text(
                    when {
                        uiState.errorNombre != null -> uiState.errorNombre
                        uiState.correoEmpresaSugerido.isNotBlank() -> "Correo de empresa: ${uiState.correoEmpresaSugerido}"
                        else -> "Nombre y apellido"
                    }
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.rut,
            onValueChange = viewModel::onRutCambia,
            label = { Text("RUT (ej: 15234871-1)") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
            isError = uiState.errorRut != null,
            supportingText = { uiState.errorRut?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.cargo,
            onValueChange = viewModel::onCargoCambia,
            label = { Text("Cargo (ej: Vendedor, Deportes)") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
            isError = uiState.errorCargo != null,
            supportingText = { uiState.errorCargo?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.correoPersonal,
            onValueChange = viewModel::onCorreoPersonalCambia,
            label = { Text("Correo personal") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_correo), contentDescription = null) },
            isError = uiState.errorCorreoPersonal != null,
            supportingText = { Text(uiState.errorCorreoPersonal ?: "Aquí le llegarán sus datos y horarios") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        SeleccionChips(
            titulo = "Rol",
            opciones = Rol.asignables,
            seleccionada = uiState.rolElegido,
            onSeleccionar = viewModel::onRolElegido,
            textoDe = { Rol.nombre(it) },
            error = uiState.errorRol
        )
        Boton(texto = "Crear cuenta", onClick = viewModel::onGuardar, modifier = Modifier.fillMaxWidth())
        TextButton(onClick = viewModel::onCancelarFormulario, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
private fun TarjetaEmpleado(empleado: Usuario, onDesactivar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(empleado.nombre, fontWeight = FontWeight.Bold)
                Text("${Rol.nombre(empleado.rolId)} · ${empleado.cargo}", style = MaterialTheme.typography.bodySmall)
                Text(empleado.correo, style = MaterialTheme.typography.bodySmall)
                if (empleado.debeCambiarContrasena) {
                    Text(
                        "Aún no cambia su contraseña temporal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (empleado.rolId != Rol.ADMINISTRADOR) {
                TextButton(onClick = onDesactivar) { Text("Desactivar") }
            }
        }
    }
}
