package com.example.rentail_conteo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentail_conteo.model.FilaIndicador
import com.example.rentail_conteo.ui.components.BarraSuperior
import com.example.rentail_conteo.ui.components.SeleccionChips
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.ui.theme.GrisDeshabilitado
import com.example.rentail_conteo.viewmodel.IndicadoresViewModel

private const val PERIODO_TODO = "Todo el registro"
private const val PERIODO_HOY = "Hoy"

@Composable
fun IndicadoresScreen(
    idAcceso: Int,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: IndicadoresViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(idAcceso) { viewModel.cargar(idAcceso) }

    val progreso = remember { Animatable(0f) }
    LaunchedEffect(uiState.porRango) {
        progreso.snapTo(0f)
        progreso.animateTo(1f, animationSpec = tween(durationMillis = 900))
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Indicadores · ${uiState.acceso?.codigo ?: "Todos"}",
                onCerrarSesion = onCerrarSesion,
                onVolver = onVolver
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text(
                uiState.acceso?.let { "${it.codigo} · ${it.nombre}" } ?: "Todos los accesos",
                style = MaterialTheme.typography.titleMedium
            )
            SeleccionChips(
                titulo = "Período",
                opciones = listOf(PERIODO_TODO, PERIODO_HOY),
                seleccionada = if (uiState.soloHoy) PERIODO_HOY else PERIODO_TODO,
                onSeleccionar = { viewModel.onCambiarPeriodo(it == PERIODO_HOY) },
                textoDe = { it }
            )

            if (uiState.ingresos == 0) {
                Text("No hay ingresos registrados en este período.", style = MaterialTheme.typography.bodyLarge)
                return@Column
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                TarjetaCifra("Entraron", uiState.ingresos, Modifier.weight(1f))
                TarjetaCifra("Compraron", uiState.compradores, Modifier.weight(1f))
                TarjetaCifra("No compraron", uiState.noCompradores, Modifier.weight(1f))
            }

            GraficoConversion(conversion = uiState.conversion, progreso = progreso.value)

            if (uiState.porAcceso.isNotEmpty()) {
                Text("Por acceso", style = MaterialTheme.typography.titleMedium)
                Leyenda()
                val maximoAcceso = uiState.porAcceso.maxOf { it.ingresos }
                uiState.porAcceso.forEach { fila ->
                    BarraRango(fila = fila, maximo = maximoAcceso, progreso = progreso.value)
                }
                uiState.accesoMasDePaso?.let { fila ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(Dimens.espacioMedio),
                            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)
                        ) {
                            Text("Acceso con más gente de paso", style = MaterialTheme.typography.titleMedium)
                            Text(fila.grupo, fontWeight = FontWeight.Bold)
                            Text("${100 - fila.conversion} % de los que entran no compra (${fila.noCompradores} de ${fila.ingresos})")
                        }
                    }
                }
            }

            Text("Por rango de edad", style = MaterialTheme.typography.titleMedium)
            Leyenda()
            val maximo = uiState.porRango.maxOf { it.ingresos }
            uiState.porRango.forEach { fila ->
                BarraRango(fila = fila, maximo = maximo, progreso = progreso.value)
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(Dimens.espacioMedio),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)
                ) {
                    Text("Perfil del público", style = MaterialTheme.typography.titleMedium)
                    Text("Compra más: ${uiState.perfilQueMasCompra ?: "sin compras aún"}")
                    Text("Más gente que NO compra: ${uiState.perfilQueNoCompra ?: "todos compraron"}")
                    Text("Compran solos: ${uiState.conversionSolos} % · acompañados: ${uiState.conversionAcompanados} %")
                }
            }
        }
    }
}

@Composable
private fun TarjetaCifra(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    val animado by animateIntAsState(targetValue = valor, animationSpec = tween(900), label = titulo)
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.espacioChico),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$animado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun GraficoConversion(conversion: Int, progreso: Float) {
    val colorFondo = GrisDeshabilitado
    val colorCompra = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val grosor = 22.dp.toPx()
            val estilo = Stroke(width = grosor, cap = StrokeCap.Round)
            val tamano = Size(size.width - grosor, size.height - grosor)
            val esquina = Offset(grosor / 2, grosor / 2)

            drawArc(colorFondo, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = esquina, size = tamano, style = estilo)
            drawArc(colorCompra, startAngle = -90f, sweepAngle = 360f * conversion / 100f * progreso,
                useCenter = false, topLeft = esquina, size = tamano, style = estilo)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(conversion * progreso).toInt()} %", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            Text("compró", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BarraRango(fila: FilaIndicador, maximo: Int, progreso: Float) {
    val colorIngresos = GrisDeshabilitado
    val colorCompra = MaterialTheme.colorScheme.primary

    Column {
        Text(
            "${fila.grupo}: ${fila.compradores} de ${fila.ingresos} compraron (${fila.conversion} %)",
            style = MaterialTheme.typography.bodyMedium
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
        ) {
            val radio = CornerRadius(6.dp.toPx())
            val anchoIngresos = size.width * fila.ingresos / maximo * progreso
            val anchoCompras = size.width * fila.compradores / maximo * progreso
            drawRoundRect(colorIngresos, size = Size(anchoIngresos, size.height), cornerRadius = radio)
            drawRoundRect(colorCompra, size = Size(anchoCompras, size.height), cornerRadius = radio)
        }
    }
}

@Composable
private fun Leyenda() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Cuadrito(MaterialTheme.colorScheme.primary)
        Text(" Compraron", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.width(Dimens.espacioMedio))
        Cuadrito(GrisDeshabilitado)
        Text(" Entraron", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun Cuadrito(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color)
    )
}
