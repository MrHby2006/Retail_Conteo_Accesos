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
import androidx.compose.material3.LinearProgressIndicator
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
        val estadoCorreo = if (cuenta.correoEnviado) {
            "La contraseña temporal se envió a ${cuenta.correoPersonal}. Solo esa persona puede verla."
        } else {
            "No se pudo enviar el correo a ${cuenta.correoPersonal}. Entrégale en persona esta contraseña temporal: " +
                    cuenta.contrasenaTemporal
        }
        AlertDialog(
            onDismissRequest = viewModel::onCerrarCuentaCreada,
            title = { Text(cuenta.titulo) },
            text = {
                Text(
                    "${cuenta.nombre}\n\nCorreo de empresa: ${cuenta.correoEmpresa}\n\n" +
                            "Ingresa con su RUT y al entrar deberá cambiar la contraseña.\n\n$estadoCorreo"
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
            if (uiState.cargando) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }
            item {
                Text(
                    "${uiState.empleados.size} empleado(s) activos",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = Dimens.espacioMedio)
                )
            }
            items(uiState.empleados, key = { it.id }) { empleado ->
                TarjetaEmpleado(
                    empleado = empleado,
                    onEditar = { viewModel.onEditar(empleado) },
                    onDesactivar = { viewModel.onPedirDesactivar(empleado, admin.id) }
                )
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
        val editando = uiState.enEdicion != null
        Text(if (editando) "Editar empleado" else "Nuevo empleado", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = uiState.nombres,
            onValueChange = viewModel::onNombresCambia,
            label = { Text("Nombres") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
            isError = uiState.errorNombres != null,
            supportingText = { uiState.errorNombres?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.apellidoPaterno,
            onValueChange = viewModel::onApellidoPaternoCambia,
            label = { Text("Apellido paterno") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
            isError = uiState.errorApellidoPaterno != null,
            supportingText = {
                Text(
                    when {
                        uiState.errorApellidoPaterno != null -> uiState.errorApellidoPaterno
                        editando -> "Correo de empresa: ${uiState.enEdicion?.correo}"
                        uiState.correoEmpresaSugerido.isNotBlank() ->
                            "Correo de empresa: ${uiState.correoEmpresaSugerido} (si ya existe se le agrega un número)"
                        else -> ""
                    }
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.apellidoMaterno,
            onValueChange = viewModel::onApellidoMaternoCambia,
            label = { Text("Apellido materno (opcional)") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
            isError = uiState.errorApellidoMaterno != null,
            supportingText = { uiState.errorApellidoMaterno?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.rut,
            onValueChange = viewModel::onRutCambia,
            label = { Text("RUT (ej: 15234871-1)") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
            isError = uiState.errorRut != null,
            supportingText = { Text(uiState.errorRut ?: if (editando) "El RUT no se puede cambiar" else "Con este RUT ingresará a la app") },
            enabled = !editando,
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
            supportingText = { Text(uiState.errorCorreoPersonal ?: "Aquí le llegará su contraseña temporal") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.enEdicion?.rolId != Rol.ADMINISTRADOR) {
            SeleccionChips(
                titulo = "Rol",
                opciones = Rol.asignables,
                seleccionada = uiState.rolElegido,
                onSeleccionar = viewModel::onRolElegido,
                textoDe = { Rol.nombre(it) },
                error = uiState.errorRol
            )
        }
        Boton(
            texto = if (editando) "Guardar cambios" else "Crear cuenta y enviar correo",
            onClick = viewModel::onGuardar,
            modifier = Modifier.fillMaxWidth()
        )
        TextButton(onClick = viewModel::onCancelarFormulario, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
private fun TarjetaEmpleado(
    empleado: Usuario,
    onEditar: () -> Unit,
    onDesactivar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.espacioMedio)) {
            Text(empleado.nombre, fontWeight = FontWeight.Bold)
            Text("${Rol.nombre(empleado.rolId)} · ${empleado.cargo}", style = MaterialTheme.typography.bodySmall)
            Text(empleado.correo, style = MaterialTheme.typography.bodySmall)
            Text(
                empleado.correoPersonal?.let { "Personal: $it" } ?: "Sin correo personal",
                style = MaterialTheme.typography.bodySmall,
                color = if (empleado.correoPersonal == null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
            if (empleado.debeCambiarContrasena) {
                Text(
                    "Aún no cambia su contraseña temporal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEditar) { Text("Editar") }
                if (empleado.rolId != Rol.ADMINISTRADOR) {
                    TextButton(onClick = onDesactivar) { Text("Desactivar") }
                }
            }
        }
    }
}
