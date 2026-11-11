package com.example.rentail_conteo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.viewmodel.CambiarContrasenaViewModel

@Composable
fun CambiarContrasenaScreen(
    usuario: Usuario,
    onListo: (Usuario) -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: CambiarContrasenaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.usuarioActualizado) {
        uiState.usuarioActualizado?.let(onListo)
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Crea tu contraseña", onCerrarSesion = onCerrarSesion) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text("Hola, ${usuario.primerNombre()}", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Entraste con una contraseña temporal. Antes de seguir, crea una propia: " +
                        "mínimo 8 caracteres, con letras y números.",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = uiState.nueva,
                onValueChange = viewModel::onNuevaCambia,
                label = { Text("Nueva contraseña") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_candado), contentDescription = null) },
                isError = uiState.errorNueva != null,
                supportingText = { uiState.errorNueva?.let { Text(it) } },
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
            Boton(
                texto = "Guardar contraseña",
                onClick = { viewModel.onGuardar(usuario.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
