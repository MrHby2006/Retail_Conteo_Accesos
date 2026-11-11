package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.components.SeleccionChips
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.CrearTrabajadorViewModel

@Composable
fun CrearTrabajadorScreen(
    onVolver: () -> Unit,
    viewModel: CrearTrabajadorViewModel = viewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensajeExito) {
        uiState.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
            onVolver()
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Crear Nuevo Trabajador",
                onCerrarSesion = {},
                onVolver = onVolver
            )
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
            Text("Información del Colaborador", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreCambia,
                label = { Text("Nombre Completo") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
                isError = uiState.errorNombre != null,
                supportingText = { uiState.errorNombre?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.rut,
                onValueChange = viewModel::onRutCambia,
                label = { Text("RUT (ej: 12345678-K)") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
                isError = uiState.errorRut != null,
                supportingText = { uiState.errorRut?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.correo,
                onValueChange = viewModel::onCorreoCambia,
                label = { Text("Correo Corporativo") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_correo), contentDescription = null) },
                isError = uiState.errorCorreo != null,
                supportingText = { uiState.errorCorreo?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.contrasena,
                onValueChange = viewModel::onContrasenaCambia,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_candado), contentDescription = null) },
                isError = uiState.errorContrasena != null,
                supportingText = { uiState.errorContrasena?.let { Text(it) } },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.cargo,
                onValueChange = viewModel::onCargoCambia,
                label = { Text("Cargo (ej: Cajero / Observador)") },
                isError = uiState.errorCargo != null,
                supportingText = { uiState.errorCargo?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SeleccionChips(
                titulo = "Rol en el Sistema",
                opciones = uiState.rolesDisponibles,
                seleccionada = uiState.rolSeleccionado,
                onSeleccionar = viewModel::onRolSeleccionado,
                textoDe = { it.second },
                error = uiState.errorRol
            )

            Spacer(Modifier.height(Dimens.espacioChico))

            Boton(
                texto = "Crear Usuario",
                onClick = { viewModel.onCrearUsuario() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}