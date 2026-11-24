package com.example.rentail_conteo.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.RecuperarContrasenaUIState
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.RecuperarContrasenaViewModel

@Composable
fun RecuperarContrasenaScreen(
    onVolver: () -> Unit,
    onListo: () -> Unit,
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    LaunchedEffect(uiState.listo) {
        if (uiState.listo) {
            snackbarHostState.showSnackbar("Contraseña cambiada. Ya puedes ingresar con ella")
            onListo()
        }
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Recuperar contraseña", onVolver = onVolver) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = uiState.paso,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "pasoRecuperar",
            modifier = Modifier.padding(innerPadding)
        ) { paso ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.espacioPantalla),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (paso == 1) PasoPedirCodigo(uiState, viewModel) else PasoNuevaContrasena(uiState, viewModel)
            }
        }
    }
}

@Composable
private fun PasoPedirCodigo(uiState: RecuperarContrasenaUIState, viewModel: RecuperarContrasenaViewModel) {
    Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.titleLarge)
    Text(
        "Escribe tu RUT. Te enviaremos un código de 6 números a tu correo de la empresa.",
        style = MaterialTheme.typography.bodyMedium
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
    if (uiState.enviando) {
        CircularProgressIndicator()
        Text("Enviando el correo...", style = MaterialTheme.typography.bodySmall)
    } else {
        Boton(texto = "Enviar código", onClick = viewModel::onEnviarCodigo, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun PasoNuevaContrasena(uiState: RecuperarContrasenaUIState, viewModel: RecuperarContrasenaViewModel) {
    Text("Revisa tu correo", style = MaterialTheme.typography.titleLarge)
    Text(
        "Enviamos el código a ${uiState.correoOculto}. Vence en ${RecuperarContrasenaViewModel.MINUTOS_VALIDEZ} minutos.",
        style = MaterialTheme.typography.bodyMedium
    )
    OutlinedTextField(
        value = uiState.codigo,
        onValueChange = viewModel::onCodigoCambia,
        label = { Text("Código de 6 números") },
        leadingIcon = { Icon(painterResource(R.drawable.ic_correo), contentDescription = null) },
        isError = uiState.errorCodigo != null,
        supportingText = { uiState.errorCodigo?.let { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = uiState.nueva,
        onValueChange = viewModel::onNuevaCambia,
        label = { Text("Nueva contraseña") },
        leadingIcon = { Icon(painterResource(R.drawable.ic_candado), contentDescription = null) },
        isError = uiState.errorNueva != null,
        supportingText = { Text(uiState.errorNueva ?: "Mínimo 8 caracteres, con letras y números") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = uiState.confirmar,
        onValueChange = viewModel::onConfirmarCambia,
        label = { Text("Repite la contraseña") },
        leadingIcon = { Icon(painterResource(R.drawable.ic_candado), contentDescription = null) },
        isError = uiState.errorConfirmar != null,
        supportingText = { uiState.errorConfirmar?.let { Text(it) } },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth()
    )
    Boton(texto = "Cambiar contraseña", onClick = viewModel::onCambiarContrasena, modifier = Modifier.fillMaxWidth())
    TextButton(onClick = viewModel::onVolverAlPaso1) { Text("No me llegó, pedir otro código") }
}
