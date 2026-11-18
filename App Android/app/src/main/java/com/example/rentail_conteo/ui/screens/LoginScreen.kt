package com.example.rentail_conteo.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.permisos.Permisos
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onIngreso: (Usuario) -> Unit,
    onRecuperarPasswordClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pedirPermisoRedLocal = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Permisos.necesitaRedLocal() && !Permisos.tiene(context, Permisos.RED_LOCAL)) {
            pedirPermisoRedLocal.launch(Permisos.RED_LOCAL)
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo FlujoSur",
                modifier = Modifier
                    .height(80.dp)
                    .padding(bottom = Dimens.espacioChico)
            )
            Text("FlujoSur", style = MaterialTheme.typography.headlineLarge)
            Text("Ingresa con tu RUT y tu contraseña", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(Dimens.espacioGrande))

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

            if (uiState.cargando) {
                CircularProgressIndicator()
            } else {
                Boton(
                    texto = "Ingresar",
                    onClick = { viewModel.onIngresar(onIngreso) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
