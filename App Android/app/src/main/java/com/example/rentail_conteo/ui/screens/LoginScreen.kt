package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.LoginViewModel

// Pantalla de ingreso. Solo DIBUJA el estado y AVISA al ViewModel.
// No recibe el NavController: avisa con onIngresoExitoso y la navegación decide.
@Composable
fun LoginScreen(
    onIngresoExitoso: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("FlujoSur", style = MaterialTheme.typography.headlineLarge)
            Text("Ingresa con tu cuenta de colaborador", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(Dimens.espacioGrande))

            // Campo RUT: ícono + borde rojo y mensaje si hay error
            OutlinedTextField(
                value = uiState.rut,
                onValueChange = { viewModel.onRutCambia(it) },
                label = { Text("RUT (ej: 15234871-1)") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_persona), contentDescription = null) },
                isError = uiState.errorRut != null,
                supportingText = { uiState.errorRut?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo contraseña: se ve con puntitos
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

            Spacer(Modifier.height(Dimens.espacioMedio))

            // Botón de Harol (Boton.kt). Si el ViewModel dice "válido", avisamos.
            Boton(
                texto = "Ingresar",
                onClick = { if (viewModel.onIngresar()) onIngresoExitoso() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
