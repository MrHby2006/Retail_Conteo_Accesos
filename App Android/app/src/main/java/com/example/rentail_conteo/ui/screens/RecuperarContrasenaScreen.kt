package com.example.rentail_conteo.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.RecuperarContrasenaViewModel

@Composable
fun RecuperarContrasenaScreen(
    onVolver: () -> Unit,
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensajeExito) {
        if (uiState.mensajeExito) {
            snackbarHostState.showSnackbar("Correo verificado. Redirigiendo a Gmail...")
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_EMAIL)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                val intentWeb = Intent(Intent.ACTION_SENDTO).apply {
                    data = android.net.Uri.parse("mailto:")
                }
                context.startActivity(intentWeb)
            }
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Recuperar Contraseña",
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
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¿Problemas para iniciar sesión?", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Dimens.espacioChico))
            Text(
                "Ingresa tu correo electrónico registrado y te enviaremos las instrucciones.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(Dimens.espacioGrande))

            OutlinedTextField(
                value = uiState.correo,
                onValueChange = { viewModel.onCorreoCambia(it) },
                label = { Text("Ingresa tu correo") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_correo), contentDescription = null) },
                isError = uiState.errorCorreo != null,
                supportingText = { uiState.errorCorreo?.let { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Dimens.espacioMedio))

            Boton(
                texto = "Enviar instrucciones",
                onClick = { viewModel.validarYEnviar() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}