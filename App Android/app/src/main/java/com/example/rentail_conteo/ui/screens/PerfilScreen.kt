package com.example.rentail_conteo.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.PerfilUIState
import com.example.rentail_conteo.model.Rol
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.permisos.Permisos
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.util.Fechas
import com.example.rentail_conteo.viewmodel.PerfilViewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    usuario: Usuario,
    onUsuarioActualizado: (Usuario) -> Unit,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(usuario.id) { viewModel.cargar(usuario.id) }

    LaunchedEffect(uiState.usuarioActualizado) {
        uiState.usuarioActualizado?.let {
            onUsuarioActualizado(it)
            viewModel.usuarioActualizadoUsado()
        }
    }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    fun avisarDenegado(mensaje: String) {
        scope.launch {
            val r = snackbarHostState.showSnackbar(mensaje, "Ajustes", duration = SnackbarDuration.Long)
            if (r == SnackbarResult.ActionPerformed) Permisos.abrirAjustes(context)
        }
    }

    val lanzadorCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto: Bitmap? ->
        if (foto != null) viewModel.onFotoTomada(foto)
    }
    fun abrirCamara() {
        try { lanzadorCamara.launch(null) } catch (e: ActivityNotFoundException) { }
    }
    val pedirPermisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) abrirCamara() else avisarDenegado("Sin permiso de cámara no se puede tomar la foto")
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        val uri = resultado.data?.data
        if (resultado.resultCode == Activity.RESULT_OK && uri != null) viewModel.onImagenElegida(uri)
    }
    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try { lanzadorGaleria.launch(intent) } catch (e: ActivityNotFoundException) { }
    }
    val pedirPermisoGaleria = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) abrirGaleria() else avisarDenegado("Sin permiso de fotos no se puede elegir la imagen")
    }

    if (uiState.eligiendoFecha) {
        CalendarioNacimiento(
            fechaActual = uiState.fechaNacimiento,
            onElegir = viewModel::onFechaElegida,
            onCerrar = viewModel::onCerrarCalendario
        )
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Mi perfil", onCerrarSesion = onCerrarSesion, onVolver = onVolver) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val actual = uiState.usuario ?: usuario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FotoPerfil(fotoUrl = uiState.fotoUrl, nombre = actual.nombre)
            if (uiState.subiendoFoto) CircularProgressIndicator()
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                OutlinedButton(onClick = {
                    if (Permisos.tiene(context, Permisos.CAMARA)) abrirCamara() else pedirPermisoCamara.launch(Permisos.CAMARA)
                }) {
                    Icon(painterResource(R.drawable.ic_camara), contentDescription = null)
                    Spacer(Modifier.width(Dimens.espacioChico))
                    Text("Cámara")
                }
                OutlinedButton(onClick = {
                    if (Permisos.tiene(context, Permisos.IMAGENES)) abrirGaleria() else pedirPermisoGaleria.launch(Permisos.IMAGENES)
                }) {
                    Icon(painterResource(R.drawable.ic_galeria), contentDescription = null)
                    Spacer(Modifier.width(Dimens.espacioChico))
                    Text("Galería")
                }
            }
            uiState.errorFoto?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            if (actual.esCumpleanosHoy()) {
                Text(
                    "¡Feliz cumpleaños, ${actual.primerNombre()}!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            DatosFijos(actual)
            DatosPersonales(uiState, viewModel)
            HorizontalDivider()
            CambioContrasena(uiState, viewModel)
        }
    }
}

@Composable
private fun FotoPerfil(fotoUrl: String?, nombre: String) {
    if (fotoUrl != null) {
        AsyncImage(
            model = fotoUrl,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
    } else {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    nombre.take(1).uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun DatosFijos(usuario: Usuario) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.espacioMedio), verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)) {
            Text(usuario.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${Rol.nombre(usuario.rolId)} · ${usuario.cargo}", style = MaterialTheme.typography.bodyMedium)
            Text("RUT: ${usuario.rutFormateado()}", style = MaterialTheme.typography.bodySmall)
            Text("Correo de la empresa: ${usuario.correo}", style = MaterialTheme.typography.bodySmall)
            Text(
                "Estos datos solo los puede cambiar el administrador.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun DatosPersonales(uiState: PerfilUIState, viewModel: PerfilViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
        Text("Mis datos", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = uiState.correoPersonal,
            onValueChange = viewModel::onCorreoCambia,
            label = { Text("Correo personal") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_correo), contentDescription = null) },
            isError = uiState.errorCorreo != null,
            supportingText = { Text(uiState.errorCorreo ?: "Aquí te llegan los horarios y el código para recuperar la contraseña") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(onClick = viewModel::onAbrirCalendario, modifier = Modifier.fillMaxWidth()) {
            Icon(painterResource(R.drawable.ic_campana), contentDescription = null)
            Spacer(Modifier.width(Dimens.espacioChico))
            Text(
                uiState.fechaNacimiento?.let { "Cumpleaños: ${Fechas.legible(it)}" } ?: "Agregar fecha de nacimiento"
            )
        }
        uiState.errorFecha?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Boton(texto = "Guardar mis datos", onClick = viewModel::onGuardarDatos, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun CambioContrasena(uiState: PerfilUIState, viewModel: PerfilViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
        Text("Cambiar contraseña", style = MaterialTheme.typography.titleMedium)
        CampoClave("Contraseña actual", uiState.actual, uiState.errorActual, viewModel::onActualCambia)
        CampoClave("Nueva contraseña", uiState.nueva, uiState.errorNueva, viewModel::onNuevaCambia)
        CampoClave("Repite la nueva contraseña", uiState.confirmar, uiState.errorConfirmar, viewModel::onConfirmarCambia)
        Boton(texto = "Cambiar contraseña", onClick = viewModel::onCambiarContrasena, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun CampoClave(etiqueta: String, valor: String, error: String?, onCambia: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambia,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(painterResource(R.drawable.ic_candado), contentDescription = null) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarioNacimiento(fechaActual: String?, onElegir: (Long?) -> Unit, onCerrar: () -> Unit) {
    val estado = rememberDatePickerState(
        initialSelectedDateMillis = fechaActual?.let { Fechas.aMillisUtc(it) },
        yearRange = (Fechas.hoy().take(4).toInt() - PerfilViewModel.EDAD_MAXIMA)..Fechas.hoy().take(4).toInt()
    )
    DatePickerDialog(
        onDismissRequest = onCerrar,
        confirmButton = { TextButton(onClick = { onElegir(estado.selectedDateMillis) }) { Text("Aceptar") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    ) {
        DatePicker(state = estado)
    }
}
