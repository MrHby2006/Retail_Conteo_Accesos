package com.example.rentail_conteo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rentail_conteo.ui.styles.EstiloBoton
import com.example.rentail_conteo.ui.styles.estiloAltoBoton
import com.example.rentail_conteo.ui.theme.Rentail_ConteoTheme

@Composable
fun Boton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.estiloAltoBoton(),
        shape = EstiloBoton.forma,
        colors = EstiloBoton.coloresPrincipales(),
        border = EstiloBoton.borde(),
        contentPadding = EstiloBoton.relleno
    ) {
        Text(text = texto)
    }
}

@Preview(showBackground = true)
@Composable
private fun BotonPreview() {
    Rentail_ConteoTheme {
        Boton(texto = "Ver información de clientes", onClick = {}, modifier = Modifier.padding(16.dp))
    }
}