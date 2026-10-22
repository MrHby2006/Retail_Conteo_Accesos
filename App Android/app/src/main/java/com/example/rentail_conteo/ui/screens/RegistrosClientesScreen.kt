package com.example.rentail_conteo.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.R
import com.example.rentail_conteo.model.Opciones
import com.example.rentail_conteo.model.PersonaIngreso
import com.example.rentail_conteo.model.RegistrosClientesUIState
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.permisos.Permisos
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.Boton
import com.example.rentail_conteo.ui.components.CampoCorreo
import com.example.rentail_conteo.ui.components.CampoNombre
import com.example.rentail_conteo.ui.components.SeleccionChips
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.util.Fechas
import com.example.rentail_conteo.viewmodel.RegistrosClientesViewModel
import kotlinx.coroutines.launch

// Pantalla del CAJERO.
//  - Modo buscar: filtra a las personas que entraron y aún no compran, y toca a la que está pagando.
//  - Modo confirmar: datos opcionales del cliente + FOTO DE LA BOLETA (recurso nativo: cámara / galería).
@Composable
fun RegistrosClientesScreen(
    cajero: Usuario,
    onCerrarSesion: () -> Unit,
    viewModel: RegistrosClientesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    // PERMISOS paso 4 (rechazado): mensaje con botón "Ajustes"
    fun avisarDenegado(mensaje: String) {
        scope.launch {
            val r = snackbarHostState.showSnackbar(mensaje, "Ajustes", duration = SnackbarDuration.Long)
            if (r == SnackbarResult.ActionPerformed) Permisos.abrirAjustes(context)
        }
    }

    // ---------- CÁMARA ----------
    // TakePicturePreview abre la cámara y devuelve la foto como Bitmap (null si se canceló)
    val lanzadorCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto: Bitmap? ->
        if (foto != null) viewModel.onFotoTomada(foto)
    }
    fun abrirCamara() {
        try { lanzadorCamara.launch(null) } catch (e: ActivityNotFoundException) { }
    }
    // PERMISOS paso 3: pedir. El resultado llega a esta lambda (ok = true si aceptó)
    val pedirPermisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) abrirCamara() else avisarDenegado("Sin permiso de cámara no se puede fotografiar la boleta")
    }

    // ---------- GALERÍA ----------
    val lanzadorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        val uri = resultado.data?.data
        if (resultado.resultCode == Activity.RESULT_OK && uri != null) viewModel.onImagenElegida(uri)
    }
    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try { lanzadorGaleria.launch(intent) } catch (e: ActivityNotFoundException) { }
    }
    val pedirPermisoGaleria = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) abrirGaleria() else avisarDenegado("Sin permiso de fotos no se puede elegir la boleta")
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Caja · ${cajero.primerNombre()}", onCerrarSesion = onCerrarSesion) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // ANIMACIÓN: al elegir una persona, la búsqueda se desvanece y aparece el formulario (y al revés)
        AnimatedContent(
            targetState = uiState.personaElegida,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "modoCaja",
            modifier = Modifier.padding(innerPadding)
        ) { persona ->
            if (persona == null) {
                BuscarComprador(uiState = uiState, viewModel = viewModel)
            } else {
                ConfirmarCompra(
                    persona = persona,
                    uiState = uiState,
                    viewModel = viewModel,
                    // PERMISOS paso 2: verificar. Si ya está concedido se abre directo; si no, se pide.
                    onCamara = {
                        if (Permisos.tiene(context, Permisos.CAMARA)) abrirCamara()
                        else pedirPermisoCamara.launch(Permisos.CAMARA)
                    },
                    onGaleria = {
                        if (Permisos.tiene(context, Permisos.IMAGENES)) abrirGaleria()
                        else pedirPermisoGaleria.launch(Permisos.IMAGENES)
                    },
                    onGuardar = { viewModel.onGuardar(cajero.id) }
                )
            }
        }
    }
}

// ----------------------------- MODO BUSCAR -----------------------------

