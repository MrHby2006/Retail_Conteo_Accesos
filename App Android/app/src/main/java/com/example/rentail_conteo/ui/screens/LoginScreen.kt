package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onIngreso: (Usuario) -> Unit,
    onRecuperarPasswordClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
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
            Text("FlujoSur", style = MaterialTheme.typography.headlineLarge)
            Text("Ingresa con tu correo corporativo", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(Dimens.espacioGrande))

            OutlinedTextField(
                value = uiState.correo,
                onValueChange = { viewModel.onCorreoCambia(it) },
                label = { Text("Correo (ej: nombre@rinconsur.cl)") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_correo), contentDescription = null) },
                isError = uiState.errorCorreo != null,
                supportingText = { uiState.errorCorreo?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.contrasena,
                onValueChange = { viewModel.onContrasenaCambia(it) },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_candado), contentDescription = null) },
                isError = uiState.errorContrasena != null,
                supportingText = { uiState.errorContrasena?.let { Text(it) } },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.espacioChico),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = onRecuperarPasswordClick) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(Dimens.espacioMedio))

            Boton(
                texto = "Ingresar",
                onClick = {
                    val usuario = viewModel.onIngresar()
                    if (usuario != null) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Haz iniciado sesión con éxito. Bienbenid@ ${usuario.nombre}",
                                duration = SnackbarDuration.Short
                            )
                            onIngreso(usuario)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