@Composable
private fun BuscarComprador(uiState: RegistrosClientesUIState, viewModel: RegistrosClientesViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.espacioPantalla),
        verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
    ) {
        item {
            Text("¿Quién está comprando?", style = MaterialTheme.typography.titleLarge)
            Text(
                "Filtra por lo que ves y toca a la persona. Toca un filtro otra vez para quitarlo.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {
            SeleccionChips(
                titulo = "Entró por",
                opciones = uiState.accesos,
                seleccionada = uiState.filtroAcceso,
                onSeleccionar = viewModel::onFiltroAcceso,
                textoDe = { it.codigo }
            )
        }
        item {
            SeleccionChips(
                titulo = "Rango de edad",
                opciones = Opciones.rangosEtarios,
                seleccionada = uiState.filtroRango,
                onSeleccionar = viewModel::onFiltroRango,
                textoDe = { it }
            )
        }
        item {
            SeleccionChips(
                titulo = "Color de la ropa",
                opciones = Opciones.coloresRopa,
                seleccionada = uiState.filtroColor,
                onSeleccionar = viewModel::onFiltroColor,
                textoDe = { it }
            )
        }
        item {
            Text(
                "${uiState.candidatos.size} persona(s) sin compra",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = Dimens.espacioChico)
            )
        }
        // key = id: Compose sabe qué tarjeta es cuál. ANIMACIÓN: animateItem() las desliza al filtrar.
        items(uiState.candidatos, key = { it.id }) { persona ->
            TarjetaPersona(
                persona = persona,
                onClick = { viewModel.onElegirPersona(persona) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun TarjetaPersona(persona: PersonaIngreso, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(Dimens.espacioMedio)) {
            Text(
                "${persona.accesoCodigo} · ${Fechas.hora(persona.fechaHora)}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(persona.resumen(), style = MaterialTheme.typography.bodyMedium)
            if (persona.detalle.isNotBlank()) {
                Text("\"${persona.detalle}\"", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// ----------------------------- MODO CONFIRMAR -----------------------------

@Composable
private fun ConfirmarCompra(
    persona: PersonaIngreso,
    uiState: RegistrosClientesUIState,
    viewModel: RegistrosClientesViewModel,
    onCamara: () -> Unit,
    onGaleria: () -> Unit,
    onGuardar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.espacioPantalla),
        verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
    ) {
        Text("Confirmar compra", style = MaterialTheme.typography.titleLarge)
        TarjetaPersona(persona = persona, onClick = {})
        TextButton(onClick = viewModel::onCancelar) { Text("Cambiar persona") }

        // Datos OPCIONALES (componentes de Harol): solo si el cliente quiere darlos
        CampoNombre(
            valor = uiState.nombreCliente,
            onValorCambia = viewModel::onNombreCambia,
            etiqueta = "Nombre del cliente (opcional)",
            icono = R.drawable.ic_persona,
            error = uiState.errorNombre
        )
        CampoCorreo(
            valor = uiState.correoCliente,
            onValorCambia = viewModel::onCorreoCambia,
            etiqueta = "Correo del cliente (opcional)",
            icono = R.drawable.ic_correo,
            error = uiState.errorCorreo,
            tipoTeclado = KeyboardType.Email
        )

        // Foto de la boleta (obligatoria)
        Text("Foto de la boleta", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
            OutlinedButton(onClick = onCamara) {
                Icon(painterResource(R.drawable.ic_camara), contentDescription = null)
                Spacer(Modifier.width(Dimens.espacioChico))
                Text("Cámara")
            }
            OutlinedButton(onClick = onGaleria) {
                Icon(painterResource(R.drawable.ic_galeria), contentDescription = null)
                Spacer(Modifier.width(Dimens.espacioChico))
                Text("Galería")
            }
        }
        // ANIMACIÓN: la vista previa aparece creciendo cuando llega la foto
        AnimatedVisibility(visible = uiState.fotoBoleta != null, enter = scaleIn() + fadeIn()) {
            uiState.fotoBoleta?.let { foto ->
                Image(
                    bitmap = foto.asImageBitmap(),
                    contentDescription = "Foto de la boleta",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(Dimens.radioCampo))
                )
            }
        }
        uiState.errorFoto?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(Dimens.espacioChico))
        Boton(texto = "Guardar compra", onClick = onGuardar, modifier = Modifier.fillMaxWidth())
    }
}
